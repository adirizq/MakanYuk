package com.pnj.makanyuk.data.transaction

import com.google.firebase.Timestamp
import com.google.firebase.database.PropertyName

data class Transaction(
    @PropertyName("type") var type: String? = null,
    @PropertyName("status") var status: String? = null,
    @PropertyName("total_price") var totalPrice: Int? = null,
    @PropertyName("created_at") var createdAt: Timestamp? = null,
    @PropertyName("products") var products: ArrayList<TransactionItem>? = null,
)
