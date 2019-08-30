package com.cft.app.xmppapp.listener

interface XMPPConnectionListener {

    fun onConnected(message:String)

    fun onError(message:String)
}