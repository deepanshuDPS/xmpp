package com.cft.app.xmppapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.listener.OnMyClickListener
import com.cft.app.xmppapp.model.ChatListModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_info.*

class ChatListAdapter(
    private var context: Context,
    private var chatList: ArrayList<ChatListModel>,
    private var onMyClickListener: OnMyClickListener
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = chatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val details = chatList[position]
        holder.tv_name.text = details.name
        holder.tv_first_char.text = details.name[0].toString()
        holder.tv_message.text = details.lastMessage

        holder.containerView.setOnClickListener {
            onMyClickListener.onMyClick(position)
        }
    }


    fun shiftToFirst(name: String, jid: String, lastMessage: String) {

        val chat = ChatListModel(name, jid, lastMessage)
        for ((index, value) in chatList.withIndex()) {
            if (jid == value.jid) {
                chatList.removeAt(index)
                break
            }
        }
        chatList.add(0, chat)
        notifyDataSetChanged()
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    }
}