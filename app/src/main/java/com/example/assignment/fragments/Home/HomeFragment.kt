package com.example.assignment.fragments.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.assignment.R
import com.example.assignment.adapters.HomeViewpagerAdapter
import com.example.assignment.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator




class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
       return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryFragments = arrayListOf<Fragment>(
            HomeCategoryFragment(),
            ClothesFragment(),
            ElectronicFragment(),
            FurnitureFragment()

        )

        val viewpager2Adapter = HomeViewpagerAdapter(categoryFragments,childFragmentManager,lifecycle)
        binding.viewPagerHome.adapter = viewpager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPagerHome){ tab: TabLayout.Tab, position: Int ->
            when(position){
                0 -> tab.text = "Home"
                1 -> tab.text = "Clothes"
                2 -> tab.text = "Electronics"
                3 -> tab.text = "Furniture"
            }
        }.attach()

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.sellItemFragment)
        }

        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_bottom)  // Enter from bottom
            .setExitAnim(R.anim.slide_out_top)     // Exit to top
            .setPopEnterAnim(R.anim.slide_in_top)  // Enter from top (on back pressed)
            .setPopExitAnim(R.anim.slide_out_bottom)  // Exit to bottom (on back pressed)
            .build()

        binding.searchView.setOnSearchClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment, null, options)
        }

    }


}