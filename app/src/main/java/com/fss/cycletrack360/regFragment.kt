package com.fss.cycletrack360

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

        val etAge = view.findViewById<EditText>(R.id.et_age)
        val dateBtn = view.findViewById<ImageButton>(R.id.dateBtn)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        // Handle Age Input
        etAge.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val age = etAge.text.toString().toIntOrNull()
                if (age != null) {
                    val birthYear = Calendar.getInstance().get(Calendar.YEAR) - age
                    selectedBirthDate = "01/01/$birthYear"
                }
            }
        }

        // Handle Date Icon Click
        dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedBirthDate = "${String.format("%02d", selectedDay)}/" +
                            "${String.format("%02d", selectedMonth + 1)}/$selectedYear"

                    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                    etAge.setText((currentYear - selectedYear).toString())
                },
                year, month, day
            ).show()
        }

        // Handle Register Button
        btnRegister.setOnClickListener {
            val gender = if (view.findViewById<RadioButton>(R.id.rb_male).isChecked) "Male" else "Female"
            val height = view.findViewById<EditText>(R.id.et_height).text.toString().toFloatOrNull() ?: 0f
            val weight = view.findViewById<EditText>(R.id.et_weight).text.toString().toFloatOrNull() ?: 0f

            if (selectedBirthDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter or select birth date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SQLite
            dbHelper.addUser(gender, height, weight, selectedBirthDate)

            // Navigate to Dashboard
            findNavController().navigate(R.id.action_regFragment_to_dashboardFragment)
        }

        return view
    }
}
