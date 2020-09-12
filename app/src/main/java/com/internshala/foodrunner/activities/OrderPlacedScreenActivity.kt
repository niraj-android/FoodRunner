package com.internshala.foodrunner.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.internshala.foodrunner.R

class OrderPlacedScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed_screen)

        Handler().postDelayed({
            startActivity(Intent(this@OrderPlacedScreenActivity, MainActivity::class.java))
            finish()

        },2000)
    }
}