package com.pnj.makanyuk.data.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cart_item")

data class CartItem(
    @PrimaryKey val doc_id: String,
    @ColumnInfo("img_url") var imgUrl: String = "",
    @ColumnInfo("name") var name: String = "",
    @ColumnInfo("price") var price: Int = 0,
    @ColumnInfo("portion") var portion: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "img_url" to imgUrl,
            "name" to name,
            "price" to price,
            "portion" to portion
        )
    }
}
