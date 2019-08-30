package com.cft.app.xmppapp.listener

import org.jivesoftware.smack.packet.Presence

interface PresenceChangeListener {

    fun onPresenceChange(presence: Presence)
}