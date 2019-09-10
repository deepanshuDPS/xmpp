package com.cft.app.xmppapp.fragment


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.cft.app.xmppapp.R
import com.cft.app.xmppapp.activity.BaseActivity
import com.cft.app.xmppapp.activity.ChatActivity
import com.cft.app.xmppapp.adapter.FriendsListAdapter
import com.cft.app.xmppapp.listener.OnAllClickListener
import com.cft.app.xmppapp.listener.OnFriendSelectionListener
import com.cft.app.xmppapp.listener.RosterChangeListener
import com.cft.app.xmppapp.model.FriendsDetailsModel
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import kotlinx.android.synthetic.main.fragment_friends_list.*

@SuppressLint("StaticFieldLeak")
class FriendsListFragment : Fragment() {


    private var baseActivity: BaseActivity? = null

    // companion object for handling friends selections from home activity and clearing list if no selection
    companion object {

        private var onFriendSelectionListener: OnFriendSelectionListener? = null
        private var friendsListAdapter: FriendsListAdapter? = null
        private var friendsList = ArrayList<FriendsDetailsModel>()
        fun getInstance(onFriendSelectionListener: OnFriendSelectionListener): Fragment {
            this.onFriendSelectionListener = onFriendSelectionListener
            return FriendsListFragment()
        }

        fun clearList() {
            if (friendsList.size > 0) {
                for (i in friendsList)
                    i.clicked = false
                friendsListAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity = activity as BaseActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_friends_list.layoutManager = LinearLayoutManager(baseActivity)
        friendsListAdapter = FriendsListAdapter(baseActivity!!, friendsList, onAllClickListener)
        rv_friends_list.adapter = friendsListAdapter
        ManageConnections.onRosterChangeListener = onRosterChangeListener
        onRosterChangeListener.onRosterChange()
    }

    // if the roster is updated,added or removed then the friends list updated with current status
    private val onRosterChangeListener = object : RosterChangeListener {

        override fun onRosterChange() {

            baseActivity?.runOnUiThread {
                val entries = ManageConnections.roster?.entries!!
                friendsList.clear()
                for (entry in entries) {
                    val jid = entry.jid.toString()
                    val name = (jid[0] - 32).toString() + jid.substring(1, jid.indexOf("@"))
                    if (entry.type.name == "both")
                        friendsList.add(FriendsDetailsModel(name, jid, entry.type.name, false))
                }
                friendsListAdapter?.notifyDataSetChanged()
            }

        }
    }

    // listener for different functions of the item of friends list
    private val onAllClickListener = object : OnAllClickListener {

        private fun sendSelectedList() {
            val selectedFriendsList = ArrayList<String>()
            for (i in friendsList) {
                if (i.clicked)
                    selectedFriendsList.add(i.jid)
            }
            onFriendSelectionListener?.onFriendsSelected(selectedFriendsList)
        }

        override fun onClick(position: Int) {
            val bundle = Bundle()
            bundle.putString("jid", friendsList[position].jid)
            baseActivity?.switchActivity(ChatActivity::class.java, bundle)
        }

        override fun onItemClick(position: Int) {
            sendSelectedList()
        }

        override fun onItemLongClick(position: Int) {
            sendSelectedList()
        }

    }


}
