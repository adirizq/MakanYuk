package com.pnj.makanyuk.data.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pnj.makanyuk.R

class ChatAdapter(private  val chatList: ArrayList<Chat>):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        var R_email = email.toString()

        var username: TextView? = null
        var time_chat: TextView? = null
        var chat_content: TextView? = null

        val outgoingChatView = itemView.findViewById<ConstraintLayout>(R.id.outgoing_chat)
        val incomingChatView = itemView.findViewById<ConstraintLayout>(R.id.incoming_chat)

        init {
            val femail = data_mail()
            if (femail == R_email) {
                outgoingChatView.visibility = View.VISIBLE
                incomingChatView.visibility = View.GONE

                username = itemView.findViewById(R.id.tv_incoming_chat_name)
                time_chat = itemView.findViewById(R.id.tv_outgoing_chat_time)
                chat_content = itemView.findViewById(R.id.tv_outgoing_chat_content)
            } else {
                outgoingChatView.visibility = View.GONE
                incomingChatView.visibility = View.VISIBLE

                username = itemView.findViewById(R.id.tv_incoming_chat_name)
                time_chat = itemView.findViewById(R.id.tv_incoming_chat_time)
                chat_content = itemView.findViewById(R.id.tv_incoming_chat_content)
            }
        }

        fun data_mail(): String {
            var femail = ""
            val uid = "nAW7uzigeaV898EC9LzK6DC3jAR2" //mhs@gmail.com
            val db = FirebaseFirestore.getInstance()
            val personQuery = db.collection("users").document(uid)

            personQuery.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Get the data for the document
                        femail = document.getString("email").toString()
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
//                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                }
            return femail
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_bubble, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatList.size

    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat: Chat = chatList[position]
        holder.username?.text = chat.username
        holder.time_chat?.text = chat.time
        holder.chat_content?.text = chat.message
    }



}