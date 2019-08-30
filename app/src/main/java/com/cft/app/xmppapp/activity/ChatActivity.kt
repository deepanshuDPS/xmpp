package com.cft.app.xmppapp.activity

import android.os.Bundle
import android.util.Log
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.listener.IncomingMessageListener
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import kotlinx.android.synthetic.main.activity_chat.*
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.adapter.ChatAdapter
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.listener.PresenceChangeListener
import com.cft.app.xmppapp.model.ChatsModel
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.iqlast.LastActivityManager
import org.jivesoftware.smackx.mam.MamManager
import java.lang.Exception
import kotlin.collections.ArrayList


class ChatActivity : BaseActivity() {

    private var mamManager: MamManager? = null
    private var jid: Jid? = null
    private var jidString: String? = null
    private var chatAdapter: ChatAdapter? = null
    private var messagesList = ArrayList<ChatsModel>()
    private var recentChatMessages = 15
    private var allMessagesFetched = false

    private var onChatIncomingMessageListener = object : IncomingMessageListener {
        override fun onIncomingMessage(message: Message, from: Jid) {
            runOnUiThread {
                if (from == jid) {
                    messagesList.add(ChatsModel(message.body, from.toString(), false))
                    chatAdapter?.notifyDataSetChanged()
                    Log.d("info message", message.toXML("x").toString())
                    rv_chat_messages.scrollToPosition(messagesList.size - 1)
                }
                /*val delay =  message.getExtension("x", "urn:xmpp:delay") as DelayInformation
                displayToast(""+delay.stamp)*/
            }
        }

    }

    private var onPresenceChange = object : PresenceChangeListener {
        override fun onPresenceChange(presence: Presence) {
            runOnUiThread {
                if (presence.from.asBareJid() == jid) {
                    if (presence.type.name == "available")
                        toolbar_chat.subtitle = "online"
                    else {
                        try {
                            val lastActivityManager =
                                LastActivityManager.getInstanceFor(ManageConnections.xMPPConnection)
                            val lastActivityIdleTime =
                                lastActivityManager.getLastActivity(jid).idleTime
                            val lastSeenTimeInMillis =
                                System.currentTimeMillis() - lastActivityIdleTime * 1000
                            val currentTimeInMillis = System.currentTimeMillis()
                            setLastSeenTime(lastSeenTimeInMillis, currentTimeInMillis)
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mamManager = MamManager.getInstanceFor(ManageConnections.xMPPConnection)
        jidString = intent.extras?.getString("jid")
        jid = JidCreate.bareFrom(jidString)
        ManageConnections.onPresenceChangeListener = onPresenceChange
        onPresenceChange.onPresenceChange(ManageConnections.roster?.getPresence(jid?.asBareJid())!!)
        ManageConnections.onIncomingMessageListeners["chat_activity"] =
            onChatIncomingMessageListener
        setSupportActionBar(toolbar_chat)
        val chats = mamManager?.queryMostRecentPage(jid, recentChatMessages)?.messages
        toolbar_chat.title = Utilities.getNameFromJid(jidString!!)

        rv_chat_messages.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this, messagesList)
        rv_chat_messages.adapter = chatAdapter
        for (i in chats!!) {

            if (i.from.asBareJid() == jid && i.body != null)
                messagesList.add(ChatsModel(i.body, jidString!!, false))
            else if (i.body != null)
                messagesList.add(ChatsModel(i.body, AppPreferences.getUsername(baseContext), true))

        }
        rv_chat_messages.scrollToPosition(messagesList.size - 1)
        rv_chat_messages.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = (rv_chat_messages.layoutManager as LinearLayoutManager)
                if (layoutManager.findFirstCompletelyVisibleItemPosition() <= 3 && messagesList.size > 3 && !allMessagesFetched) {
                    //fetch next 15 messages
                    recentChatMessages += 15
                    val newChats = mamManager?.queryMostRecentPage(jid, recentChatMessages)?.messages!!
                    allMessagesFetched =  newChats.size < recentChatMessages
                    var to = 15
                    if(newChats.size < recentChatMessages)
                        to = newChats.size/recentChatMessages
                    for (i in 0..to) {
                        if (newChats[i].from.asBareJid() == jid && newChats[i].body != null)
                            messagesList.add(0,ChatsModel(newChats[i].body, jidString!!, false))
                        else if (newChats[i].body != null)
                            messagesList.add(0,ChatsModel(newChats[i].body, AppPreferences.getUsername(baseContext), true))
                    }
                    rv_chat_messages.smoothScrollToPosition(to)
                    chatAdapter?.notifyDataSetChanged()
                }
            }

        })

        rv_chat_messages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (oldBottom < bottom && messagesList.size > 0) {
                rv_chat_messages.scrollToPosition(messagesList.size - 1)
            }
        }

        bt_send.setOnClickListener {
            val message = getTextFromEditText(et_message)
            if (message.isNotEmpty()) {
                val jid = JidCreate.entityBareFrom(jid)
                val stanza = Message()
                stanza.body = message
                stanza.type = Message.Type.chat
                val chat = ManageConnections.chatManager?.chatWith(jid)
                chat?.send(stanza)
                messagesList.add(ChatsModel(message, AppPreferences.getUsername(baseContext), true))
                chatAdapter?.notifyDataSetChanged()
                ManageConnections.onIncomingMessageListeners["home_activity"]?.onIncomingMessage(
                    stanza,
                    jid
                )
                rv_chat_messages.scrollToPosition(messagesList.size - 1)
                et_message.text = null
            }
        }

        toolbar_chat.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onDestroy() {
        super.onDestroy()
        ManageConnections.onIncomingMessageListeners.remove("chat_activity")
    }

    private fun setLastSeenTime(lastSeenTimeInMillis: Long, currentTimeInMillis: Long) {
        try {
            val oneDay = 60 * 60 * 1000 * 24
            if (Utilities.findDate(lastSeenTimeInMillis) == Utilities.findDate(
                    currentTimeInMillis
                )
            )
                toolbar_chat.subtitle =
                    "last seen today at ${Utilities.findTime(lastSeenTimeInMillis)}"
            else if (Utilities.findDate(lastSeenTimeInMillis) == Utilities.findDate(
                    currentTimeInMillis - oneDay
                )
            )
                toolbar_chat.subtitle =
                    "last seen yesterday at ${Utilities.findTime(
                        lastSeenTimeInMillis
                    )}"
            else {
                var flag = true
                for (i in 2..6) {
                    if (Utilities.findDate(lastSeenTimeInMillis) == Utilities.findDate(
                            currentTimeInMillis - (i * oneDay)
                        )
                    ) {
                        toolbar_chat.subtitle =
                            "last seen ${Utilities.findDayFromTime(
                                lastSeenTimeInMillis
                            )} at ${Utilities.findTime(
                                lastSeenTimeInMillis
                            )}"
                        flag = false
                        break
                    }
                }
                if (flag)
                    toolbar_chat.subtitle =
                        "last seen on ${Utilities.findDateFromTime(
                            lastSeenTimeInMillis
                        )}"
            }
        } catch (e: Exception) {
        }
    }
}
