package com.fss.cycletrack360

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class TabFragment : Fragment() {

    private var activityType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the activity type passed from the DashboardFragment
        activityType = arguments?.getString("activity_type")

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_tab_content, container, false)

        // Dynamically update the UI based on the activity type
        val speedometerLabel = rootView.findViewById<TextView>(R.id.tv_speedometer_label)
        speedometerLabel.text = "$activityType Speedometer"

        return rootView
    }
}
