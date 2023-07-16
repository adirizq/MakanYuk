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

class ChatAdapter(private val chatList: ArrayList<Chat>, private val currUserEmail: String):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val outContent: TextView = itemView.findViewById(R.id.tv_outgoing_chat_content)
        val outTime: TextView = itemView.findViewById(R.id.tv_outgoing_chat_time)

        val inName: TextView = itemView.findViewById(R.id.tv_incoming_chat_name)
        val inUsername: TextView = itemView.findViewById(R.id.tv_incoming_chat_username)
        val inContent: TextView = itemView.findViewById(R.id.tv_incoming_chat_content)
        val inTime: TextView = itemView.findViewById(R.id.tv_incoming_chat_time)

        val outgoingChatView = itemView.findViewById<ConstraintLayout>(R.id.outgoing_chat)
        val incomingChatView = itemView.findViewById<ConstraintLayout>(R.id.incoming_chat)

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

        if (currUserEmail == chat.email) {
            holder.outgoingChatView.visibility = View.VISIBLE
            holder.incomingChatView.visibility = View.GONE

            holder.outContent.text = chat.message.toString()
            holder.outTime.text = chat.time.toString()
        } else {
            holder.outgoingChatView.visibility = View.GONE
            holder.incomingChatView.visibility = View.VISIBLE

            holder.inName.text = chat.name.toString()
            holder.inUsername.text = "@" + chat.username.toString()
            holder.inContent.text = chat.message.toString()
            holder.inTime.text = chat.time.toString()
        }
    }
}