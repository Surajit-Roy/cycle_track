package com.fss.cycletrack360

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    private lateinit var dbHelper: AppDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        dbHelper = AppDatabaseHelper(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val hasUserData = checkUserData()

            val destination = if (hasUserData) {
                R.id.dashboardFragment
            } else {
                R.id.regFragment
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("destination", destination)
            }
            startActivity(intent)
            finish()
        }, 2000) // Delay of 2 seconds
    }

    private fun checkUserData(): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM User", null)
        cursor.moveToFirst()
        val hasData = cursor.getInt(0) > 0
        cursor.close()
        db.close()
        return hasData
    }
}