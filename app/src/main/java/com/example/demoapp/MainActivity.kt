package com.example.demoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.demoapp.databinding.ActivityLoginScreenBinding
import com.example.demoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val sharedPreferences = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
       val edit=sharedPreferences.edit()
        var userName=sharedPreferences.getString("USERNAME","").toString()
        var userPassword=sharedPreferences.getString("PASSWORD","").toString()

        binding?.welcome?.text ="UserName is :$userName Password is $userPassword"
        binding?.button?.setOnClickListener(){
            edit?.clear()
            edit?.apply()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }
}