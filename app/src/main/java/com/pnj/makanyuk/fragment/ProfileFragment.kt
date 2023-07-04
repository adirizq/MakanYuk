package com.pnj.makanyuk.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.pnj.makanyuk.R
import com.pnj.makanyuk.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val uid = "DNGowVPxTCy5T7bp5LrK"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSave.setOnClickListener {
            val new_password = binding.edtNewPassword.text.toString()
            edit_password(new_password)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        retrieveAllData(uid)

        return binding.root
    }



    private fun retrieveAllData(uid: String) {

    }

    private fun edit_password(new_password: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val new_password = new_password

        user!!.updatePassword(new_password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Password Berhasil Diubah", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Password Gagal Diubah", Toast.LENGTH_SHORT).show()
            }
        }
    }

}