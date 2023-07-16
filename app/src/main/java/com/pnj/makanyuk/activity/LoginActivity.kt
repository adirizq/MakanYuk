package com.pnj.makanyuk.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.pnj.makanyuk.R
import com.pnj.makanyuk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            signin_firebase(email, password)
        }
    }

    private fun signin_firebase(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.loading.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    binding.loading.visibility = View.GONE
                    BeautifulDialog.build(this)
                        .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                        .description(it.exception?.message.toString(),  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                        .type(type= BeautifulDialog.TYPE.ERROR)
                        .position(BeautifulDialog.POSITIONS.CENTER)
                        .hideNegativeButton(true)
                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                        }
                }
            }
        }
        else {
            binding.loading.visibility = View.GONE
            BeautifulDialog.build(this)
                .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Harap lengkapi input",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ERROR)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .hideNegativeButton(true)
                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                }
        }
    }

//    override fun onStart() {
//        super.onStart()
//
//        if (firebaseAuth.currentUser != null) {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//    }
}