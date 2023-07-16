package com.pnj.makanyuk.data.products

data class Products(
    var id : String?= null,
    var nama : String?= null,
    var harga : Int?= null,
    var deskripsi : String?= null,
    val img_product : String?= null,
)
