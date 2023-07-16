package com.pnj.makanyuk.data.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pnj.makanyuk.R
import java.text.NumberFormat
import java.util.Locale

class TransactionDetailItemAdaper(private val transactionItemList: ArrayList<TransactionItem>): RecyclerView.Adapter<TransactionDetailItemAdaper.TransacationDetailItemViewHolder>() {

    class TransacationDetailItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val subTitle: TextView = itemView.findViewById(R.id.tv_sub)
        val img: ImageView = itemView.findViewById(R.id.transaction_detail_item_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacationDetailItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_detail, parent, false)
        return TransacationDetailItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transactionItemList.size
    }

    override fun onBindViewHolder(holder: TransacationDetailItemViewHolder, position: Int) {
        val transactionItem = transactionItemList[position]

        holder.productName.text = transactionItem.name
        holder.subTitle.text = "${transactionItem.portion} x ${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transactionItem.price).toString().dropLast(3)}"

        Glide.with(holder.itemView.context)
            .load(transactionItem.img_url)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.img)
    }
}