package com.fss.cycletrack360

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController

class regFragment : Fragment() {
    private lateinit var dbHelper: AppDatabaseHelper

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reg, container, false)
        dbHelper = AppDatabaseHelper(requireContext())

        val btnRegister = view.findViewById<Button>(R.id.btn_register)
        btnRegister.setOnClickListener {
            val gender = if (view.findViewById<RadioButton>(R.id.rb_male).isChecked) "Male" else "Female"
            val height = view.findViewById<EditText>(R.id.et_height).text.toString().toFloat()
            val weight = view.findViewById<EditText>(R.id.et_weight).text.toString().toFloat()
            val birthdate = view.findViewById<Button>(R.id.btn_select_birthdate)

                // Save to SQLite
                dbHelper.addUser(gender, height, weight, "20/10/2024")

            // Navigate to Dashboard
            findNavController().navigate(R.id.action_regFragment_to_dashboardFragment)
        }

        return view
    }
}