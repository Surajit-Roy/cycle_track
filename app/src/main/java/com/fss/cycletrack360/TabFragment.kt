package com.fss.cycletrack360

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class TabFragment : Fragment() {

    private var activityType: String? = null
    private lateinit var locationManager: LocationManager
    private lateinit var speedTextView: TextView
    private lateinit var timeInfoTextView: TextView
    private lateinit var distanceInfoTextView: TextView
    private lateinit var calInfoTextView: TextView
    private var isTracking = false
    private var startTime: Long = 0
    private var totalDistance = 0.0f
    private var caloriesBurned = 0.0f
    private var gender = ""
    private var weight: Float = 0f // In kg, update according to user's data
    private var lastSpeed: Float = 0.0f // Store the last known speed
    private var previousLocation: Location? = null // Store the previous location
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var dbHelper: AppDatabaseHelper
    private lateinit var calorieCalculator: CalorieCalculator

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

        dbHelper = AppDatabaseHelper(requireContext())
        val user = dbHelper.getUser(1)
        if (user != null) {
            weight = user.weight
            gender = user.gender
        }

        calorieCalculator = CalorieCalculator(weight.toDouble(), gender.lowercase())

        // Initialize the speed display
        speedTextView = TextView(requireContext()).apply {
            textSize = 48f
            text = "0.0 km/h"
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        // Add speedTextView to the speedometer container
        val speedometerContainer = rootView.findViewById<ViewGroup>(R.id.speedometer_container)
        speedometerContainer.addView(speedTextView)

        // Initialize the time, distance, and calorie displays
        timeInfoTextView = rootView.findViewById(R.id.time_info)
        distanceInfoTextView = rootView.findViewById(R.id.distance_info)
        calInfoTextView = rootView.findViewById(R.id.cal_info)

        // Initialize the location manager
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Button to start tracking
        val startButton = rootView.findViewById<Button>(R.id.btn_start)
        startButton.setOnClickListener {
            if (!isTracking) {
                startLocationUpdates()
                startButton.text = "Stop"
                startButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.stop_button_color))
                isTracking = true
            } else {
                stopLocationUpdates()
                startButton.text = "Start"
                startButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.start_button_color))
                isTracking = false
            }
        }

        return rootView
    }

    private fun startLocationUpdates() {
        // Reset UI and initialize tracking
        speedTextView.text = "0.0 km/h"
        distanceInfoTextView.text = "0m"
        startTime = System.currentTimeMillis()

        // Start updating time, distance, and calories
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (isTracking) {
                    updateTime()
                    updateDistanceAndCalories()
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable)

        // Check permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            return
        }

        // Request location updates from GPS_PROVIDER
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, locationListener)
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
        if (::handler.isInitialized) handler.removeCallbacks(runnable)
    }

    private fun updateTime() {
        val elapsedTime = (System.currentTimeMillis() - startTime) / 1000 // In seconds
        val minutes = (elapsedTime / 60).toInt()
        val seconds = (elapsedTime % 60).toInt()
        timeInfoTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

//    private fun calculateCaloriesBurned(speed: Float, elapsedTimeInSeconds: Long, activityType: String?): Float {
//        // Determine MET value dynamically based on activity type and speed
//        val metValue = when (activityType) {
//            "Running" -> 0.2f * speed + 3.5f
//            "Cycling" -> 0.1f * speed + 5.5f
//            else -> 0.1f * speed + 3.5f
//        }
//
//        // Convert elapsed time to hours
//        val elapsedTimeInHours = elapsedTimeInSeconds / 3600f
//
//        // Calorie burn formula
//        return metValue * weight * elapsedTimeInHours
//    }

    private fun updateDistanceAndCalories() {
        // Distance
        if (totalDistance < 1000) {
            distanceInfoTextView.text = String.format("%.0f m", totalDistance)
        } else {
            distanceInfoTextView.text = String.format("%.2f km", totalDistance / 1000)
        }

        // Calculate elapsed time in seconds
        val elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000

        // Update calories burned
        caloriesBurned = calorieCalculator.calculateCalories(activityType ?: "", totalDistance.toDouble()).toFloat()
        calInfoTextView.text = String.format("%.1f kcal", caloriesBurned)
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val speed = location.speed * 3.6f // Convert m/s to km/h
            lastSpeed = speed
            speedTextView.text = String.format("%.1f km/h", speed)

            // Update distance
            if (previousLocation != null) {
                val distanceInMeters = previousLocation!!.distanceTo(location)
                totalDistance += distanceInMeters
            }
            previousLocation = location

            // Update UI
            updateDistanceAndCalories()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {
            handleGPSUnavailable()
        }
    }

    private fun handleGPSUnavailable() {
        Toast.makeText(requireContext(), "GPS unavailable. Enable GPS for tracking.", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isTracking) stopLocationUpdates()
    }
}
