package com.example.vayuVaani.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.vayuVaani.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomePagerAdapter(fragment: Fragment):FragmentStateAdapter(fragment) {

    private val tabConstructors = arrayOf(::FolderListFragment,::FavouriteMediaListFragment)

    override fun getItemCount(): Int = tabConstructors.size

    override fun createFragment(position: Int): Fragment {
        return tabConstructors[position]()
    }

}