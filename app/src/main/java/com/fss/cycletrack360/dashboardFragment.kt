package com.fss.cycletrack360

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView

class dashboardFragment : Fragment() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        bottomNav = rootView.findViewById(R.id.bottom_nav)

        // Set up the bottom navigation using the new method
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.walkingFragment -> {
                    // Load Walking fragment
                    loadTabFragment("Walking")
                    true
                }
                R.id.runningFragment -> {
                    // Load Running fragment
                    loadTabFragment("Running")
                    true
                }
                R.id.cyclingFragment -> {
                    // Load Cycling fragment
                    loadTabFragment("Cycling")
                    true
                }
                else -> false
            }
        }

        // Load the Walking fragment by default when the dashboard is first created
        loadTabFragment("Walking")

        return rootView
    }

    private fun loadTabFragment(activityType: String) {
        // Create a new instance of TabFragment and pass the activity type
        val tabFragment = TabFragment()
        val bundle = Bundle()
        bundle.putString("activity_type", activityType) // Pass the activity type
        tabFragment.arguments = bundle

        // Replace the current fragment inside the container
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, tabFragment)
        transaction.commit()
    }
}
