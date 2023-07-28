package com.example.demoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demoapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding?=null
    var database:FirebaseDatabase? = null
    var users:ArrayList<User>? =null
    var userAdapter:UserAdapter? =null
    var user:User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        database=FirebaseDatabase.getInstance()
        users=ArrayList<User>()
        userAdapter= UserAdapter(this,users!!)
        val layoutManage= LinearLayoutManager(this)
        binding!!.mRec.layoutManager=layoutManage

        database!!.reference.child("User")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user=snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        binding!!.mRec.adapter=userAdapter
        database!!.reference.child("User")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    users!!.clear()
                    for (sna in snapshot.children){
                        Log.e("value","${sna.children}")
                        var user:User?=sna.getValue(User::class.java)
      if (!user!!.uid.equals(FirebaseAuth.getInstance().uid))
          users!!.add(user!!)
                    }
                    userAdapter!!.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        val sharedPreferences = getSharedPreferences("MY_PRE", Context.MODE_PRIVATE)
       val edit=sharedPreferences.edit()
        var userName=sharedPreferences.getString("USERNAME","").toString()
        var userPassword=sharedPreferences.getString("PASSWORD","").toString()

//        binding?.welcome?.text ="UserName is :$userName Password is $userPassword"
        binding?.button?.setOnClickListener(){
            edit?.clear()
            edit?.apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        var currentId=FirebaseAuth.getInstance().uid
        database?.reference?.child("Presence")?.child(currentId!!)?.setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        var currentId=FirebaseAuth.getInstance().uid
        database?.reference?.child("Presence")?.child(currentId!!)?.setValue("Offline")
    }
}