package com.vayuVaani.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.vayuVaani.R
import com.example.vayuVaani.util.getMediaInFolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vayuVaani.db.realm
import com.vayuVaani.fileList.FileAdapter
import com.vayuVaani.models.File
import com.vayuVaani.models.Folder
import com.vayuVaani.util.EmptyItemDecoration
import com.vayuVaani.util.TAG
import com.vayuVaani.viewModel.CentralViewModel
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Paths


class FolderMediaListFragment(private val currentFolder: Folder) : Fragment(R.layout.list_layout) {

    private lateinit var refreshLayout: SwipeRefreshLayout
    private val viewModel: CentralViewModel by viewModels()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChildViews(view)
        initData()
    }

    private fun initData() {
        scope.launch {
            val all = realm.query<File>("parentPath = $0",currentFolder.path).find()
            withContext(Dispatchers.Main) {
                viewModel.updateFolderMedia(all)
            }
        }
    }

    private fun refreshData() {
        scope.launch {
            val newFiles =
                if (currentFolder.isUri) {
                    DocumentFile.fromTreeUri(requireContext(), Uri.parse(currentFolder.path))
                        ?.let { getMediaInFolder(it) } ?: emptyList()
                } else
                    getMediaInFolder(Paths.get(currentFolder.path))
            withContext(Dispatchers.Main) {
                viewModel.updateFolderMedia(newFiles)
                refreshLayout.isRefreshing=false
            }
            realm.write { // this : MutableRealm
                newFiles.forEach { file -> copyToRealm(file, UpdatePolicy.ALL) }
            }
        }
    }

    private fun initChildViews(view: View) {
        val adapter = FileAdapter(emptyList(), ::mediaOnClick)

        val recyclerView:RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(EmptyItemDecoration())
        viewModel.folderMediaList.observe(viewLifecycleOwner, adapter::updateList)

        val fab:FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            playMedia()
        }

        refreshLayout=view.findViewById(R.id.swipe_refresh_layout)
        refreshLayout.setOnRefreshListener(::refreshData)
    }

    private fun playMedia() {
        TODO("Not yet implemented")
    }

    private fun mediaOnClick(file: File) {
        parentFragmentManager.commit {
            add(R.id.fragment_container_view, VlcPlayerFragment(file), TAG)
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateFolderMedia(emptyList())
    }
}