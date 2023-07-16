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
import com.google.firebase.firestore.FirebaseFirestore
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
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
            finish()
        }

        binding.btnRegister.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
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
            }else {
                binding.loading.visibility = View.GONE
                BeautifulDialog.build(this)
                    .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description("Password dan Konfirmasi Password berbeda",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.ERROR)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    }
            }

        }
        else {
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

    fun addUsers(uid: String) {
        var email: String = binding.edtEmail.text.toString()
        var nama: String = binding.edtName.text.toString()
        var username: String = binding.edtUsername.text.toString()

        val users: MutableMap<String, Any> = HashMap()
        users["email"] = email
        users["name"] = nama
        users["username"] = username
        users["role"] = "user"

        firestoreDatabase.collection("users").document(uid).set(users)
            .addOnSuccessListener {
                binding.loading.visibility = View.GONE
                BeautifulDialog.build(this)
                    .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description("Berhasil membuat akun",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.SUCCESS)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                        finish()
                    }
            }
            .addOnFailureListener{
                binding.loading.visibility = View.GONE
                BeautifulDialog.build(this)
                    .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description(it.message.toString(),  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.ERROR)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    }
            }

    }
}