/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vayuVaani.data

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.vayuVaani.File
import java.nio.file.Files
import java.nio.file.Files.isDirectory
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.name
import kotlin.streams.toList


const val rootFolder = "/storage/emulated/0"

/* Returns initial list of flowers. */
//fun flowerList(contentResolver: ContentResolver): List<File> {
//    return mutableListOf()
//    val baseUri = contentResolver.persistedUriPermissions.get(0).uri
//    Log.i("TAG", "flowerList: $baseUri")
//    traverseDirectoryEntries(contentResolver,baseUri)
//    return Files.list(Paths.get(rootFolder)).map { path: Path -> File(path.fileName.name) }
//        .toList()
//}

fun traverseDirectoryEntries(contentResolver : ContentResolver,rootUri: Uri?) {
    var childrenUri : Uri
     childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
        rootUri,
        DocumentsContract.getTreeDocumentId(rootUri)
    )

    // Keep track of our directory hierarchy
    val dirNodes: MutableList<Uri> = LinkedList()
    dirNodes.add(childrenUri)
    while (!dirNodes.isEmpty()) {
        childrenUri = dirNodes.removeAt(0) // get the item from top
        Log.d("TAG", "node uri: $childrenUri")
        val c: Cursor? = contentResolver.query(
            childrenUri,
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
            ),
            null,
            null,
            null
        )
            if (c != null) {
                while (c.moveToNext()) {
                    val docId: String = c.getString(0)
                    val name: String = c.getString(1)
                    val mime: String = c.getString(2)
                    Log.d("TAG", "docId: $docId, name: $name, mime: $mime")
                    if (isDirectory(Paths.get(mime))) {
                        val newNode =
                            DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, docId)
                        dirNodes.add(newNode)
                    }
                }
            }
    }
}


private fun readSDK30(tree: DocumentFile) {
//    val tree = DocumentFile.fromTreeUri(this, treeUri)!!
    val uriList = arrayListOf<Uri>()
    listFiles(tree).forEach { uri ->
        uriList.add(uri)
        Log.i("Uri Log:", uri.toString())
        // Collect all the Uri from here
    }
}

private fun listFiles(folder: DocumentFile): List<Uri> {
    return if (folder.isDirectory) {
        folder.listFiles().mapNotNull { file ->
            if (file.name != null) file.uri else null
        }
    } else {
        emptyList()
    }
}