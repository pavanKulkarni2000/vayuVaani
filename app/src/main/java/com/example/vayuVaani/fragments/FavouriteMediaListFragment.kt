package com.example.vayuVaani.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.vayuVaani.R
import com.example.vayuVaani.fileList.FileAdapter
import com.example.vayuVaani.models.File
import com.example.vayuVaani.util.EmptyItemDecoration
import com.example.vayuVaani.viewModel.CentralViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class FavouriteMediaListFragment() :Fragment(R.layout.list_layout){

    private lateinit var adapter: FileAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val viewModel: CentralViewModel by viewModels()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChildViews(view)
        initData()
    }

    private fun initData() {
        scope.launch {
            //fixme
            viewModel.updateFolderMedia(emptyList())
        }
    }

    private fun initChildViews(view: View) {
        adapter = FileAdapter(emptyList(), ::mediaOnClick)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(EmptyItemDecoration())
        viewModel.favouriteMediaList.observe(viewLifecycleOwner, adapter::updateList)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            scope.launch { fabOnClick() }
        }
    }

    private fun fabOnClick() {
        TODO("Not yet implemented")
    }

    private fun mediaOnClick(file: File) {
        parentFragmentManager.commit {
            add(R.id.fragment_container_view,PlayerFragment(file))
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateFolderMedia(emptyList())
    }
}