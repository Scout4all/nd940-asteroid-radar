/*
 * Copyright (c) 2023.
 *
 *  Developed by : Bigad Aboubakr
 *  Developer website : http://bigad.me
 *  Developer github : https://github.com/Scout4all
 *  Developer Email : bigad@bigad.me
 */

package me.bigad.asteroidradar.ui.main


import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import me.bigad.asteroidradar.R
import me.bigad.asteroidradar.databinding.FragmentMainBinding
import me.bigad.asteroidradar.ui.detail.DetailFragment
import timber.log.Timber


class MainFragment : Fragment(), MenuProvider {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        val isTablet = context?.resources?.getBoolean(R.bool.isTablet)
        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)

        binding.asteroidRecycler.adapter = MainFragmentRecyclerAdapter(OnClickListener { asteroid ->
            when (isTablet) {
                true -> {
                    val bundle = Bundle()
                    bundle.putParcelable(DetailFragment.selectedAsteroid, asteroid)
                    binding.itemDetailNavContainer?.findNavController()
                        ?.navigate(R.id.detailFragmentControlled, bundle,navBuilder.build())
                }
                else -> {
                    this.findNavController()
                        .navigate(MainFragmentDirections.actionShowDetail(asteroid))

                }
            }

        }, isTablet)
        viewModel.status.observe(viewLifecycleOwner, Observer {
            Timber.e(it.toString())
        })

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_overflow_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.show_today_asteroids_menu -> {
        viewModel.searchForToday()
                return true
            }
            R.id.show_all_menu->{
                viewModel.showWeekAsteroids()
            }
            R.id.show_saved_asteroids_menu->{
                viewModel.loadOnlineData()
            }
        }
        return false
    }
}
