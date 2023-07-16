package com.pnj.makanyuk.data.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pnj.makanyuk.R
import com.pnj.makanyuk.activity.CartActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartItemAdapter(
    private val cartItemList: ArrayList<CartItem>,
    private val listener: OnAddSubtractPortionClickListener,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val cartItemDB: CartItemDatabase
    ): RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.cart_item_img)
        val itemName: TextView = itemView.findViewById(R.id.tv_name)
        val price: TextView = itemView.findViewById(R.id.tv_price)

        val addPortionBtn: ImageView = itemView.findViewById(R.id.btn_add_portion)
        val subtractPortionBtn: ImageView = itemView.findViewById(R.id.btn_subtract_portion)
        val edtPortion: EditText = itemView.findViewById(R.id.edt_portion)

        var currPortion = 0

        init {
            addPortionBtn.setOnClickListener {
                currPortion += 1
                edtPortion.setText(currPortion.toString())

                val position = adapterPosition

                lifecycleScope.launch {
                    val cartItem = CartItem(cartItemList[position].doc_id, cartItemList[position].imgUrl, cartItemList[position].name, cartItemList[position].price, currPortion)
                    cartItemDB.getCartItemDao().updateCartItem(cartItem)
                    listener.onAddSubtractPortionClick()
                }
            }

            subtractPortionBtn.setOnClickListener {
                if(currPortion > 0) {
                    currPortion -= 1
                } else {
                    currPortion = 0
                }

                edtPortion.setText(currPortion.toString())

                val position = adapterPosition

                lifecycleScope.launch {
                    val cartItem = CartItem(cartItemList[position].doc_id, cartItemList[position].imgUrl, cartItemList[position].name, cartItemList[position].price, currPortion)
                    cartItemDB.getCartItemDao().updateCartItem(cartItem)
                    listener.onAddSubtractPortionClick()
                }
            }
        }
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
        holder.price.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(currentItem.price).toString().dropLast(3)

        Glide.with(holder.itemView.context)
            .load(currentItem.imgUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.img)
    }

    interface OnAddSubtractPortionClickListener {
        fun onAddSubtractPortionClick()
    }
}