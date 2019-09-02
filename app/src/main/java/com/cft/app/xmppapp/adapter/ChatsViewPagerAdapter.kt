package com.cft.app.xmppapp.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cft.app.xmppapp.fragment.FriendsListFragment
import com.cft.app.xmppapp.fragment.ChatsListFragment
import com.cft.app.xmppapp.listener.OnFriendSelectionListener

class ChatsViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragmentList: ArrayList<String>,
    private val onFriendSelectionListener: OnFriendSelectionListener
) : FragmentStatePagerAdapter(fragmentManager) {


    override fun getItem(position: Int) =
        if (position == 0) ChatsListFragment() else FriendsListFragment.getInstance(onFriendSelectionListener)

    override fun getCount() = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList[position]
    }
}