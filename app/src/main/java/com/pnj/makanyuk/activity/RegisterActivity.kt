package com.pnj.makanyuk.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.makanyuk.R
import com.pnj.makanyuk.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firestoreDatabase = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestoreDatabase = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirm_password = binding.edtPasswordConfirmation.text.toString()

            signup_firebase(email, password, confirm_password)



        }
    }
    private fun signup_firebase(email: String, password: String, confirm_password: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && confirm_password.isNotEmpty()) {
            if (password == confirm_password) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.user?.let { it1 -> addUsers(it1.uid) }
                    }
                    else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText( this, "Samakan Password dan Konfirmasi Password", Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Toast.makeText(this, "Lengkapi Input", Toast.LENGTH_SHORT).show()
        }
    }

    fun addUsers(uid: String) {
        var email: String = binding.edtEmail.text.toString()
        var nama: String = binding.edtName.text.toString()
        var username: String = binding.edtUsername.text.toString()

        val users: MutableMap<String, Any> = HashMap()
        users["email"] = email
        users["nama"] = nama
        users["username"] = username
        users["role"] = "user"

        firestoreDatabase.collection("users").document(uid).set(users)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener{
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

    }
}