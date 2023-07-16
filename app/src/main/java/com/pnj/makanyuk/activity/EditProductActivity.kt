package com.pnj.makanyuk.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
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
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProductBinding
    private val db = FirebaseFirestore.getInstance()
    private val REQ_CAM = 101
    private var dataGambar : Bitmap? = null
    private lateinit var imgUri : Uri

    private lateinit var role: String
    private val uid = "DNGowVPxTCy5T7bp5LrK"
    private lateinit var currProducts: Products

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loading.visibility = View.VISIBLE

        val intent = intent
        val id = intent.getStringExtra("id").toString()
        val nama = intent.getStringExtra("nama").toString()
        val harga = intent.getStringExtra("harga").toString()
        val deskripsi = intent.getStringExtra("deskripsi").toString()
        val imgUrl = intent.getStringExtra("img_url").toString()

        role = "user"

        if (role == "user"){
            binding.componentView.visibility = View.VISIBLE
            binding.componentEdit.visibility = View.GONE

            binding.btnAddToCart.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.GONE
            binding.btnSave.visibility = View.GONE

            binding.tvName.text = nama
            binding.tvPrice.text = "${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(harga.toInt()).toString().dropLast(3)}"
            binding.tvDescription.text = deskripsi
        } else {
            binding.componentView.visibility = View.GONE
            binding.componentEdit.visibility = View.VISIBLE

            binding.btnAddToCart.visibility = View.GONE
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnSave.visibility = View.VISIBLE

            binding.editName.setText(nama)
            binding.editPrice.setText(harga)
            binding.editDescription.setText(deskripsi)
        }

        Glide.with(this)
            .load(imgUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(binding.btnImg)

        binding.loading.visibility = View.GONE

        currProducts  = Products(id, nama, harga.toInt(), deskripsi, imgUrl)

        binding.btnSave.setOnClickListener{
            BeautifulDialog.build(this)
                .title("Simpan Perubahan?", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Silahkan klik tombol \"Ya\" jika anda yakin untuk menyimpan perubahan sekarang.",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ALERT)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = "Ya", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    binding.loading.visibility = View.VISIBLE

                    var nama  : String = binding.editName.text.toString()
                    var harga : Int = binding.editPrice.text.toString().toInt()
                    var deskripsi : String = binding.editDescription.text.toString()

                    val products = mutableMapOf<String, Any>()

                    products["nama"] = nama
                    products["harga"] = harga
                    products["deskripsi"] = deskripsi

                    if (dataGambar != null ){
                        uploadPictFirebase(dataGambar!!).addOnSuccessListener { downloadUrl ->
                            val storageReference = Firebase.storage.getReferenceFromUrl(imgUrl)
                            storageReference.delete()
                                .addOnSuccessListener {
                                    products["img_product"] = downloadUrl

                                    db.collection("products").document(id).update(products)
                                        .addOnSuccessListener {
                                            binding.loading.visibility = View.GONE
                                            finish()
                                        }
                                }
                        }
                    } else {
                        products["img_product"] = intent.getStringExtra("img_url").toString()
                        db.collection("products").document(id).update(products)
                            .addOnSuccessListener {
                                binding.loading.visibility = View.GONE
                                finish()
                            }
                    }
                }
                .onNegative(text = "Tidak", buttonBackgroundColor = R.drawable.bg_outline_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.yellow), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                }
        }

        binding.btnDelete.setOnClickListener{
            deleteProducts(currProducts, id)
            deleteFoto("img_product/${currProducts.nama}")
        }

        binding.btnImg.setOnClickListener{
            openCamera()
        }

        binding.btnAddToCart.setOnClickListener {
            db.collection("users").document(uid).get()
                .addOnSuccessListener {
                    val cartItem = it.get("cart_items")
                    val cartItemList = mutableListOf<MutableMap<String, Any>>()

                    if (cartItem != null) {
                        cartItemList.addAll(cartItem as MutableList<MutableMap<String, Any>>)
                    }

                    cartItemList.add(
                        mutableMapOf(
                            "img_url" to currProducts.img_product.toString(),
                            "name" to currProducts.nama.toString(),
                            "price" to currProducts.harga!!.toInt(),
                            "portion" to 1
                        )
                    )

                    db.collection("users").document(uid).update(hashMapOf("cart_items" to cartItemList) as Map<String, Any>).addOnSuccessListener {
                        BeautifulDialog.build(this)
                            .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                            .description("Berhasil menambahkan ke Keranjang",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                            .type(type= BeautifulDialog.TYPE.SUCCESS)
                            .position(BeautifulDialog.POSITIONS.CENTER)
                            .hideNegativeButton(true)
                            .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                                finish()
                            }
                    }
                }
        }

    }

    fun deleteProducts(products: Products, doc_id: String) {
        binding.loading.visibility = View.VISIBLE

        BeautifulDialog.build(this)
            .title("Hapus Produk?", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
            .description("Silahkan klik tombol \"Ya\" jika anda yakin untuk menghapus produk sekarang.",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
            .type(type= BeautifulDialog.TYPE.ALERT)
            .position(BeautifulDialog.POSITIONS.CENTER)
            .onPositive(text = "Ya", buttonBackgroundColor = R.drawable.bg_red_rounded, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                db.collection("products").document(doc_id).delete()
                    .addOnSuccessListener {
                        val storageReference = Firebase.storage.getReferenceFromUrl(products.img_product.toString())
                        storageReference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext, products.nama.toString() + "is Deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.loading.visibility = View.GONE
                                finish()
                        }
                    }
            }
            .onNegative(text = "Tidak", buttonBackgroundColor = R.drawable.bg_outline_red_rounded, textColor = ContextCompat.getColor(this, R.color.red), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
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
    private fun deleteFoto(imgUrl: String) {
//        val storage = Firebase.storage
//        val storageRef = storage.reference
//        val deleteFileRef = storageRef.child(file_name)
//        if(deleteFileRef != null) {
//            deleteFileRef.delete().addOnSuccessListener {
//                Log.e("deleted", "success")
//            }.addOnFailureListener{
//                Log.e("deleted", "failed")
//            }
//        }


    }


}