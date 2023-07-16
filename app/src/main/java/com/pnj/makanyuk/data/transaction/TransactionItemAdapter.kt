package com.pnj.makanyuk.data.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pnj.makanyuk.R

class TransactionItemAdapter(private val transactionItemList: ArrayList<TransactionItem>): RecyclerView.Adapter<TransactionItemAdapter.TransacationItemViewHolder>() {

    class TransacationItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_name)
        val portion: TextView = itemView.findViewById(R.id.tv_portion)
        val img: ImageView = itemView.findViewById(R.id.transaction_item_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacationItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_menu, parent, false)
        return TransacationItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transactionItemList.size
    }

    override fun onBindViewHolder(holder: TransacationItemViewHolder, position: Int) {
        val transactionItem = transactionItemList[position]

        holder.productName.text = transactionItem.name
        holder.portion.text = transactionItem.portion.toString() + " Porsi"

        Glide.with(holder.itemView.context)
            .load(transactionItem.img_url.toString())
            .placeholder(R.drawable.image_placeholder)
            .into(holder.img)
    }
}