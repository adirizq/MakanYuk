package com.pnj.makanyuk.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.makanyuk.R
import com.pnj.makanyuk.databinding.ActivityAddProductBinding
import java.io.ByteArrayOutputStream

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
        var nama : String = binding.addName.text.toString()
        var harga : String = binding.addPrice.text.toString()
        var deskripsi : String = binding.addDescription.text.toString()

        val products : MutableMap<String, Any> = HashMap()
        products["nama"] = nama
        products["harga"] = harga.toInt()
        products["deskripsi"] = deskripsi

        if(dataGambar != null) {
            uploadPictFirebase(dataGambar!!, "${nama}")
        }

        firestoreDatabase.collection("products").add(products)
            .addOnSuccessListener {
                val intentMain = Intent(this, MainActivity::class.java)
                startActivity(intentMain)
            }
    }

//    private fun pickImageGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/"
//        startActivityForResult(intent, IMAGE_REQUEST_CODE)
//    }
    
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

    private fun uploadPictFirebase(img_bitmap: Bitmap, file_name:String) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img_product/${file_name}.jpg")
        img_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val img = baos.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result.let { Uri ->
                            imgUri = Uri
                            binding.btnImg.setImageBitmap(img_bitmap)
                        }
                    }
                }
            }
    }


}