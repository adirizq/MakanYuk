package com.pnj.makanyuk.data.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.CartActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartItemAdapter(
    private val cartItemList: ArrayList<CartItem>,
    private val listener: OnAddSubtractPortionClickListener,
    private val db: FirebaseFirestore,
    private val uid: String,
    ): RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    private var currPortion = 0

    inner class CartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.cart_item_img)
        val itemName: TextView = itemView.findViewById(R.id.tv_name)
        val price: TextView = itemView.findViewById(R.id.tv_price)
        val edtPortion: EditText = itemView.findViewById(R.id.edt_portion)

        val addPortionBtn: ImageView = itemView.findViewById(R.id.btn_add_portion)
        val subtractPortionBtn: ImageView = itemView.findViewById(R.id.btn_subtract_portion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem = cartItemList[position]

        holder.itemName.text = currentItem.name
        holder.edtPortion.setText(currentItem.portion.toString())
        holder.price.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(currentItem.price).toString().dropLast(3)

        Glide.with(holder.itemView.context)
            .load(currentItem.img_url)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.img)

        holder.addPortionBtn.setOnClickListener {
            currentItem.portion += 1

            updatePortion(currentItem.portion, position, holder)
        }

        holder.subtractPortionBtn.setOnClickListener {
            if(currentItem.portion > 0) {
                currentItem.portion -= 1
            } else {
                currentItem.portion = 0
            }

            updatePortion(currentItem.portion, position, holder)
        }
    }

    interface OnAddSubtractPortionClickListener {
        fun onAddSubtractPortionClick()
    }

    private fun updatePortion(newPortion: Int, position: Int, holder: CartItemViewHolder) {

        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                val cartItem = it.get("cart_items")
                val listForUpdate = mutableListOf<MutableMap<String, Any>>()

                cartItemList.clear()

                if (cartItem != null) {
                    listForUpdate.addAll(cartItem as MutableList<MutableMap<String, Any>>)

                    val cartItemMap = cartItem as MutableList<MutableMap<String, Any>>

                    cartItemList.addAll(
                        cartItemMap.map { hashMap ->
                            CartItem(hashMap["img_url"] as String, hashMap["name"] as String, (hashMap["price"] as Long).toInt(), (hashMap["portion"] as Long).toInt())
                        }
                    )
                }

                listForUpdate[position]["portion"] = newPortion
                cartItemList[position].portion = newPortion

                if(newPortion == 0) {
                    cartItemList.removeAt(position)
                    listForUpdate.removeAt(position)
                }

                db.collection("users").document(uid).update(hashMapOf("cart_items" to listForUpdate) as Map<String, Any>).addOnSuccessListener {
                    holder.edtPortion.setText(newPortion.toString())
                    listener.onAddSubtractPortionClick()

                    if(newPortion == 0) {
                        notifyDataSetChanged()
                    }
                }
            }
    }
}