package com.fss.cycletrack360

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import java.util.Calendar

class regFragment : Fragment() {
    private lateinit var dbHelper: AppDatabaseHelper
    private var selectedBirthDate: String = ""

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg, container, false)
        dbHelper = AppDatabaseHelper(requireContext())

        val etYearOfBirth = view.findViewById<EditText>(R.id.et_year_of_birth)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        // Handle Year Input
        etYearOfBirth.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val inputYear = etYearOfBirth.text.toString().toIntOrNull()
                if (inputYear != null && inputYear in 1900..Calendar.getInstance().get(Calendar.YEAR)) {
                    // Valid year input
                } else {
                    etYearOfBirth.error = "Please enter a valid year"
                }
            }
        }

        // Handle Calendar Icon Click
        etYearOfBirth.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etYearOfBirth.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= (etYearOfBirth.right - drawableEnd.bounds.width())) {
                    // Calendar icon clicked
                    showYearPickerDialog(etYearOfBirth)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Handle Register Button
        btnRegister.setOnClickListener {
            val birthYear = etYearOfBirth.text.toString()
            val height = view.findViewById<EditText>(R.id.edit_height).text.toString().toFloatOrNull() ?: 0f
            val weight = view.findViewById<EditText>(R.id.edit_weight).text.toString().toFloatOrNull() ?: 0f
            val gender = if (view.findViewById<RadioButton>(R.id.rb_male).isChecked) "Male" else "Female"
            val unit = if (view.findViewById<RadioButton>(R.id.km_unit).isChecked) "Meter" else "Kilometer"

            if (birthYear.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter or select birth year", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (height <= 0 || weight <= 0) {
                Toast.makeText(requireContext(), "Please enter valid height and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SQLite
            dbHelper.addUser(birthYear, height, weight, gender, unit)

            // Navigate to Dashboard
            findNavController().navigate(R.id.action_regFragment_to_dashboardFragment)
        }

        return view
    }

    private fun showYearPickerDialog(editText: EditText) {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Create a DatePickerDialog in spinner mode
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Light_Dialog, // Use spinner mode theme
            { _, selectedYear, _, _ ->
                editText.setText(selectedYear.toString())
            },
            currentYear,
            0,
            1
        )

        // Customize the DatePicker to show only the year
        datePickerDialog.datePicker.apply {
            calendarViewShown = false // Hide calendar view if present
            spinnersShown = true // Ensure spinner mode is shown

            // Hide month and day views to restrict to year selection
            findViewById<View>(
                resources.getIdentifier("month", "id", "android")
            )?.visibility = View.GONE
            findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )?.visibility = View.GONE
        }

        // Set the title for the dialog
        datePickerDialog?.setTitle("Select Year")

        datePickerDialog.show()
    }
}
