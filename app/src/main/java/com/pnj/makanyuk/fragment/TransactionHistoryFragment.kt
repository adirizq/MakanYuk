package com.pnj.makanyuk.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pnj.makanyuk.data.transaction.Transaction
import com.pnj.makanyuk.data.transaction.TransactionAdapter
import com.pnj.makanyuk.data.transaction.TransactionItem
import com.pnj.makanyuk.data.transaction.TransactionItemAdapter
import com.pnj.makanyuk.databinding.FragmentTransactionHistoryBinding

class TransactionHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryBinding

    private val db = Firebase.firestore
    private val transactionList = ArrayList<Transaction>()
    private val transactionAdapter = TransactionAdapter(transactionList)

    private val uid = "DNGowVPxTCy5T7bp5LrK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionHistoryBinding.inflate(layoutInflater)

        retrieveAllData(uid)

        binding.rvTransactions.layoutManager = LinearLayoutManager(activity)
        binding.rvTransactions.adapter = transactionAdapter

        return binding.root
    }

    private fun retrieveAllData(uid: String) {
        db.collection("users").document(uid).collection("transactions")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Retrieve All Data [Transaction]", "Listen failed.", e)
                    return@addSnapshotListener
                }

                binding.rvTransactions.visibility = View.INVISIBLE
                binding.loading.visibility = View.VISIBLE

                transactionList.clear()

                for (document in snapshots!!) {
                    Log.i("Transaction Document", document.toString())
                    val transaction = document.toObject(Transaction::class.java)
                    Log.i("Transaction Document", transaction.toString())
                    val products = ArrayList<TransactionItem>()

                    document.reference.collection("products")
                        .get()
                        .addOnSuccessListener { result ->
                            for (productDocument in result) {
                                val product = productDocument.toObject(TransactionItem::class.java)
                                products.add(product)
                            }
                            transaction.products = products
                        }

                    transactionList.add(transaction)
                }

                transactionAdapter.notifyDataSetChanged()

                binding.rvTransactions.visibility = View.VISIBLE
                binding.loading.visibility = View.GONE
            }
    }

}