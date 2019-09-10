package com.cft.app.xmppapp.listener

import org.jxmpp.jid.Jid

// listener for when the friend request arrives from any user
interface FriendRequestListener {
    fun askForRequest(from: Jid)
}