package com.cft.app.xmppapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.model.ChatsModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chats.*

class ChatAdapter(private var context: Context,
                  private var messagesList: ArrayList<ChatsModel>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_chats,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = messagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chatsModel = messagesList[position]
        if(chatsModel.type){
            holder.tv_message_send.visibility = View.VISIBLE
            holder.tv_message_received.visibility = View.GONE
            holder.tv_message_send.text = chatsModel.message
        }
        else{
            holder.tv_message_send.visibility = View.GONE
            holder.tv_message_received.visibility = View.VISIBLE
            holder.tv_message_received.text = chatsModel.message
        }
    }


    inner class ViewHolder(override val containerView:View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
    }
}