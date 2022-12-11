package com.example.vayuVaani

import android.content.Context
import android.content.Intent
import android.content.UriPermission
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.vayuVaani.util.EmptyItemDecoration
import com.example.vayuVaani.util.androidFolderTreeUriStr
import com.example.vayuVaani.util.listAllFoldersWithMedia
import com.example.vayuVaani.util.managePermissions

class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        requestAllFilesPermission()
//        requestDocumentPermission("data")
//        requestDocumentPermission("obb")
        managePermissions(this)

        val files = listAllFoldersWithMedia(context = applicationContext)

        val fileAdapter = FileAdapter(files, ::fileOnClick)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = fileAdapter
        recyclerView.addItemDecoration(EmptyItemDecoration())

    }

    private fun fileOnClick(file: File) {

    }

    private fun requestAllFilesPermission() {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.fromParts("package", packageName, null)
            activityWithResultLauncher.launch(intent)
        }
    }

    private fun requestDocumentPermission(folder: String) {
        val scheme = androidFolderTreeUriStr(folder)
        var uri = Uri.parse(scheme)
        if (contentResolver.persistedUriPermissions.any { element: UriPermission ->
                element.uri == uri &&
                        element.isReadPermission &&
                        element.isWritePermission
            }) {
            return
        }
        uri = Uri.parse(scheme.replace("/tree/", "/document/"))
        docTreeLauncher.launch(uri)
    }
}