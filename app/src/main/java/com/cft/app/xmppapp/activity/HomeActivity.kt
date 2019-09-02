package com.cft.app.xmppapp.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.core.content.res.ResourcesCompat
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.adapter.ChatsViewPagerAdapter
import com.cft.app.xmppapp.app_helper.AppConstants
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.fragment.FriendsListFragment
import com.cft.app.xmppapp.listener.FriendRequestListener
import com.cft.app.xmppapp.listener.OnFriendSelectionListener
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_home.*
import org.jivesoftware.smack.roster.RosterEntry
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate


class HomeActivity : BaseActivity() {

    /* private var onIncomingMessageListener = object :IncomingMessageListener{
         override fun onIncomingMessage(message:Message,from: Jid) {
             runOnUiThread {
                 displayToast("${message.body} from ${Utilities.getNameFromJid(from)}")
                 Log.d("info message",message.toXML("urn:xmpp:delay").toString())
             }
         }

     }*/


    /*
    <delay xmlns='urn:xmpp:delay' stamp='2019-08-28T06:48:53.808+00:00' from='192.168.1.12'>Offline storage</delay>
    */

    var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar_home)
        val tabList = ArrayList<String>()
        tabList.add("Chats")
        tabList.add("Friends")
        view_pager.adapter =
            ChatsViewPagerAdapter(supportFragmentManager, tabList, onFriendSelectionListener)

        tab_layout_chat.setupWithViewPager(view_pager)

        fab_add_friend.setOnClickListener {

            showSendRequestDialog()
        }

        ManageConnections.friendRequestListener = friendRequestListener

        toolbar_home.setOnMenuItemClickListener {

            if (it.itemId == R.id.log_out) {
                logOut()
                finish()
                switchActivity(LogInActivity::class.java)
            }

            return@setOnMenuItemClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    private fun showSendRequestDialog() {

        val dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.dialog_send_request)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.findViewById<Button>(R.id.bt_send).setOnClickListener {
            val nameLayout = dialog.findViewById<TextInputLayout>(R.id.ti_name)
            val name = getTextFromEditText(dialog.findViewById(R.id.et_name))
            if (name.isNotEmpty()) {
                sendRequestToUser(name)
                nameLayout.isErrorEnabled = false
                dialog.dismiss()
            } else nameLayout.error = "Please Enter Name"
        }
        dialog.findViewById<Button>(R.id.bt_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private val friendRequestListener = object : FriendRequestListener {
        override fun askForRequest(from: Jid, entries: Set<RosterEntry>?) {
            var flag = true

            // checking for request accepting automatically
            val entry = ManageConnections.roster?.getEntry(from.asBareJid())
            if (entry?.jid == from && entry.type.name == "to") {
                flag = false
                Utilities.sendStanzaForSubscribed(from)
                Log.d("request", "approved $from ")
            }

            val name = Utilities.getNameFromJid(from)
            runOnUiThread {
                if (flag) {
                    val dialog = Dialog(this@HomeActivity)
                    dialog.setContentView(R.layout.dialog_request)
                    dialog.setCancelable(false)
                    dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.findViewById<TextView>(R.id.tv_message).text =
                        ("$name sends you a friend Request.\nDid you want to Accept")

                    dialog.findViewById<Button>(R.id.bt_accept).setOnClickListener {

                        Utilities.sendStanzaForSubscribed(from)
                        Log.d("request", "approved $from ")
                        ManageConnections.roster?.sendSubscriptionRequest(from.asBareJid())
                        Log.d("request", "Confirm $from ")
                        dialog.dismiss()
                    }

                    dialog.findViewById<Button>(R.id.bt_cancel)
                        .setOnClickListener { dialog.dismiss() }
                    dialog.show()
                }
            }
        }

    }

    private fun sendRequestToUser(name: String) {

        val jid = JidCreate.bareFrom("$name@${AppConstants.JID}")
        ManageConnections.roster?.sendSubscriptionRequest(jid)

    }

    inner class ActionBarCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {

            if (item.itemId == R.id.new_group) {
                // create group dialog
                //AppPreferences.getSelectedFriends(baseContext)
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_create_group, menu)

            app_bar_layout.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorAccent,
                    null
                )
            )
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.title = null
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mode?.finish()
            FriendsListFragment.clearList()
            app_bar_layout.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
        }


    }

    private val onFriendSelectionListener = object : OnFriendSelectionListener {

        @SuppressLint("RestrictedApi")
        override fun onFriendsSelected(friendsJidList: ArrayList<String>) {

            AppPreferences.saveSelectedFriends(baseContext, friendsJidList)
            if (friendsJidList.size != 0 && actionMode == null)
                actionMode = this@HomeActivity.startSupportActionMode(ActionBarCallback())
            else if (friendsJidList.size == 0) {
                actionMode?.finish()
                actionMode = null
                AppPreferences.saveSelectedFriends(baseContext, null)
            }

        }

    }

}


