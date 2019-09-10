package com.cft.app.xmppapp.listener


// listener for the view click, item click and item long click
interface OnAllClickListener {

    fun onClick(position:Int)

    fun onItemClick(position:Int)

    fun onItemLongClick(position: Int)
}