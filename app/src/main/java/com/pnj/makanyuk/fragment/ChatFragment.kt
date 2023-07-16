package com.pnj.makanyuk.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pnj.makanyuk.R
import com.pnj.makanyuk.data.chat.Chat
import com.pnj.makanyuk.data.chat.ChatAdapter
import com.pnj.makanyuk.data.chat.SharedViewModel
import com.pnj.makanyuk.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var database: DatabaseReference

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var chatRecycleView: RecyclerView
    private lateinit var chatArrayList: ArrayList<Chat>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currUserEmail: String

    private val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val db = FirebaseFirestore.getInstance()
    val personQuery = db.collection("users").document(uid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()

        chatRecycleView = binding.rvChat
        chatRecycleView.layoutManager = LinearLayoutManager(context)
        chatRecycleView.setHasFixedSize(true)

        chatArrayList = arrayListOf()

        personQuery.get()
            .addOnSuccessListener {
                currUserEmail = it.getString("email").toString()
                chatAdapter = ChatAdapter(chatArrayList, currUserEmail)
                chatRecycleView.adapter = chatAdapter

                loadChatData(currUserEmail)
            }

        binding.btnSend.setOnClickListener {
            val message = binding.edtChat.text.toString()

            personQuery.get()
                .addOnSuccessListener {document ->
                    if (document != null) {
                        // Get the data for the document
                        val username = document.getString("username").toString()
                        val name = document.getString("name").toString()
                        val email = document.getString("email").toString()

                        send_chat(username, message, name, email)
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }

        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun loadChatData(email: String) {
        binding.loading.visibility = View.VISIBLE

        database = FirebaseDatabase.getInstance().getReference("chat_db")
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    chatArrayList.clear()
                    for (chatSnapshoot in snapshot.children) {
                        val chat_data = chatSnapshoot.getValue(Chat::class.java)
                        chatArrayList.add(chat_data!!)
                    }
                    chatRecycleView.adapter = ChatAdapter(chatArrayList, email)
                }
                binding.loading.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun send_chat(username: String, message: String, name: String, email: String) {
        val time  = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = formatter.format(time).toString()

        val chat = Chat(username, message, name, email, currentTime)

        database = FirebaseDatabase.getInstance().getReference("chat_db")

        database.child(currentTime).setValue(chat).addOnSuccessListener {
            binding.edtChat.text.clear()
        }
            .addOnFailureListener {
                Toast.makeText(context, "Kirim Chat Gagal", Toast.LENGTH_SHORT).show()
                Log.e("gagal", "kirim gagal")
            }
    }

}