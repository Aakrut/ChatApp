package com.ex.chatappfirebase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val messageList : List<Message>,val image : String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private lateinit var firebaseAuth: FirebaseAuth
    class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var circleImageView_chat : CircleImageView
        var message_left : TextView
        var time_left : TextView
        var message_right : TextView
        var time_right : TextView

        init {
            circleImageView_chat = itemView.findViewById(R.id.receiver_image_left)
            message_left = itemView.findViewById(R.id.text_message_chat_left)
            message_right = itemView.findViewById(R.id.text_message_chat_right)
            time_left = itemView.findViewById(R.id.time_text_chat_left)
            time_right = itemView.findViewById(R.id.time_text_chat_right)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
         if(viewType == 1){
            return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_right,parent,false))
        }else{
            return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_left,parent,false))
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message : Message = messageList[position]


        holder.message_left.text = message.message
        holder.message_right.text = message.message
        holder.time_left.text = message.time.toInt().toString()
        holder.time_right.text = message.time.toInt().toString()

        Picasso.get().load(image).into(holder.circleImageView_chat)

    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseAuth = Firebase.auth
        return if(messageList[position].sender == firebaseAuth.currentUser!!.uid){
            1
        }else{
            0
        }
    }
}