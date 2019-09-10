package com.cft.app.xmppapp.listener

import org.jivesoftware.smack.packet.Presence

// if any roaster user presence changes then this listener called
interface PresenceChangeListener {
    fun onPresenceChange(presence: Presence)
}