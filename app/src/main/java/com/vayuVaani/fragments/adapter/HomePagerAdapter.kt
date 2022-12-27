package com.vayuVaani.fragments.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vayuVaani.fragments.FavouriteMediaListFragment
import com.vayuVaani.fragments.FolderListFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabConstructors = arrayOf(::FolderListFragment, ::FavouriteMediaListFragment)

    override fun getItemCount(): Int = tabConstructors.size

    override fun createFragment(position: Int): Fragment {
        return tabConstructors[position]()
    }

}