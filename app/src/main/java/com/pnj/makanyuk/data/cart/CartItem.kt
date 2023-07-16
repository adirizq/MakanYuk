package com.pnj.makanyuk.data.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.pnj.makanyuk.data.transaction.TransactionItem
import java.io.Serializable

data class CartItem(
    var img_url: String,
    var name: String,
    var price: Int,
    var portion: Int
){

    constructor() : this("", "", 0, 0)
}
