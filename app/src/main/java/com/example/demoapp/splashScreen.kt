package com.example.demoapp

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.TextView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )
        val sharedPreferences = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)

        var userName=sharedPreferences.getString("USERNAME","").toString()
        var userPassword=sharedPreferences.getString("PASSWORD","").toString()
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "Fasthand-Regular.ttf")
        var app_name=findViewById<TextView?>(R.id.app_name)
        app_name.typeface=typeface

        Handler(Looper.getMainLooper()).postDelayed({
         if (userName ==""&&userPassword =="") {
             startActivity(Intent(this, LoginScreen::class.java))
             finish()
         }else{
             startActivity(Intent(this, MainActivity::class.java))
             finish()
         }
        }, 2500)
    }
}