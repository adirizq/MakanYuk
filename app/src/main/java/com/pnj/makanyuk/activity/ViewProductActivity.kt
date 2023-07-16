package com.pnj.makanyuk.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.makanyuk.databinding.ActivityEditProductBinding
import com.pnj.makanyuk.databinding.ActivityViewProductBinding
import java.io.File


class ViewProductActivity : AppCompatActivity() {
    private lateinit var binding : ActivityViewProductBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id").toString()
        val nama = intent.getStringExtra("nama").toString()
        val harga = intent.getStringExtra("harga").toString()
        val deskripsi = intent.getStringExtra("deskripsi").toString()

        binding.tvName.setText(nama)
        binding.tvPrice.setText(harga)
        binding.tvDescription.setText(deskripsi)

        showFoto()

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    fun showFoto() {
        val intent = intent
        val nama = intent.getStringExtra("nama").toString()


        val storageRef = FirebaseStorage.getInstance().reference.child("img_product/${nama}.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            binding.btnImg.setImageBitmap(bitmap)
        }. addOnFailureListener{
            Log.e("foto ?", "gagal")
        }
    }

}