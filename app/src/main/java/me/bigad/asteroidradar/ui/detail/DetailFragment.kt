package me.bigad.asteroidradar.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.bigad.asteroidradar.R
import me.bigad.asteroidradar.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        if (getArguments() != null) {
            val args = DetailFragmentArgs.fromBundle(requireArguments())


            if ( args.selectedAsteroid != null) {
                val asteroid =args.selectedAsteroid
                binding.asteroid = asteroid
                binding.asteridDataContainer?.visibility = View.VISIBLE
                binding.noAsterid?.visibility = View.GONE
            } else {
                binding.asteridDataContainer?.visibility = View.GONE
                binding.noAsterid?.visibility = View.VISIBLE
            }
        }




        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        return binding.root
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomica_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }

    companion object {
        val selectedAsteroid: String = "selectedAsteroid"
    }
}
