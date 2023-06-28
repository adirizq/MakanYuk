package com.pnj.makanyuk.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.Products
import com.pnj.makanyuk.data.ProductsAdapter
import com.pnj.makanyuk.databinding.FragmentMenuBinding

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuFragment : Fragment() {
    private lateinit var  binding : FragmentMenuBinding

    private lateinit var productsRecyclerView : RecyclerView
    private lateinit var productsArrayList : ArrayList<Products>
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater)

        productsRecyclerView = binding.rvMenu
        productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.setHasFixedSize(true)

        productsArrayList = arrayListOf()
        productsAdapter = ProductsAdapter(productsArrayList)

        productsRecyclerView.adapter = productsAdapter

        load_data()
        return binding.root
    }

    private fun load_data() {
        productsArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("products").
                addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error != null) {
                            Log.e("Firestore Error", error.message.toString())
                            return
                        }
                        for (dc : DocumentChange in value?.documentChanges!!) {
                            if(dc.type == DocumentChange.Type.ADDED)
                                productsArrayList.add(dc.document.toObject(Products::class.java))
                        }
                        productsAdapter.notifyDataSetChanged()
                    }

                })


    }

}