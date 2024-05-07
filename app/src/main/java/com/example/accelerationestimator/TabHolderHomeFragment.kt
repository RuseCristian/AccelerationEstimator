package com.example.accelerationestimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.accelerationestimator.ui.*
import com.google.android.material.tabs.TabLayout


class TabHolderHomeFragment : Fragment() {
    private lateinit var tabViewModel: TabViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_tab_holder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewStateAdapter = ViewStateAdapter(this)

        tabViewModel = ViewModelProvider(requireActivity()).get(TabViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        tabViewModel.tabFragments.apply {
            add(HomeFirstTabFragment())
            add(HomeSecondTabFragment())
            add(HomeThirdTabFragment())
            add(HomeFourthTabFragment())
            add(HomeFifthTabFragment())
        }

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager2)
        viewPager.apply {
            offscreenPageLimit = tabViewModel.tabFragments.size - 1 // Set offscreenPageLimit
            adapter = viewStateAdapter
        }

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                tabLayout.getTabAt(tab.position)?.select();
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager.currentItem = tabViewModel.selectedTabIndex

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabViewModel.selectedTabIndex = position
                // Set the FragmentManager in the Utils
                setFragmentManager(childFragmentManager)
                tabLayout.getTabAt(position)?.select();
            }
        })



    }


    private inner class ViewStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFirstTabFragment()
                1 -> HomeSecondTabFragment()
                2 -> HomeThirdTabFragment()
                3 -> HomeFourthTabFragment()
                else -> HomeFifthTabFragment()
            }
        }

        override fun getItemCount(): Int {
            return 5
        }
    }
}