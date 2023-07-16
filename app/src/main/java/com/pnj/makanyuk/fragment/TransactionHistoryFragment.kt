package com.pnj.makanyuk.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.transaction.Transaction
import com.pnj.makanyuk.data.transaction.TransactionAdapter
import com.pnj.makanyuk.data.transaction.TransactionItem
import com.pnj.makanyuk.data.transaction.TransactionItemAdapter
import com.pnj.makanyuk.databinding.FragmentTransactionHistoryBinding

class TransactionHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryBinding

    private val db = Firebase.firestore
    private val transactionList = ArrayList<Transaction>()
    private val fullTransactionList = ArrayList<Transaction>()

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

        binding.swipeRefresh.setColorSchemeResources(R.color.yellow)

        retrieveAllData(uid)

        binding.swipeRefresh.setOnRefreshListener {
            retrieveAllData(uid)
        }

        binding.rvTransactions.layoutManager = LinearLayoutManager(activity)
        binding.rvTransactions.adapter = transactionAdapter

        binding.edtSearchBar.doOnTextChanged { text, start, before, count ->
            binding.swipeRefresh.isRefreshing = true
            findTransaction(text.toString())
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        retrieveAllData(uid)
    }


    private fun retrieveAllData(uid: String) {
        binding.rvTransactions.visibility = View.INVISIBLE
        binding.swipeRefresh.isRefreshing = true

        db.collection("users").document(uid).collection("transactions")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { results ->
                val documentCount = results.size()
                var documentsProcessed = 0

                transactionList.clear()

                for (document in results) {
                    val transaction = document.toObject(Transaction::class.java)
                    val products = ArrayList<TransactionItem>()

                    transaction.doc_id = document.id

                    document.reference.collection("products")
                        .get()
                        .addOnSuccessListener { productsResults ->
                            for (productDocument in productsResults) {
                                val product = productDocument.toObject(TransactionItem::class.java)
                                products.add(product)
                            }
                            transaction.products = products

                            documentsProcessed++
                            if (documentsProcessed == documentCount) {
                                transactionAdapter.notifyDataSetChanged()
                                fullTransactionList.clear()
                                fullTransactionList.addAll(transactionList)

                                binding.swipeRefresh.isRefreshing = false
                                binding.rvTransactions.visibility = View.VISIBLE
                            }
                        }

                    transactionList.add(transaction)
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun findTransaction(keyword: String) {
        transactionList.clear()

        if (keyword != "") {
            transactionList.addAll(fullTransactionList.filter { transaction ->
                transaction.products?.any { it.name.contains(keyword, true) } ?: false
            } as ArrayList<Transaction>)
        } else {
            transactionList.addAll(fullTransactionList)
        }

        transactionAdapter.notifyDataSetChanged()
    }

}