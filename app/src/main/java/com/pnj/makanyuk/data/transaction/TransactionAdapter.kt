package com.pnj.makanyuk.data.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pnj.makanyuk.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactionList: ArrayList<Transaction>): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id_ID"))

    class TransactionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val type: TextView = itemView.findViewById(R.id.tv_type)
        val icType: ImageView = itemView.findViewById(R.id.ic_type)
        val status: TextView = itemView.findViewById(R.id.tv_status)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val totalPrice: TextView = itemView.findViewById(R.id.tv_total_price)
        val itemRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_transaction_menu_items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.type.text = transaction.type
        holder.status.text = transaction.status
        holder.date.text = dateFormat.format(transaction.createdAt!!.toDate())
        holder.totalPrice.text = NumberFormat.getCurrencyInstance(Locale("id_ID")).format(transaction.totalPrice).toString()

        if (transaction.type == "Diantar") {
            holder.icType.setImageResource(R.drawable.round_delivery_dining_24)
        } else if (transaction.type == "Ambil Sendiri") {
            holder.icType.setImageResource(R.drawable.round_takeout_dining_24)
        }

        val transactionItemAdapter = transaction.products?.let { TransactionItemAdapter(it) }
        holder.itemRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemRecyclerView.adapter = transactionItemAdapter
    }

}