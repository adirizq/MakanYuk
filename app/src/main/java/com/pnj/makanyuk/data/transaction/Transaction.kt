package com.pnj.makanyuk.data.transaction

import com.google.firebase.Timestamp
import com.google.firebase.database.PropertyName

data class Transaction(
    var doc_id: String,
    var kode_transaksi: String,
    var type: String,
    var status: String,
    var alamat_pengantaran: String,
    var total_price: Int,
    var created_at: Timestamp?,
    var products: ArrayList<TransactionItem>?,
){
    constructor() : this("", "", "", "", "",0, null, null)
}


