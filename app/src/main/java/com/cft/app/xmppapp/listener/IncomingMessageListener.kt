package com.cft.app.xmppapp.listener

import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.Jid

// listener for when the incoming message arrived from users
interface IncomingMessageListener {
    fun onIncomingMessage(message: Message,from: Jid)
}