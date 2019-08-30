package com.cft.app.xmppapp.fragment


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
import com.cft.app.xmppapp.listener.OnMyClickListener
import com.cft.app.xmppapp.listener.RosterChangeListener
import com.cft.app.xmppapp.model.FriendsDetailsModel
import com.cft.app.xmppapp.xmpp_connections.ManageConnections
import kotlinx.android.synthetic.main.fragment_friends_list.*


class FriendsListFragment : Fragment() {

    private var friendsListAdapter: FriendsListAdapter? = null
    private var friendsList = ArrayList<FriendsDetailsModel>()
    private var baseActivity: BaseActivity? = null

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
        friendsListAdapter = FriendsListAdapter(baseActivity!!, friendsList, onMyClickListener)
        rv_friends_list.adapter = friendsListAdapter
        ManageConnections.onRosterChangeListener = onRosterChangeListener
        onRosterChangeListener.onRosterChange()
    }

    private val onRosterChangeListener = object : RosterChangeListener {

        override fun onRosterChange() {

            baseActivity?.runOnUiThread {
                val entries = ManageConnections.roster?.entries!!
                friendsList.clear()
                for (entry in entries) {
                    val jid = entry.jid.toString()
                    val name = (jid[0] - 32).toString() + jid.substring(1, jid.indexOf("@"))
                    if (entry.type.name != "none")
                        friendsList.add(FriendsDetailsModel(name, jid, entry.type.name))
                }
                friendsListAdapter?.notifyDataSetChanged()
            }

        }
    }

    private val onMyClickListener = object : OnMyClickListener {
        override fun onMyClick(position: Int) {
            val bundle = Bundle()
            bundle.putString("jid", friendsList[position].jid)
            baseActivity?.switchActivity(ChatActivity::class.java, bundle)
        }
    }


}
