package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.getFormattedDate
import java.util.*

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var adapter: AsteroidAdapter
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        adapter = AsteroidAdapter(AsteroidClick {
            val action = MainFragmentDirections.actionShowDetail(it)
            findNavController().navigate(action)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.asteroids.observe(viewLifecycleOwner, { asteroids ->
            asteroids?.let {
                updateList(asteroids, viewModel.filter.value)
            }
            if (asteroids?.isNullOrEmpty() == true) {
                binding.statusLoadingWheel.visibility = VISIBLE
            } else {
                binding.statusLoadingWheel.visibility = GONE
            }
        })

        viewModel.pictureOfDay.observe(viewLifecycleOwner, { picture ->
            if (picture?.isNotEmpty() == true) {
                val pic = picture[0]
                if (pic.mediaType == "image") {
                    binding.activityMainImageOfTheDayLayout.visibility = VISIBLE
                    Picasso.with(context).load(pic.url).into(binding.activityMainImageOfTheDay)
                    binding.activityMainImageOfTheDay.contentDescription = getString(
                        R.string.nasa_picture_of_day_content_description_format,
                        pic.title
                    )
                } else {
                    binding.activityMainImageOfTheDayLayout.visibility = GONE
                }
            } else {
                binding.activityMainImageOfTheDayLayout.visibility = GONE
            }
        })

        viewModel.filter.observe(viewLifecycleOwner, { filter ->
            updateList(viewModel.asteroids.value, filter)
        })

        viewModel.error.observe(viewLifecycleOwner, { error ->
            if (error) {
                Snackbar.make(binding.root, R.string.no_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.refresh) {
                        viewModel.fetchData()
                    }.show()
                viewModel.finishedShowingError()
            }
        })

        activity?.setTitle(R.string.app_name)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> {
                viewModel.updateFilter(MainViewModel.Filter.WEEK)
            }
            R.id.show_today_menu -> {
                viewModel.updateFilter(MainViewModel.Filter.TODAY)
            }
            R.id.show_saved_menu -> {
                viewModel.updateFilter(MainViewModel.Filter.SAVED)
            }
        }
        return true
    }

    private fun updateList(
        asteroids: List<Asteroid>?,
        filter: MainViewModel.Filter? = MainViewModel.Filter.WEEK
    ) {
        val filtered = when (filter) {
            MainViewModel.Filter.WEEK -> asteroids
            MainViewModel.Filter.TODAY -> asteroids?.filter { asteroid -> asteroid.closeApproachDate == Calendar.getInstance().time.getFormattedDate() }
            MainViewModel.Filter.SAVED -> asteroids?.filter { asteroid -> asteroid.saved }
            else -> asteroids
        }
        if (filtered?.isEmpty() == true) {
            binding.asteroidRecycler.visibility = GONE
            binding.textNoSavedAsteroids.visibility = VISIBLE
        } else {
            binding.asteroidRecycler.visibility = VISIBLE
            binding.textNoSavedAsteroids.visibility = GONE
        }
        adapter.submitList(filtered)
    }
}

class AsteroidClick(val block: (Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = block(asteroid)
}

class AsteroidAdapter(val callback: AsteroidClick) :
    ListAdapter<Asteroid, AsteroidViewHolder>(AsteroidDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.also {
            it.asteroid = item
            it.callback = callback
        }
    }

    class AsteroidDiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }

    }
}

class AsteroidViewHolder(val binding: AsteroidItemBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        @LayoutRes
        const val LAYOUT = R.layout.asteroid_item

        fun from(parent: ViewGroup): AsteroidViewHolder {
            val binding: AsteroidItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), LAYOUT, parent, false)

            return AsteroidViewHolder(binding)
        }
    }
}