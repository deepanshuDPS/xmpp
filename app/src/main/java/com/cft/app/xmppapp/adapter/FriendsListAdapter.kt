package com.cft.app.xmppapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.listener.OnAllClickListener
import com.cft.app.xmppapp.model.FriendsDetailsModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_person.*

class FriendsListAdapter(
    private var context: Context,
    private var friendsList: ArrayList<FriendsDetailsModel>,
    private var onAllClickListener: OnAllClickListener
) : RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat_person, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = friendsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val details = friendsList[position]
        holder.tv_name.text = details.name
        holder.tv_first_char.text = details.name[0].toString()
        if (!details.clicked)
            holder.layout_friend.setBackgroundColor(Color.parseColor("#ffffff"))
        else
            holder.layout_friend.setBackgroundColor(
                ResourcesCompat.getColor(
                    context.resources,
                    R.color.grey,
                    null
                )
            )

        if (position % 2 == 0)
            holder.cv_background.setImageResource(R.color.color_to)
        else
            holder.cv_background.setImageResource(R.color.color_from)

        holder.ib_chat.setOnClickListener {
            onAllClickListener.onClick(position)
        }

        fun checkLayoutStatus() {

            if (details.clicked) {
                holder.layout_friend.setBackgroundColor(Color.parseColor("#ffffff"))
                details.clicked = false
            } else {
                holder.layout_friend.setBackgroundColor(
                    ResourcesCompat.getColor(
                        context.resources,
                        R.color.grey,
                        null
                    )
                )
                details.clicked = true
            }
        }

        holder.containerView.setOnClickListener {
            for (i in friendsList) {
                if (i.clicked) {
                    checkLayoutStatus()
                    break
                }
            }
            onAllClickListener.onItemClick(position)
        }

        holder.containerView.setOnLongClickListener {
            checkLayoutStatus()
            onAllClickListener.onItemLongClick(position)
            return@setOnLongClickListener true
        }
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer
}