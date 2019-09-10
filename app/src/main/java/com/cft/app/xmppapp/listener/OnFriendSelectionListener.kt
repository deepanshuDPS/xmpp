package com.cft.app.xmppapp.listener

// listener for friends selection for group creation with friends selected list
interface OnFriendSelectionListener {
    fun onFriendsSelected(friendsJidList:ArrayList<String>)
}