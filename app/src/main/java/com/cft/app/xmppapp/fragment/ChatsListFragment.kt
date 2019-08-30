package com.cft.app.xmppapp.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.activity.BaseActivity
import com.cft.app.xmppapp.activity.ChatActivity
import com.cft.app.xmppapp.adapter.ChatListAdapter
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.listener.IncomingMessageListener
import com.cft.app.xmppapp.listener.OnMyClickListener
import com.cft.app.xmppapp.model.ChatListModel
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import kotlinx.android.synthetic.main.fragment_chat_list.*
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.mam.MamManager
import org.jxmpp.jid.Jid

class ChatsListFragment : Fragment() {



    private var onIncomingMessageListener = object : IncomingMessageListener {
        override fun onIncomingMessage(message: Message, from: Jid) {

            baseActivity?.runOnUiThread {
                          //displayToast("${message.body} from ${Utilities.getNameFromJid(from)}")
                chatListAdapter?.shiftToFirst(Utilities.getNameFromJid(from),from.toString(),message.body)
            }
        }
    }

    private var chatListAdapter: ChatListAdapter? = null
    private var chatsList = ArrayList<ChatListModel>()
    private var baseActivity: BaseActivity? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = activity as BaseActivity
        ManageConnections.onIncomingMessageListeners["home_activity"] = onIncomingMessageListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_chats_list.layoutManager = LinearLayoutManager(baseActivity)
        chatListAdapter = ChatListAdapter(baseActivity!!, chatsList, onMyClickListener)
        rv_chats_list.adapter = chatListAdapter
        createChatListFromRoster()
    }

    private fun createChatListFromRoster() {
        val mamManager = MamManager.getInstanceFor(ManageConnections.xMPPConnection)
        val entries = ManageConnections.roster?.entries!!
        for (entry in entries) {
            if (entry.type.name != "none") {
                val chat = mamManager.queryMostRecentPage(entry.jid, 1).messages
                //Log.d("Check Message", chat[0].body)
                if (chat.size != 0 && chat[0].body != null)
                    chatsList.add(
                        ChatListModel(
                            Utilities.getNameFromJid(entry.jid),
                            entry.jid.toString(),
                            chat[0].body.toString()
                        )
                    )
            }

        }
        chatListAdapter?.notifyDataSetChanged()
    }


    private val onMyClickListener = object : OnMyClickListener {
        override fun onMyClick(position: Int) {
            val bundle = Bundle()
            bundle.putString("jid", chatsList[position].jid)
            baseActivity?.switchActivity(ChatActivity::class.java, bundle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ManageConnections.onIncomingMessageListeners.remove("home_activity")
    }

}
