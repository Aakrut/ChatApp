package com.ex.chatappfirebase.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val messageList : List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private lateinit var firebaseAuth: FirebaseAuth
    class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        var message : TextView ?= null

        init {

            message = itemView.findViewById(R.id.text_message_chat)

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


        holder.message!!.text = message.message

        holder.itemView.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert")
            builder.setMessage("Do You Want to Delete This Message")

            builder.setPositiveButton("Yes") { dialog, which ->

                val db = Firebase.firestore
                db.collection("ChatLists").document(message.message_id).delete().addOnSuccessListener {
                    Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show()
            }
                Toast.makeText(context,
                        android.R.string.yes, Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { dialog, which ->
                Toast.makeText(context,
                        android.R.string.no, Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }

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