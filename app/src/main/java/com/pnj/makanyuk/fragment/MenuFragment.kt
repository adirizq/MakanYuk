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
import android.widget.Toast
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
import com.pnj.makanyuk.data.products.ProductsAdapter
import com.pnj.makanyuk.activity.AddProductActivity
import com.pnj.makanyuk.data.products.Products
import com.pnj.makanyuk.data.transaction.Transaction
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

    private val uid = "DNGowVPxTCy5T7bp5LrK"
    private var db = FirebaseFirestore.getInstance()
    private var fullProductsArrayList = ArrayList<Products>()

    private lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
        load_data()

        if (role == "user") {
            binding.btnShoppingBag.visibility = View.GONE
            binding.btnAddProduct.visibility = View.GONE

            db.collection("users").document(uid).get()
                .addOnSuccessListener {
                    var cartItem = it.get("cart_items")

                    if (cartItem != null) {
                        cartItem = cartItem as List<*>
                        if (cartItem.size != 0) {
                            binding.btnShoppingBag.visibility = View.VISIBLE
                        }
                    }
                }
        } else {
            binding.btnShoppingBag.visibility = View.GONE
            binding.btnAddProduct.visibility = View.VISIBLE
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        role = "user"

        binding.swipeRefresh.setColorSchemeResources(R.color.yellow)

        load_data()

        binding.swipeRefresh.setOnRefreshListener {
            load_data()
        }

        productsRecyclerView = binding.rvMenu
        productsRecyclerView.setHasFixedSize(true)

        productsArrayList = arrayListOf()
        productsAdapter = ProductsAdapter(productsArrayList)

        productsRecyclerView.adapter = productsAdapter



        binding.btnShoppingBag.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            activity?.startActivity(intent)
        }

        if (role == "user") {
            binding.btnShoppingBag.visibility = View.GONE
            binding.btnAddProduct.visibility = View.GONE

            db.collection("users").document(uid).get()
                .addOnSuccessListener {
                    var cartItem = it.get("cart_items")

                    if (cartItem != null) {
                        cartItem = cartItem as List<*>
                        if (cartItem.size != 0) {
                            binding.btnShoppingBag.visibility = View.VISIBLE
                        }
                    }
                }
        } else {
            binding.btnShoppingBag.visibility = View.GONE
            binding.btnAddProduct.visibility = View.VISIBLE
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
        binding.rvMenu.visibility = View.INVISIBLE
        binding.swipeRefresh.isRefreshing = true

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

                fullProductsArrayList.clear()
                fullProductsArrayList.addAll(productsArrayList)

                binding.swipeRefresh.isRefreshing = false
                binding.rvMenu.visibility = View.VISIBLE
            }

    }

    private fun search_data(keyword :String) {
        binding.rvMenu.visibility = View.INVISIBLE
        binding.swipeRefresh.isRefreshing = true
        productsArrayList.clear()

        if (keyword != "") {
            productsArrayList.addAll(fullProductsArrayList.filter { product ->
                product.nama?.contains(keyword, true) ?: false
            } as ArrayList<Products>)
        } else {
            productsArrayList.addAll(fullProductsArrayList)
        }

        productsAdapter.notifyDataSetChanged()
        binding.swipeRefresh.isRefreshing = false
        binding.rvMenu.visibility = View.VISIBLE

//        val query = db.collection("products")
//            .orderBy("nama")
//            .startAt(keyword)
//            .get()
//        query.addOnSuccessListener {
//            productsArrayList.clear()
//            for (document in it) {
//                productsArrayList.add(document.toObject(Products::class.java))
//            }
//            binding.swipeRefresh.isRefreshing = false
//            binding.rvMenu.visibility = View.VISIBLE
//        }
    }

}