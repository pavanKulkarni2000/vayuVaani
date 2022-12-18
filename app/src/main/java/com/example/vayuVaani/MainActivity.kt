package com.example.vayuVaani

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.example.vayuVaani.fileList.FileAdapter
import com.example.vayuVaani.fragments.MediaListActivity
import com.example.vayuVaani.viewModel.FileListViewModel
import com.example.vayuVaani.models.File
import com.example.vayuVaani.models.FileType
import com.example.vayuVaani.util.*
import kotlinx.coroutines.*
import java.net.URLConnection
import java.nio.file.Paths
import kotlin.io.path.name

class MainActivity : AppCompatActivity(R.layout.list_layout) {

    private lateinit var adapter: FileAdapter
    private lateinit var recyclerView: RecyclerView
    private val fileListViewModel: FileListViewModel by viewModels()
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(savedInstanceState==null){
//            supportFragmentManager.commit {
//                setReorderingAllowed(true)
//                add(R.id.list_layout,MediaListActivity())
//            }
//        }

        managePermissions(this)

        initRecyclerView()

        initOtherControlls()
    }

    private fun initOtherControlls() {

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(fileListViewModel.folderView.value){
                    true -> finish()
                    false -> {
                        fileListViewModel.updateFiles(mainFolderListCache)
                        fileListViewModel.updateView(true)
                    }
                    null -> finish()
                }
            }
        })
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            scope.launch { fabOnClick() }
        }
    }

    private fun initRecyclerView() {
        adapter = FileAdapter(emptyList(), ::fileOnClick)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(EmptyItemDecoration())
        fileListViewModel.fileList.observe(this, adapter::updateList)
    }

    private suspend fun fabOnClick() {
        coroutineScope {
            val differed1= async { listFolder() }
            val differed2= async { listAndroidFolder(context = applicationContext) }
            val list=differed1.await()
            val dataList=differed2.await()
            Log.d(TAG, "fabOnClick: $list")
            Log.d(TAG, "fabOnClick: $dataList")
            fileListViewModel.updateFiles(list+dataList)
        }
    }

    private fun fileOnClick(file: File) {
        if(file.fileType== FileType.DIR){
            fileListViewModel.updateView(false)
            mainFolderListCache= fileListViewModel.fileList.value!!
            scope.launch {
                val newFiles=
                if(file.isUri) {
                    DocumentFile.fromTreeUri(applicationContext, Uri.parse(file.path))
                        ?.let { documentFile ->
                            getMediaInFolder(documentFile).map{
                                File.fromDocFile(it, fileType(it.type))
                            }
                        } ?: emptyList()
                } else
                    getMediaInFolder(Paths.get(file.path)).map{
                        File.fromPath(it,
                            fileType(URLConnection.guessContentTypeFromName(it.name))
                        )
                    }
                fileListViewModel.updateFiles(newFiles)
            }
        } else{

        }

    }

    val activityWithResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    data.data?.let { treeUri ->
                        contentResolver.takePersistableUriPermission(
                            treeUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                    }
                }
            }
        }

    val docTreeLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {treeUri ->
            if (treeUri != null) {
                contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            it.forEach{
                    entry: Map.Entry<String, Boolean> ->  if(!entry.value){
                Log.d(TAG, "${entry.key}: not allowed")
                finish()
            }
            }
        }

}

