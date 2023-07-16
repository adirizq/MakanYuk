package com.pnj.makanyuk.fragment

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.CartActivity
import com.pnj.makanyuk.data.Products
import com.pnj.makanyuk.data.ProductsAdapter
import com.pnj.makanyuk.activity.AddProductActivity
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
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater)

        productsRecyclerView = binding.rvMenu
        // productsRecyclerView.layoutManager = LinearLayoutManager(activity)
        productsRecyclerView.setHasFixedSize(true)

        productsArrayList = arrayListOf()
        productsAdapter = ProductsAdapter(productsArrayList)

        productsRecyclerView.adapter = productsAdapter

        load_data()

        binding.btnShoppingBag.visibility = View.VISIBLE

        binding.btnShoppingBag.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.btnAddProduct.setOnClickListener{
            val intentMain = Intent(activity, AddProductActivity::class.java)
            activity?.startActivity( intentMain)
        }

        binding.edtSearchBar.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val keyword = binding.edtSearchBar.text.toString()
                if(keyword.isNotEmpty()){
                    search_data(keyword)
                }
                else{
                    load_data()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        return binding.root

    }

    private fun load_data() {

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productsArrayList.clear()
                for (document in result ) {
                    val product = document.toObject(Products::class.java)
                    product.id = document.id
                    productsArrayList.add(product)
                }
               productsAdapter.notifyDataSetChanged()
            }



    }

    private fun search_data(keyword :String) {
        productsArrayList.clear()

        db = FirebaseFirestore.getInstance()

        val query = db.collection("products")
            .orderBy("nama")
            .startAt(keyword)
            .get()
        query.addOnSuccessListener {
            productsArrayList.clear()
            for (document in it) {
                productsArrayList.add(document.toObject(Products::class.java))
            }
        }
    }

}