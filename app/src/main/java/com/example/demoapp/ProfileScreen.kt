package com.example.demoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.demoapp.databinding.ActivityProfileScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class ProfileScreen : AppCompatActivity() {
    private var binding: ActivityProfileScreenBinding? = null

    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding?.imageView?.setOnClickListener() {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }
        binding?.btn?.setOnClickListener() {
            var name: String? = binding?.name?.text.toString()
            if (name!!.isEmpty()) {
                binding?.name?.setError("Please enter Name")
            }
            if (selectedImage != null) {
                val reference = storage!!.reference.child("Profile").child(auth!!.uid!!)
                reference.putFile(selectedImage!!).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            val imageUrl = uri.toString()
                            val uid = auth!!.uid
                            val gmail = auth!!.currentUser!!.email
                            val name = binding?.name?.text.toString()
                            val user = User(gmail,name,imageUrl,uid)
                            database!!.reference.child("User")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCompleteListener {
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                    Toast.makeText(this, "Profile Uploaded", Toast.LENGTH_LONG)
                                        .show()
                                }
                        }
                    } else {
                        val uid = auth!!.uid
                        val gmail = auth!!.currentUser!!.email
                        val name = binding?.name?.text.toString()
                        val user = User(gmail, name,"No Image",uid)
                        database!!.reference.child("User")
                            .child(uid!!)
                            .setValue(user)
                            .addOnCompleteListener {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                                Toast.makeText(this, "No Profile Uploaded", Toast.LENGTH_LONG)
                                    .show()
                            }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                var uri = data.data
                var storage = FirebaseStorage.getInstance()
                var time = Date().time
                var reference = storage.reference
                    .child("Profile")
                    .child(time.toString() + "")
                reference.putFile(uri!!).addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        reference.downloadUrl.addOnCompleteListener { uri ->
                            var filePath = uri.toString()
                            var obj = HashMap<String, Any>()
                            obj["image"] = filePath
                            database!!.reference.child("User")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnCompleteListener { }

                        }
                    }

                }
                binding?.imageView?.setImageURI(data.data)
                selectedImage = data.data
            }
        }
    }

}