package com.pnj.makanyuk.data.products

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.EditProductActivity
import com.pnj.makanyuk.activity.ViewProductActivity
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class ProductsAdapter(private val productsList : ArrayList<Products>) :
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    private lateinit var activity : AppCompatActivity

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.tv_title)
        val harga : TextView = itemView.findViewById(R.id.tv_price)
        val img_product : ImageView = itemView.findViewById(R.id.img_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false  )
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val products : Products = productsList[position]
        holder.nama.text = products.nama
        holder.harga.text = "${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(products.harga).toString().dropLast(3)}"

        Glide.with(holder.itemView.context)
            .load(products.img_product)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.img_product)

//        val imgRef = FirebaseStorage.getInstance().reference.child("img_product/${products.nama}.jpg")
//
//        imgRef.downloadUrl.addOnSuccessListener {
//
//        }

//        val localfile = File.createTempFile("tempImage", "jps")
//
//        storageRef.getFile(localfile).addOnSuccessListener {
//            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//            holder.img_product.setImageBitmap(bitmap)
//
//        }.addOnFailureListener{
//            Log.e("foto ?","gagal")
//        }


        holder.itemView.setOnClickListener {
            activity = it.context as AppCompatActivity
            activity.startActivity(Intent(activity, EditProductActivity::class.java).apply {
                putExtra("nama", products.nama.toString())
                putExtra("harga", products.harga.toString())
                putExtra("deskripsi", products.deskripsi.toString())
                putExtra("img_url", products.img_product.toString())
                putExtra("id", products.id.toString())
            })
        }

//        holder.itemView.setOnClickListener {
//            activity = it.context as AppCompatActivity
//            activity.startActivity(Intent(activity, ViewProductActivity::class.java).apply {
//                putExtra("nama", products.nama.toString())
//                putExtra("harga", products.harga.toString())
//                putExtra("deskripsi", products.deskripsi.toString())
//                putExtra("id", products.id.toString())
//            })
//        }
    }
}