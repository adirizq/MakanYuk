package com.pnj.makanyuk.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.hideNegativeButton
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.cart.CartItem
import com.pnj.makanyuk.data.cart.CartItemAdapter
import com.pnj.makanyuk.databinding.ActivityCartBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CartActivity : AppCompatActivity(), CartItemAdapter.OnAddSubtractPortionClickListener {

    private lateinit var binding: ActivityCartBinding

    private val dateFormat = SimpleDateFormat("yyyyMMdd/HHmmss", Locale("id", "ID"))

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var totalPrice: Int = 0

    private lateinit var cartItemList: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        binding = ActivityCartBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        binding.rvItemCart.layoutManager = LinearLayoutManager(this)

        cartItemList = arrayListOf<CartItem>()

        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                val cartItem = it.get("cart_items")

                if (cartItem != null) {
                    val cartItemMap = cartItem as MutableList<MutableMap<String, Any>>

                    cartItemList.addAll(
                        cartItemMap.map { hashMap ->
                            CartItem(hashMap["img_url"] as String, hashMap["name"] as String, (hashMap["price"] as Long).toInt(), (hashMap["portion"] as Long).toInt())
                        }
                    )
                }

                updateTotalPrice()

                binding.rvItemCart.adapter = CartItemAdapter(cartItemList, this@CartActivity, db, uid)
            }

//        lifecycleScope.launch {
//            cartItemList = cartItemDB.getCartItemDao().getAllCartItem()
//
//        }

        binding.rbAmbilSendiri.setOnCheckedChangeListener { compoundButton, b ->
            if (binding.rbAmbilSendiri.isChecked) {
                binding.tvAlamatPengantaran.visibility = View.GONE
                binding.edtAddress.visibility = View.GONE
            } else {
                binding.tvAlamatPengantaran.visibility = View.VISIBLE
                binding.edtAddress.visibility = View.VISIBLE
            }
        }

        binding.btnPesan.setOnClickListener {

            BeautifulDialog.build(this)
                .title("Buat Pesanan?", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Silahkan klik tombol \"Ya\" jika anda yakin untuk memesan sekarang.",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ALERT)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = "Ya", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    saveTransaction(uid)
                }
                .onNegative(text = "Tidak", buttonBackgroundColor = R.drawable.bg_outline_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.yellow), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                }

        }
    }

    private fun saveTransaction(userId: String) {
        updateTotalPrice()

        var type = ""
        var typeInvoice = ""
        var alamatPengantaran = ""

        if (binding.rbAmbilSendiri.isChecked) {
            type = "Ambil Sendiri"
            typeInvoice = "P"
        } else {
            type = "Diantar"
            typeInvoice = "D"
            alamatPengantaran = binding.edtAddress.text.toString()

            if(alamatPengantaran == "") {
                BeautifulDialog.build(this)
                    .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                    .description("Harap isi semua kolom",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                    .type(type= BeautifulDialog.TYPE.ERROR)
                    .position(BeautifulDialog.POSITIONS.CENTER)
                    .hideNegativeButton(true)
                    .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                    }

                return
            }
        }

        if(totalPrice == 0) {
            BeautifulDialog.build(this)
                .title("Gagal", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Harap isi semua kolom",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ERROR)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .hideNegativeButton(true)
                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold), shouldIDismissOnClick = true) {
                }

            return
        }

        val createdAt = Calendar.getInstance().time
        val formatedDateTimeNow = dateFormat.format(createdAt).split('/')
        val kodeTransaksi = "INV/${formatedDateTimeNow[0]}/${typeInvoice}/${userId.subSequence(0, 7)}/${formatedDateTimeNow[1]}"

        val status = "Dalam Proses"

        val transaction: MutableMap<String, Any> = HashMap()
        transaction["kode_transaksi"] = kodeTransaksi
        transaction["type"] = type
        transaction["status"] = status
        transaction["alamat_pengantaran"] = alamatPengantaran
        transaction["total_price"] = totalPrice
        transaction["created_at"] = Timestamp(createdAt)

        db.collection("users").document(userId).collection("transactions")
            .add(transaction)
            .addOnSuccessListener { documentReference ->
                for(p in cartItemList) {
                    db.collection("users").document(userId)
                        .collection("transactions")
                        .document(documentReference.id)
                        .collection("products")
                        .add(p)
                        .addOnSuccessListener {
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener {
                                    val cartItemList = mutableListOf<MutableMap<String, Any>>()
                                    db.collection("users").document(uid).update(hashMapOf("cart_items" to cartItemList) as Map<String, Any>).addOnSuccessListener {
                                        BeautifulDialog.build(this)
                                            .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                                            .description("Pesanan berhasil dibuat",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                                            .type(type= BeautifulDialog.TYPE.SUCCESS)
                                            .position(BeautifulDialog.POSITIONS.CENTER)
                                            .hideNegativeButton(true)
                                            .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                                                finish()
                                            }
                                    }
                                }


                        }
                }
            }
    }

    fun updateTotalPrice() {
        binding.tvTotalPrice.text = "..."

        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                cartItemList.clear()
                totalPrice = 0

                val cartItem = it.get("cart_items")

                if (cartItem != null) {
                    val cartItemMap = cartItem as MutableList<MutableMap<String, Any>>

                    cartItemList.addAll(
                        cartItemMap.map { hashMap ->
                            CartItem(hashMap["img_url"] as String, hashMap["name"] as String, (hashMap["price"] as Long).toInt(), (hashMap["portion"] as Long).toInt())
                        }
                    )
                }

                for (item in cartItemList) {
                    totalPrice += (item.price * item.portion)
                }

                binding.tvTotalPrice.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(totalPrice).toString().dropLast(3)

            }
    }

    override fun onAddSubtractPortionClick() {
        updateTotalPrice()
    }
}

