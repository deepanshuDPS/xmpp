package com.cft.app.xmppapp.activity

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.adapter.ChatsViewPagerAdapter
import com.cft.app.xmppapp.adapter.SelectedUsersAdapter
import com.cft.app.xmppapp.app_helper.AppConstants
import com.cft.app.xmppapp.app_helper.AppPreferences
import com.cft.app.xmppapp.app_helper.Utilities
import com.cft.app.xmppapp.fragment.FriendsListFragment
import com.cft.app.xmppapp.listener.FriendRequestListener
import com.cft.app.xmppapp.listener.OnFriendSelectionListener
import com.cft.app.xmppapp.listener.OnMyClickListener
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_home.*
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate

/**
 * activity for managing chats and friends for a xmpp user
 */
class HomeActivity : BaseActivity() {

    var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar_home)
        val tabList = ArrayList<String>()
        tabList.add("Chats")
        tabList.add("Friends")

        //setup chats and friends tabs
        view_pager.adapter = ChatsViewPagerAdapter(supportFragmentManager, tabList, onFriendSelectionListener)

        tab_layout_chat.setupWithViewPager(view_pager)

        tab_layout_chat.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                clearActionMode()
            }

        })

        // fab button to add friend using its username without jid attached
        fab_add_friend.setOnClickListener {
            showSendRequestDialog()
        }

        ManageConnections.friendRequestListener = friendRequestListener

        toolbar_home.setOnMenuItemClickListener {

            if (it.itemId == R.id.log_out) {        // on logout erase previous login data and switch to login
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

    /**
     * dialog for sending request to a user
     */
    private fun showSendRequestDialog() {

        val dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.dialog_send_request)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.findViewById<Button>(R.id.bt_send).setOnClickListener {
            val nameLayout = dialog.findViewById<TextInputLayout>(R.id.ti_name)
            val name = getTextFromEditText(dialog.findViewById(R.id.et_name))
            if (name.isNotEmpty()) {
                val jid = JidCreate.bareFrom("$name@${AppConstants.JID}")   // here we attached with username
                ManageConnections.roster?.sendSubscriptionRequest(jid)          // sending subscribe request
                nameLayout.isErrorEnabled = false
                dialog.dismiss()
            } else nameLayout.error = getString(R.string.please_enter_name)
        }
        dialog.findViewById<Button>(R.id.bt_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    // if friend request arrives then this listener handles that
    private val friendRequestListener = object : FriendRequestListener {
        override fun askForRequest(from: Jid) {
            var flag = true

            // subscribed for request automatically when already as "to" status in roster
            // "to" means this user already subscribe "from" user
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

    // action mode for new group creation but not working now
    inner class ActionBarCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {

            if (item.itemId == R.id.new_group) {
                // create group dialog
                createGroupDialog()
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
            clearActionMode()
        }


    }

    // clear action mode if no need for selected friends group creation
    private fun clearActionMode() {
        actionMode?.finish()
        actionMode = null
        AppPreferences.saveSelectedFriends(baseContext, null)
        FriendsListFragment.clearList()
        app_bar_layout.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorPrimary,
                null
            )
        )
    }

    // create group dialog to create new group with specified users
    private fun createGroupDialog() {
        val dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.dialog_create_group)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.findViewById<Button>(R.id.bt_create).setOnClickListener {
            val groupNameLayout = dialog.findViewById<TextInputLayout>(R.id.ti_name)
            val groupName = getTextFromEditText(dialog.findViewById(R.id.et_group_name))
            if (groupName.isNotEmpty()) {
//                ManageConnections.createGroup(groupName,AppPreferences.getSelectedFriends(baseContext))
                groupNameLayout.isErrorEnabled = false
                clearActionMode()
                dialog.dismiss()
            } else groupNameLayout.error = "Please Enter Group Name"
        }
        dialog.findViewById<Button>(R.id.bt_cancel).setOnClickListener {
            dialog.dismiss()
            clearActionMode()
        }

        val rvSelectedUsers = dialog.findViewById<RecyclerView>(R.id.rv_selected_users)
        rvSelectedUsers.layoutManager = LinearLayoutManager(this)
        val list = ArrayList<String>()
        for(i in AppPreferences.getSelectedFriends(baseContext))
            list.add(i)
        val adapter = SelectedUsersAdapter(baseContext,list,object :OnMyClickListener{
            override fun onMyClick(position: Int) {
                dialog.dismiss()
                clearActionMode()
            }

        })
        rvSelectedUsers.adapter = adapter
        dialog.show()
    }

    // this listener is called when the users selection occurs in chat list
    private val onFriendSelectionListener = object : OnFriendSelectionListener {

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


