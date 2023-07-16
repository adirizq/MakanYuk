package com.pnj.makanyuk.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.products.Products
import com.pnj.makanyuk.data.products.ProductsAdapter
import com.pnj.makanyuk.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

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
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNew.visibility = View.INVISIBLE
        binding.loadingNew.visibility = View.VISIBLE

        productsArrayList = arrayListOf()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productsArrayList.clear()
                for (document in result ) {
                    val product = document.toObject(Products::class.java)
                    product.id = document.id
                    productsArrayList.add(product)
                }

                productsAdapter = ProductsAdapter(productsArrayList.shuffled().take(4) as ArrayList<Products>)

                binding.rvNew.setHasFixedSize(true)
                binding.rvNew.adapter = productsAdapter

                binding.loadingNew.visibility = View.GONE
                binding.rvNew.visibility = View.VISIBLE
            }

        val promoList = ArrayList<SlideModel>()
        promoList.add(SlideModel(R.drawable.promo_1))
        promoList.add(SlideModel(R.drawable.promo_2))
        promoList.add(SlideModel(R.drawable.promo_3))

        val blogList = ArrayList<SlideModel>()
        blogList.add(SlideModel(R.drawable.blog_1))
        blogList.add(SlideModel(R.drawable.blog_2))
        blogList.add(SlideModel(R.drawable.blog_3))

        val promoSlider = binding.promoSlider
        promoSlider.setImageList(promoList, ScaleTypes.CENTER_CROP)

        val blogSlider = binding.blogSlider
        blogSlider.setImageList(blogList, ScaleTypes.CENTER_CROP)

        binding.customerSupport.setOnClickListener {
            val item = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).menu.findItem(R.id.navigation_chat)
            NavigationUI.onNavDestinationSelected(item, view.findNavController())
        }
    }

}