package com.example.vayuVaani.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.example.vayuVaani.models.File
import com.example.vayuVaani.models.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLConnection
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.streams.toList


val rootPath="/storage/emulated/0/"

var mainFolderListCache:List<File> = emptyList()

    suspend fun listFolder():List<File>{
        val result=LinkedList<File>()
        val stack=Stack<Path>()
        stack.add(Paths.get(rootPath))
        var flagFolderAdded: Boolean
        while(stack.isNotEmpty()){
            flagFolderAdded=false
            val folPath=stack.pop()
            val list = withContext(Dispatchers.IO) {
                try {
                    Files.list(folPath)
                }catch (e: AccessDeniedException){
                    emptyList<Path>().stream()
                }
            }
            val folders= mutableListOf<Path>()
            list.forEach{
                path->
                if(path.isDirectory()){
                    folders.add(path)
                }else if (!flagFolderAdded && isVideoAudio(fileType(URLConnection.guessContentTypeFromName(path.name)))){
                    result.add(File.fromPath(folPath, FileType.DIR))
                    flagFolderAdded=true
                }
            }
            stack.addAll(folders)
        }
        return result
    }
    suspend fun listAndroidFolder(context:Context): List<File> {
        val data=DocumentFile.fromTreeUri(context, Uri.parse(androidFolderTreeUriStr("data")))!!
        val stack=Stack<DocumentFile>()
        stack.add(data)
        val result=LinkedList<File>()
        var flagFolderAdded: Boolean
        withContext(Dispatchers.IO) {
            while (stack.isNotEmpty()) {
                flagFolderAdded = false
                val folder = stack.pop()
                val folders = mutableListOf<DocumentFile>()
                folder.listFiles().forEach { doc ->
                    if (doc.isDirectory) {
                        folders.add(doc)
                    } else if (!flagFolderAdded && isVideoAudio(fileType(doc.type))) {
                        result.add(File.fromDocFile(folder, FileType.DIR))
                        flagFolderAdded = true
                    }
                }
                stack.addAll(folders)
            }
        }
        return result
    }
suspend fun getMediaInFolder(folder:Path):List<Path>{
    return withContext(Dispatchers.IO) {
        Files.list(folder).toList().filter { path -> isVideoAudio(fileType(URLConnection.guessContentTypeFromName(path.name))) }
    }
}

fun isVideoAudio(type: FileType):Boolean = type== FileType.AUDIO||type== FileType.VIDEO

suspend fun getMediaInFolder(folder:DocumentFile):List<DocumentFile>{
    return withContext(Dispatchers.IO) {
        folder.listFiles().toList().filter { doc -> isVideoAudio(fileType(doc.type)) }
    }
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

fun fileType(mimeType:String?): FileType {
    return mimeType?.let{
        try {
            FileType.valueOf(it.split("/")[0].uppercase())
        }catch (e:Exception){
            FileType.OTHER
        }
    }?: FileType.OTHER
}

fun androidFolderTreeUriStr(folder:String): String {
    return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2F$folder"
}