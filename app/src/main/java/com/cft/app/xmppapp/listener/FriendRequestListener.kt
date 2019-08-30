package com.cft.app.xmppapp.listener

import org.jivesoftware.smack.roster.RosterEntry
import org.jxmpp.jid.Jid

interface FriendRequestListener {
    fun askForRequest(from: Jid,entries:Set<RosterEntry>?)
}