package com.cft.app.xmppapp.xmpp_connections

import android.util.Log
import com.cft.app.xmppapp.app_helper.AppConstants
import com.cft.app.xmppapp.listener.*
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.bosh.BOSHConfiguration
import org.jivesoftware.smack.bosh.XMPPBOSHConnection
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.roster.*
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jxmpp.jid.DomainBareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import com.cft.app.xmppapp.listener.IncomingMessageListener
import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.parts.Resourcepart
import org.jxmpp.jid.util.JidUtil
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation
import org.jxmpp.jid.BareJid


object ManageConnections {

    var mucManager: MultiUserChatManager? = null
    var boshConfiguration: BOSHConfiguration? = null
    var xMPPConnection: XMPPBOSHConnection? = null
    var isConnected = false
    var roster: Roster? = null
    var chatManager: ChatManager? = null
    var friendRequestListener: FriendRequestListener? = null
    var onRosterChangeListener: RosterChangeListener? = null
    var onIncomingMessageListeners = LinkedHashMap<String, IncomingMessageListener>()
    var onPresenceChangeListener: PresenceChangeListener? = null

    fun setConnection(
        username: String,
        password: String,
        xMPPConnectionListener: XMPPConnectionListener
    ) {

        val connectionThread = object : Thread() {
            override fun run() {
                boshConfiguration = BOSHConfiguration.builder()
                    .setHost(AppConstants.HOST)
                    .setUsernameAndPassword(username, password)
                    .setPort(AppConstants.PORT)
                    .setFile("/http-bind")
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setXmppDomain(getServiceName())
                    .build()

                xMPPConnection = XMPPBOSHConnection(boshConfiguration)
                try {

                    xMPPConnection!!.connect()
                    if (xMPPConnection!!.isConnected) {
                        xMPPConnection!!.login()
                    }

                    if (xMPPConnection!!.isAuthenticated) {
                        isConnected = true
                        xMPPConnectionListener.onConnected("Connected")
                        setRosterAndMucManager()
                        setChatMessenger()
                    }
                } catch (e: Exception) {
                    isConnected = false
                    xMPPConnectionListener.onConnected("Error")
                }
            }
        }

        connectionThread.start()

    }

    private fun setRosterAndMucManager() {
        roster = Roster.getInstanceFor(xMPPConnection)
        mucManager = MultiUserChatManager.getInstanceFor(xMPPConnection)
        //roster?.subscriptionMode = Roster.SubscriptionMode.accept_all
        roster?.addRosterListener(object : RosterListener {
            override fun entriesDeleted(addresses: MutableCollection<Jid>) {
                Log.d("roster", "deleted")
                onRosterChangeListener?.onRosterChange()
            }

            override fun presenceChanged(presence: Presence) {
                Log.d("roster", "presence Changed")
                //onRosterChangeListener?.onRosterChange()
                onPresenceChangeListener?.onPresenceChange(presence)
            }

            override fun entriesUpdated(addresses: MutableCollection<Jid>) {
                Log.d("roster", "updated $addresses")
                onRosterChangeListener?.onRosterChange()
            }

            override fun entriesAdded(addresses: MutableCollection<Jid>) {
                Log.d("roster", "added")
                onRosterChangeListener?.onRosterChange()
            }
        })


        roster?.addSubscribeListener { from, _ ->
            friendRequestListener?.askForRequest(from, roster?.entries)
            Log.d("request:", "accepted")
            null
        }


    }


    private fun setChatMessenger() {

        chatManager = ChatManager.getInstanceFor(xMPPConnection)

        chatManager?.addIncomingListener { from, message, chat ->

            if (onIncomingMessageListeners.size == 1)
                onIncomingMessageListeners["home_activity"]?.onIncomingMessage(message!!, from!!)
            else if (onIncomingMessageListeners.size == 2) {
                onIncomingMessageListeners["chat_activity"]?.onIncomingMessage(message!!, from!!)
                onIncomingMessageListeners["home_activity"]?.onIncomingMessage(message!!, from!!)
            }
        }
    }

    fun createGroup(groupName: String, participants: MutableSet<String>) {

        /*val mucJid = JidCreate.entityBareFrom(groupName + AppConstants.GROUP_CHAT_QUERY)
        val nickname = Resourcepart.from(groupName)
        val muc = mucManager?.getMultiUserChat(mucJid)
        muc?.create(nickname)
        val form = muc?.configurationForm?.createAnswerForm()
        //form?.setAnswer("muc#roomconfig_roomowners", ArrayList<String>(participants))
        muc?.sendConfigurationForm(form)
        roster?.createGroup(mucJid.toString())

        for (i in participants) {
            val message = Message(JidCreate.bareFrom(i))
            message.addExtension(GroupChatInvitation(mucJid.toString()))
            xMPPConnection?.sendStanza(message)
        }*/

    }

    private fun getServiceName(): DomainBareJid? {
        var serviceName: DomainBareJid? = null
        try {
            serviceName = JidCreate.domainBareFrom(AppConstants.JID)
        } catch (e: XmppStringprepException) {
            e.printStackTrace()
        }
        return serviceName
    }
}