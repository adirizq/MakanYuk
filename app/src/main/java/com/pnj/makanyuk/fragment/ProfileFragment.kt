package com.pnj.makanyuk.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.EmailAuthProvider
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
import com.pnj.makanyuk.activity.LoginActivity
import com.pnj.makanyuk.activity.MainActivity
import com.pnj.makanyuk.data.chat.SharedViewModel
import com.pnj.makanyuk.databinding.FragmentProfileBinding
import com.pnj.makanyuk.databinding.FragmentTransactionHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    val user = FirebaseAuth.getInstance().currentUser
    private val userEmail = user?.email.toString()
    private val uid = user?.uid.toString()

    val db = FirebaseFirestore.getInstance()
    val personQuery = db.collection("users").document(uid)

    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        SetData()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSave.setOnClickListener {
            val newPass = binding.edtNewPassword.text.toString()
            val oldPass = binding.edtOldPassword.text.toString()
            val newName = binding.addName.text.toString()

            changeData(oldPass, newPass, newName)

        }

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }

    fun changeData(oldPassword: String, newPassword: String, newName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null && oldPassword != "") {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (newPassword != "") {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Password updated successfully
                                        if (newName != "") {
                                            personQuery.update("name", newName)
                                                .addOnSuccessListener {
                                                    BeautifulDialog.build(context as Activity)
                                                        .title("Berhasil", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                                        .description("Data berhasil diubah",  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                                        .type(type= BeautifulDialog.TYPE.SUCCESS)
                                                        .position(BeautifulDialog.POSITIONS.CENTER)
                                                        .hideNegativeButton(true)
                                                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                                        }
                                                }
                                                .addOnFailureListener { exception ->
                                                    // Handle any errors
                                                    BeautifulDialog.build(context as Activity)
                                                        .title("Gagal", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                                        .description(exception.message.toString(),  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                                        .type(type= BeautifulDialog.TYPE.ERROR)
                                                        .position(BeautifulDialog.POSITIONS.CENTER)
                                                        .hideNegativeButton(true)
                                                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                                        }
                                                }
                                        } else {
                                            BeautifulDialog.build(context as Activity)
                                                .title("Berhasil", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                                .description("Data berhasil diubah",  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                                .type(type= BeautifulDialog.TYPE.SUCCESS)
                                                .position(BeautifulDialog.POSITIONS.CENTER)
                                                .hideNegativeButton(true)
                                                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                                }
                                        }
                                    } else {
                                        // Password update failed
                                        BeautifulDialog.build(context as Activity)
                                            .title("Gagal", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                            .description(task.exception?.message.toString(),  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                            .type(type= BeautifulDialog.TYPE.ERROR)
                                            .position(BeautifulDialog.POSITIONS.CENTER)
                                            .hideNegativeButton(true)
                                            .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                            }
                                    }
                                }
                        } else if (newName != "") {
                            personQuery.update("name", newName)
                                .addOnSuccessListener {
                                    BeautifulDialog.build(context as Activity)
                                        .title("Berhasil", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                        .description("Data berhasil diubah",  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                        .type(type= BeautifulDialog.TYPE.SUCCESS)
                                        .position(BeautifulDialog.POSITIONS.CENTER)
                                        .hideNegativeButton(true)
                                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle any errors
                                    BeautifulDialog.build(context as Activity)
                                        .title("Gagal", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                                        .description(exception.message.toString(),  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                                        .type(type= BeautifulDialog.TYPE.ERROR)
                                        .position(BeautifulDialog.POSITIONS.CENTER)
                                        .hideNegativeButton(true)
                                        .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                                        }
                                }
                        }

                    } else {
                        // Re-authentication failed
                        BeautifulDialog.build(context as Activity)
                            .title("Gagal", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                            .description("Verifikasi password salah",  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                            .type(type= BeautifulDialog.TYPE.ERROR)
                            .position(BeautifulDialog.POSITIONS.CENTER)
                            .hideNegativeButton(true)
                            .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                            }
                    }
                }
        } else {
            BeautifulDialog.build(context as Activity)
                .title("Gagal", titleColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold))
                .description("Harap isi verifikasi password",  color = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ERROR)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .hideNegativeButton(true)
                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(context as Activity, R.color.black), fontStyle = ResourcesCompat.getFont(context as Activity, R.font.poppins_bold)) {
                }
        }
    }

    private fun SetData() {
        personQuery.get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    // Get the data for the document
                    val name = document.getString("name")
                    val username = document.getString("username")

                    binding.addName.setText(name)
                    binding.edtUsername.setText(username)
                    binding.edtEmail.setText(userEmail)
                }
            }
            .addOnFailureListener {
                // Handle any errors
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.username.value = binding.edtUsername.text.toString()
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

}