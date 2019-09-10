package com.cft.app.xmppapp.listener

// listener for the xmpp connection connected or failure
interface XMPPConnectionListener {

    fun onConnected(message:String)

    fun onError(message:String)
}