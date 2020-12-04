package com.ex.chatappfirebase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ex.chatappfirebase.R
import com.ex.chatappfirebase.data.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context,val mUserList : List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var circle_image : CircleImageView
        var username_text : TextView
        var fullname_text : TextView

        init {
            circle_image = itemView.findViewById(R.id.circle_image_search_item_list)
            username_text = itemView.findViewById(R.id.username_text_search_item_list)
            fullname_text = itemView.findViewById(R.id.fullname_text_search_item_list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
       return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_item_list,parent,false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user : User = mUserList[position]
            holder.username_text.text = user.username
            holder.fullname_text.text = user.fullname

        Picasso.get().load(user.photo_profile).into(holder.circle_image)
    }

    override fun getItemCount(): Int {
        return mUserList.size
    }
}