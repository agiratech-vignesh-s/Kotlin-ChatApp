package com.example.demoapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginTop
import com.example.demoapp.databinding.ActivityLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginScreen : AppCompatActivity() {
    private var binding:ActivityLoginScreenBinding?=null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code:Int=123
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val sharedPreferences = getSharedPreferences("MY_PRE",Context.MODE_PRIVATE)
        val editer=sharedPreferences.edit()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        firebaseAuth= FirebaseAuth.getInstance()
   binding?.google?.setOnClickListener(){

       val signInIntent: Intent =mGoogleSignInClient.signInIntent
       startActivityForResult(signInIntent,Req_Code)
   }

        val typeface: Typeface =
            Typeface.createFromAsset(assets, "Fasthand-Regular.ttf")
        val app_name=findViewById<TextView?>(R.id.app_name)
        app_name.typeface=typeface


        binding?.btn?.setOnClickListener(){

            if(binding?.btn?.text=="Sign Up") {
                registerUser(editer)
            }else{
                signInRegisteredUser(editer)
            }
        }


        binding?.RegesterName?.setOnClickListener(){

            if (binding?.btn?.text != "Sign Up") {
                Toast.makeText(this,"Sign Up",Toast.LENGTH_SHORT).show()
                binding?.forgotPassword?.visibility = View.INVISIBLE
                binding?.confirmPassword?.visibility = View.VISIBLE
                binding?.btn?.text = "Sign Up"
                binding?.Regester?.text = "I Have Already Account ?"
                binding?.RegesterName?.text = "Login"
            }else{
                Toast.makeText(this,"Sign In",Toast.LENGTH_LONG).show()
                binding?.forgotPassword?.visibility = View.VISIBLE
                binding?.confirmPassword?.visibility = View.GONE
                binding?.btn?.text = "Sign In"
                binding?.Regester?.text = "Not a Member ?"
                binding?.RegesterName?.text = "Regester"
            }


        }

    }

    private fun signInRegisteredUser(editer:SharedPreferences.Editor) {
        val email: String = binding?.email?.text.toString().trim { it <= ' ' }
        val password: String = binding?.password?.text.toString().trim { it <= ' ' }

        if (validateForm(email, password,password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        editer.putString("USERNAME",binding?.email?.text.toString())
                        editer.putString("PASSWORD",binding?.password?.text.toString())
                        editer.apply()
                        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ProfileScreen::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException){
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



    private fun registerUser(editer:SharedPreferences.Editor) {
        val email: String = binding?.email?.text.toString().trim { it <= ' ' }
        val password: String = binding?.password?.text.toString().trim { it <= ' ' }
        val confirmPassword: String = binding?.confirmPasswords?.text.toString().trim { it <= ' ' }

        if (password == confirmPassword && validateForm(confirmPassword,email,password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            editer.putString("USERNAME",binding?.email?.text.toString())
                            editer.putString("PASSWORD",binding?.password?.text.toString())
                            editer.apply()
                            Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show()
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!

                            binding?.forgotPassword?.visibility = View.VISIBLE
                            binding?.confirmPassword?.visibility = View.GONE
                            binding?.btn?.text = "Sign In"
                            binding?.Regester?.text = "Not a Member ?"
                            binding?.RegesterName?.text = "Regester"

                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }

    }

    private fun validateForm(confirmPass: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this,"Please enter email.",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this,"Please enter password.",Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(confirmPass) -> {
                Toast.makeText(this,"Please enter Confirm Password.",Toast.LENGTH_LONG).show()
                false
            }
            else -> {
                true
            }
        }
    }

}