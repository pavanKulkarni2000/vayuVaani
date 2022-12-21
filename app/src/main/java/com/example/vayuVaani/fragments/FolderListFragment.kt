package com.example.vayuVaani.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.vayuVaani.R
import com.example.vayuVaani.fileList.FileAdapter
import com.example.vayuVaani.models.File
import com.example.vayuVaani.util.*
import com.example.vayuVaani.viewModel.CentralViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class FolderListFragment :Fragment(R.layout.list_layout){

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
        val dialog = ProgressDialog(context)
        dialog.setMessage("Fetching folders ...")
        dialog.setCancelable(false)
        dialog.setInverseBackgroundForced(false)
        dialog.show()
        scope.launch {
            coroutineScope {
                val differed1= async { listFolder() }
                val differed2= async { listAndroidFolder(context = requireContext()) }
                val list=differed1.await()
                val dataList=differed2.await()
                Log.d(TAG, "fabOnClick: $list")
                Log.d(TAG, "fabOnClick: $dataList")
                viewModel.updateFolders(list+dataList)
            }
            dialog.cancel()
        }
    }


    private fun initChildViews(view: View) {
        adapter = FileAdapter(emptyList(), ::folderOnClick)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(EmptyItemDecoration())
        viewModel.folderList.observe(viewLifecycleOwner, adapter::updateList)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            scope.launch { fabOnClick() }
        }
    }

    private fun fabOnClick() {
        TODO("Not yet implemented")
    }

    private fun folderOnClick(file: File) {
        requireParentFragment().parentFragmentManager.commit {
            add(R.id.fragment_container_view,FolderMediaListFragment(file))
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateFavouriteMedia(emptyList())
    }
}