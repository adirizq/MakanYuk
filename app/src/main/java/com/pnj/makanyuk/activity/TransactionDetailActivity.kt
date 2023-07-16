package com.pnj.makanyuk.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.pnj.makanyuk.data.transaction.Transaction
import com.pnj.makanyuk.data.transaction.TransactionDetailItemAdaper
import com.pnj.makanyuk.data.transaction.TransactionItem
import com.pnj.makanyuk.data.transaction.TransactionItemAdapter
import com.pnj.makanyuk.databinding.ActivityTransactionDetailBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Locale
import java.util.TimeZone

class TransactionDetailActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss z", Locale("id", "ID"))

    private lateinit var binding: ActivityTransactionDetailBinding
    private lateinit var doc_id: String

    private val db = Firebase.firestore
    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        doc_id = intent.getStringExtra("DOC_ID").toString()

        populateData()

        binding.btnComplete.setOnClickListener {
            BeautifulDialog.build(this)
                .title("Selesaikan Pesanan?", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Silahkan klik tombol \"Ya\" jika anda yakin untuk menyelesaikan pesanan sekarang.",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ALERT)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = "Ya", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    db.collection("users").document(uid).collection("transactions")
                        .document(doc_id)
                        .update("status", "Selesai")
                        .addOnSuccessListener {
                            populateData()

                            BeautifulDialog.build(this)
                                .title("Berhasil", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                                .description("Pesanan berhasil diselesaikan",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                                .type(type= BeautifulDialog.TYPE.SUCCESS)
                                .position(BeautifulDialog.POSITIONS.CENTER)
                                .hideNegativeButton(true)
                                .onPositive(text = "Tutup", buttonBackgroundColor = R.drawable.bg_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                                    finish()
                                }
                        }
                }
                .onNegative(text = "Tidak", buttonBackgroundColor = R.drawable.bg_outline_yellow_rounded, textColor = ContextCompat.getColor(this, R.color.yellow), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                }
        }

        binding.btnDelete.setOnClickListener {
            BeautifulDialog.build(this)
                .title("Hapus Pesanan?", titleColor = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold))
                .description("Silahkan klik tombol \"Ya\" jika anda yakin untuk menghapus pesanan sekarang.",  color = ContextCompat.getColor(this, R.color.black), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_medium))
                .type(type= BeautifulDialog.TYPE.ALERT)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = "Ya", buttonBackgroundColor = R.drawable.bg_red_rounded, textColor = ContextCompat.getColor(this, R.color.white), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                    db.collection("users").document(uid).collection("transactions")
                        .document(doc_id)
                        .delete()
                        .addOnSuccessListener {
                            finish()
                        }
                }
                .onNegative(text = "Tidak", buttonBackgroundColor = R.drawable.bg_outline_red_rounded, textColor = ContextCompat.getColor(this, R.color.red), fontStyle = ResourcesCompat.getFont(this, R.font.poppins_bold)) {
                }


        }
    }

    private fun populateData() {
        binding.loading.visibility = View.VISIBLE

        db.collection("users").document(uid).collection("transactions").document(doc_id).get()
            .addOnSuccessListener { result ->
                val transaction = result.toObject(Transaction::class.java)
                val products = ArrayList<TransactionItem>()

                transaction!!.doc_id = result.id

                result.reference.collection("products")
                    .get()
                    .addOnSuccessListener { productsResults ->
                        for (productDocument in productsResults) {
                            val product = productDocument.toObject(TransactionItem::class.java)
                            products.add(product)
                        }
                        transaction.products = products

                        if(transaction.status == "Selesai") {
                            binding.btnComplete.visibility = View.GONE
                        }

                        binding.tvStatus.text = transaction.status
                        binding.tvInvoice.text = transaction.kode_transaksi
                        binding.tvDate.text = dateFormat.format(transaction.created_at!!.toDate())
                        binding.tvTotalPrice.text = NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transaction.total_price).toString().dropLast(3)

                        if(transaction.type == "Diantar") {
                            binding.rbDiantar.isChecked = true
                            binding.tvAlamatPengantaran.text = transaction.alamat_pengantaran
                            binding.tvAlamatPengantaran.visibility = View.VISIBLE
                            binding.uiAlamatPengantaran.visibility = View.VISIBLE
                        }else {
                            binding.rbAmbilSendiri.isChecked = true
                            binding.tvAlamatPengantaran.visibility = View.GONE
                            binding.uiAlamatPengantaran.visibility = View.GONE
                        }

                        val transactionDetailItemAdapter = transaction.products?.let { TransactionDetailItemAdaper(it) }
                        binding.rvItem.layoutManager = LinearLayoutManager(this)
                        binding.rvItem.adapter = transactionDetailItemAdapter
                        binding.rvItem.suppressLayout(true)

                        binding.loading.visibility = View.GONE

                    }.addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.i("Transaction Detail", it.message.toString())
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}