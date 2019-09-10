package com.cft.app.xmppapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.listener.OnMyClickListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_person.tv_name
import kotlinx.android.synthetic.main.item_group_user.*

class SelectedUsersAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
    private val onMyClickListener: OnMyClickListener
) : RecyclerView.Adapter<SelectedUsersAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jid = list[position]

        holder.tv_name.text = Utilities.getNameFromJid(jid)
        holder.iv_close.setOnClickListener {
            list.removeAt(position)
            if (list.size == 0) {
                AppPreferences.saveSelectedFriends(context, null)
                onMyClickListener.onMyClick(position)
            } else
                AppPreferences.saveSelectedFriends(context, list)
            notifyDataSetChanged()

        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer
}