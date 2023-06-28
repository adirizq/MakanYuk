package com.pnj.makanyuk.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pnj.makanyuk.R

class ProductsAdapter(private val productsList : ArrayList<Products>) :
    RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.tv_title)
        val harga : TextView = itemView.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_menu, parent, false  )
        return ProductsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val products : Products = productsList[position]
        holder.nama.text = products.nama
        holder.harga.text = products.harga.toString()

    }

}