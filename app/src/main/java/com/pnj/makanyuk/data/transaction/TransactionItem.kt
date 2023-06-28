package com.pnj.makanyuk.data.transaction

import com.google.firebase.database.PropertyName

data class TransactionItem(
    @PropertyName("name") var name: String,
    @PropertyName("portion") var portion: Int,
    @PropertyName("img_url") var imgUrl: String
)
