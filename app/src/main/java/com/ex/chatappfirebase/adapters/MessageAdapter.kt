package com.ex.chatappfirebase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(
    val context: Context,
    val messageList: List<Message>,
    val imageUrl: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private lateinit var firebaseAuth : FirebaseAuth

    inner class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var profile_image : CircleImageView?= null
        var show_text : TextView?= null
        var left_image_photo : ImageView?= null
        var right_image_photo : ImageView ?= null
        var text_seen_or_not : TextView ?= null
        init{
            profile_image = itemView.findViewById(R.id.profile_image_left)
            show_text = itemView.findViewById(R.id.show_text_message)
            left_image_photo = itemView.findViewById(R.id.left_image_view)
            right_image_photo = itemView.findViewById(R.id.right_image_view)
            text_seen_or_not = itemView.findViewById(R.id.text_seen)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            if(viewType == 1){
                return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item_right,parent,false))
            }else{
                return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_item_left,parent,false))
            }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val message_c : Message = messageList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        if(message_c.messageId.equals("sent you an image.") && !message_c.url.equals("")){
            //image message right side
            if(message_c.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                holder.show_text!!.visibility = View.GONE
                holder.right_image_photo!!.visibility = View.GONE

                Picasso.get().load(message_c.url).into(holder.right_image_photo)
            }//image message left side
            else if(message_c.sender.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                holder.show_text!!.visibility = View.GONE
                holder.left_image_photo!!.visibility = View.VISIBLE

                Picasso.get().load(message_c.url).into(holder.left_image_photo)
            }
        }
        //text messages
        else{
            holder.show_text!!.text = message_c.messageId
        }
        //sent and seen message
        if(position == messageList.size-1){
            if(message_c.isseen.toBoolean()){
                holder.text_seen_or_not!!.text = "Seen"
                if(message_c.messageId.equals("sent you an image.") && !message_c.url.equals("")){
                    val lp : RelativeLayout.LayoutParams ?= holder.text_seen_or_not!!.layoutParams as RelativeLayout.LayoutParams ?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen_or_not!!.layoutParams = lp
                }
            }else{
                holder.text_seen_or_not!!.text = "Sent"
                if(message_c.messageId.equals("sent you an image.") && !message_c.url.equals("")){
                    val lp : RelativeLayout.LayoutParams ?= holder.text_seen_or_not!!.layoutParams as RelativeLayout.LayoutParams ?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen_or_not!!.layoutParams = lp
                }
            }
        }else{
            holder.text_seen_or_not!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseAuth = Firebase.auth
        if(messageList[position].sender.equals(firebaseAuth.currentUser!!.uid)){
            return 1
        }else{
            return 0
        }
    }
}