package com.cft.app.xmppapp.adapter

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cft.app.xmppapp.fragment.FriendsListFragment
import com.cft.app.xmppapp.fragment.ChatsListFragment

class ChatsViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragmentList: ArrayList<String>
) : FragmentStatePagerAdapter(fragmentManager) {


    override fun getItem(position: Int) =
        if (position == 0) ChatsListFragment() else FriendsListFragment()

    override fun getCount() = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentList[position]
    }
}