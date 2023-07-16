package com.pnj.makanyuk.data.transaction

import com.google.firebase.database.PropertyName

data class TransactionItem(
    var name: String,
    var portion: Int,
    var price: Int,
    var img_url: String
) {
    constructor(): this("", 0, 0, "")
}
