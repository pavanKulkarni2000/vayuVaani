package com.example.vayuVaani

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.vayuVaani.fragments.FolderListFragment
import com.example.vayuVaani.fragments.HomePagerFragment
import com.example.vayuVaani.util.*

class MainActivity : AppCompatActivity(R.layout.main_layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        managePermissions(this)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view,HomePagerFragment())
            }
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

