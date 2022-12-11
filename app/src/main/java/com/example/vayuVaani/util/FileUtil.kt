package com.example.vayuVaani.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.vayuVaani.File


    val rootUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia")
    fun listAllFoldersWithMedia(context:Context): List<File> {
        var docFile=DocumentFile.fromTreeUri(context,rootUri)!!
        var docs=docFile.listFiles().mapNotNull { doc -> doc.name?.let { it1 -> File(it1) } }
        return docs
    }
    fun arrayOfDocumentFilesInFolder(baseUri:Uri,contentResolver: ContentResolver): Array<DocumentFile?> {
        val childrenUri: Uri = DocumentsContract.buildChildDocumentsUriUsingTree(
            baseUri,
            DocumentsContract.getDocumentId(baseUri)
        )
        val results: ArrayList<Uri> = ArrayList()
        var c: Cursor? = null
        try {
            c = contentResolver.query(
                childrenUri, arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
                ), null, null, null
            )
            if (c != null) {
                while (c.moveToNext()) {
                    val documentId: String = c.getString(0)
                    val documentUri: Uri = DocumentsContract.buildDocumentUriUsingTree(
                        baseUri,
                        documentId
                    )
                    results.add(documentUri)
                }
            }
        } catch (e: Exception) {
            Log.w("TAG", "Failed query: $e")
//        } finally {
//            closeQuietly(c)
        }
        val result: Array<Uri> = results.toArray(arrayOfNulls<Uri>(results.size))
        val resultFiles = arrayOfNulls<DocumentFile>(result.size)
//        for (i in result.indices) {
//            resultFiles[i] = DocumentFile( Context, result[i])
//        }
        return resultFiles
    }
