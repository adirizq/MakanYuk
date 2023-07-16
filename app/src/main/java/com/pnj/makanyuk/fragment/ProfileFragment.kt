package com.pnj.makanyuk.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.LoginActivity
import com.pnj.makanyuk.activity.MainActivity
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
//    private val uid = "MSmu9WTeekTuDLN6wUHx8KKSXnw1"
    private val uid = "nAW7uzigeaV898EC9LzK6DC3jAR2" //mhs@gmail.com



    val db = FirebaseFirestore.getInstance()
    val personQuery = db.collection("users").document(uid)

    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email
    var data_email = email.toString()

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
            val new_pass = binding.edtNewPassword.text.toString()
            val old_pass = binding.edtOldPassword.text.toString()
            val new_name = binding.addName.text.toString()

            changeData(old_pass, new_pass, new_name)

        }

        return binding.root
    }

    fun changeData(oldPassword: String, newPassword: String, newName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Password updated successfully
                                    personQuery.update("name", newName)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Data Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            // Handle any errors
                                            Toast.makeText(context, "Data Gagal Diubah", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // Password update failed
                                    Toast.makeText(context, "Password Gagal Diubah", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Re-authentication failed
                        Toast.makeText(context, "Password Tidak Sesuai", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun SetData() {
        binding.edtEmail.setText(data_email)
        personQuery.get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    // Get the data for the document
                    val name = document.getString("nama")
                    val username = document.getString("username")

                    binding.addName.setText(name)
                    binding.edtUsername.setText(username)
                }
            }
            .addOnFailureListener {
                // Handle any errors
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

        override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser == null) {
            firebaseAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}