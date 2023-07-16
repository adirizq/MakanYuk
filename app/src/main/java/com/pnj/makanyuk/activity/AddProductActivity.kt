package com.pnj.makanyuk.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.pnj.makanyuk.R
import com.pnj.makanyuk.databinding.ActivityAddProductBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddProductBinding
    private val firestoreDatabase = FirebaseFirestore.getInstance()

    private val REQ_CAM = 101
    private lateinit var imgUri : Uri
    private var dataGambar : Bitmap? = null

    companion object {
        val IMAGE_REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener{
            addProducts()
        }

        binding.btnImg.setOnClickListener{
            openCamera()
        }



    }

    fun addProducts() {
        binding.loading.visibility = View.VISIBLE
        val nama : String = binding.addName.text.toString()
        val harga : String = binding.addPrice.text.toString()
        val deskripsi : String = binding.addDescription.text.toString()

        if (nama == "" || harga == "" || deskripsi == "" || dataGambar == null) {
            binding.loading.visibility = View.GONE
            Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }


        uploadPictFirebase(dataGambar!!)
            .addOnSuccessListener { downloadUrl ->
                val products : MutableMap<String, Any> = HashMap()

                products["nama"] = nama
                products["harga"] = harga.toInt()
                products["deskripsi"] = deskripsi
                products["img_product"] = downloadUrl

                firestoreDatabase.collection("products").add(products)
                    .addOnSuccessListener {
                        binding.loading.visibility = View.GONE
                        finish()
                    }
            }
            .addOnFailureListener {
                binding.loading.visibility = View.GONE
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {intent ->
            this.packageManager?.let {
                intent?.resolveActivity(it).also {
                    startActivityForResult(intent, REQ_CAM)
                }
            }
        }
    }

    

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CAM && resultCode == RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            binding.btnImg.setImageBitmap(dataGambar)
        }
    }

    private fun uploadPictFirebase(bitmap: Bitmap): Task<Uri> {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("img_product/$fileName")

        return refStorage.putBytes(data).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            refStorage.downloadUrl
        }
    }


}