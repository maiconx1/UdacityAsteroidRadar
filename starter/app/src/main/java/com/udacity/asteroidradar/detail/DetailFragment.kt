package com.udacity.asteroidradar.detail


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentDetailBinding
import com.udacity.asteroidradar.domain.Asteroid

class DetailFragment : Fragment() {

    private lateinit var menu: Menu

    private lateinit var asteroid: Asteroid

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid

        binding.asteroid = asteroid

        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        setHasOptionsMenu(true)

        activity?.title = asteroid.codename

        return binding.root
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomical_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_save_menu, menu)
        this.menu = menu
        setMenuIcon(asteroid.saved)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save_asteroid -> {
                asteroid = asteroid.copy(saved = !asteroid.saved)
                viewModel.updateAsteroid(asteroid)
                setMenuIcon(asteroid.saved)
            }
            android.R.id.home -> findNavController().navigateUp()
        }
        return true
    }

    private fun setMenuIcon(saved: Boolean) {
        if (saved) {
            menu.findItem(R.id.menu_save_asteroid).setIcon(R.drawable.ic_delete)
        } else {
            menu.findItem(R.id.menu_save_asteroid).setIcon(R.drawable.ic_save)
        }
    }
}
