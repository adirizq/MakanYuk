package com.pnj.makanyuk.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.products.Products
import com.pnj.makanyuk.databinding.ActivityEditProductBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProductBinding
    private val db = FirebaseFirestore.getInstance()
    private val REQ_CAM = 101
    private var dataGambar : Bitmap? = null
    private lateinit var imgUri : Uri




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id").toString()
        val nama = intent.getStringExtra("nama").toString()
        val harga = intent.getStringExtra("harga").toString()
        val deskripsi = intent.getStringExtra("deskripsi").toString()

        binding.editName.setText(nama)
        binding.editPrice.setText(harga)
        binding.editDescription.setText(deskripsi)

        showFoto()


        val curr_products  = Products(id, nama, harga.toInt(), deskripsi)

        binding.btnSave.setOnClickListener{
            val new_data_products = newProducts()
            updateProducts(id , new_data_products)

            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }

        binding.btnDelete.setOnClickListener{
//            lifecycleScope.launch {
//                val product = curr_products
//                val productQuery = db.collection("products")
//                    .whereEqualTo("nama", product.nama)
//                    .whereEqualTo("harga", product.harga)
//                    .whereEqualTo("deskrupsi", product.deskripsi)
//                    .get().await()
//                if(productQuery.documents.isNotEmpty()){
//
//                }
//            }
            deleteProducts(curr_products, id)
            deleteFoto("img_product/${curr_products.nama}")
        }

        binding.btnImg.setOnClickListener{
            openCamera()
        }



    }

//    fun setDefaultValue(): Products {
//
//
//        val curr_products = Products(nama, harga.toInt(), deskripsi)
//        return curr_products
//
//    }
    
    fun newProducts() : Map<String, Any> {
        var nama  : String = binding.editName.text.toString()
        var harga : Int = binding.editPrice.text.toString().toInt()
        var deskripsi : String = binding.editDescription.text.toString()

        if (dataGambar != null ){
            uploadPictFirebase(dataGambar!!, "${nama}")
        }

        val products = mutableMapOf<String, Any>()
        products["nama"] = nama
        products["harga"] = harga
        products["deskripsi"] = deskripsi

        return products
    }

    fun updateProducts(id: String, newProductMap : Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        db.collection("products")
            .document(id).update(newProductMap)

    }

    fun deleteProducts(products: Products, doc_id: String) {
        val builder =  AlertDialog.Builder(this)
        builder.setMessage("Apakah ${products.nama} ingin dihapus?" )
            .setCancelable(false)
            .setPositiveButton("yes") { dialog, id ->
                    db.collection("products")
                        .document(doc_id).delete().addOnSuccessListener {
                            Toast.makeText(
                                applicationContext, products.nama.toString() + "is Deleted",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }

            }
            .setNegativeButton("No") {dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

//    fun deleteClick(viewHolder: ViewHolder, direction: Int) {
//        val position = viewHolder.adapterPosition
//        lifecycleScope.launch {
//            val products =
//        }
//    }
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
private fun openCamera() {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
        this.packageManager?.let {
            intent?.resolveActivity(it).also {
                startActivityForResult(intent, REQ_CAM)
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_CAM && resultCode == RESULT_OK) {
            dataGambar = data?.extras?.get("data") as Bitmap
            binding.btnImg.setImageBitmap(dataGambar)
        }

    }
    private fun deleteFoto(file_name: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val deleteFileRef = storageRef.child(file_name)
        if(deleteFileRef != null) {
            deleteFileRef.delete().addOnSuccessListener {
                Log.e("deleted", "success")
            }.addOnFailureListener{
                Log.e("deleted", "failed")
            }
        }
    }


}