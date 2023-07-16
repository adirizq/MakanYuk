package com.pnj.makanyuk.data.transaction

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.TransactionDetailActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(private val transactionList: ArrayList<Transaction>): RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

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

        Log.i("Transaction Products Item Document", transaction.toString())

        if(transaction.status == "Selesai") {
            holder.status.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.status.setBackgroundResource(R.drawable.bg_light_green_rounded_small)
        }

        holder.type.text = transaction.type
        holder.status.text = transaction.status
        holder.date.text = dateFormat.format(transaction.created_at!!.toDate())
        holder.totalPrice.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transaction.total_price).toString().dropLast(3)

        if (transaction.type == "Diantar") {
            holder.icType.setImageResource(R.drawable.round_delivery_dining_24)
        } else if (transaction.type == "Ambil Sendiri") {
            holder.icType.setImageResource(R.drawable.round_takeout_dining_24)
        }

        val transactionItemAdapter = transaction.products?.let { TransactionItemAdapter(it) }
        holder.itemRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemRecyclerView.adapter = transactionItemAdapter
        holder.itemRecyclerView.suppressLayout(true)

        holder.itemView.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val intent = Intent(activity, TransactionDetailActivity::class.java)
            activity.startActivity(intent.apply {
                putExtra("DOC_ID", transaction.doc_id)
            })
        }
    }

}