package com.vayuVaani.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.vayuVaani.FolderList.FolderAdapter
import com.example.vayuVaani.R
import com.example.vayuVaani.util.listAndroidFolder
import com.example.vayuVaani.util.listFolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vayuVaani.db.realm
import com.vayuVaani.models.Folder
import com.vayuVaani.util.EmptyItemDecoration
import com.vayuVaani.viewModel.CentralViewModel
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderListFragment : Fragment(R.layout.list_layout) {

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
            val all = realm.query<Folder>().find()
            withContext(Dispatchers.Main) {
                viewModel.updateFolders(all)
            }
        }
    }

    private fun refreshData() {
        scope.launch {
            coroutineScope {
                val differed1 = async { listFolder() }
                val differed2 = async { listAndroidFolder(context = requireContext()) }
                val list = (differed1.await() + differed2.await())
                withContext(Dispatchers.Main) {
                    viewModel.updateFolders(list)
                    refreshLayout.isRefreshing = false
                }
                realm.write { // this : MutableRealm
                    list.forEach { folder -> copyToRealm(folder, UpdatePolicy.ALL) }
                }
            }

        }
    }


    private fun initChildViews(view: View) {

        val adapter = FolderAdapter(requireContext(), emptyList(), ::folderOnClick)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(EmptyItemDecoration())
        viewModel.folderList.observe(viewLifecycleOwner, adapter::updateList)

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
             playMedia()
        }

        refreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        refreshLayout.setOnRefreshListener(this::refreshData)
    }

    private fun playMedia() {
        TODO("Not yet implemented")
    }

    private fun folderOnClick(file: Folder) {
        requireParentFragment().parentFragmentManager.commit {
            add(R.id.fragment_container_view, FolderMediaListFragment(file))
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.updateFavouriteMedia(emptyList())
    }
}