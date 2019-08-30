package com.cft.app.xmppapp.listener

import org.jivesoftware.smack.packet.Message
import org.jxmpp.jid.Jid

interface IncomingMessageListener {

    fun onIncomingMessage(message: Message,from: Jid)
}