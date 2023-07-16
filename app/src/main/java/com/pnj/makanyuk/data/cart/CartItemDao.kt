package com.pnj.makanyuk.data.cart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_item")
    suspend fun getAllCartItem(): ArrayList<CartItem>

    @Insert
    suspend fun insertCartItem(cartItem: CartItem)

    @Update(entity = CartItem::class)
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)
}