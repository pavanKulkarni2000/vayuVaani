package com.example.vayuVaani.util

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.vayuVaani.models.File
import com.vayuVaani.models.FileType
import com.vayuVaani.models.Folder
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


val rootPath = "/storage/emulated/0/"

suspend fun listFolder(): List<Folder> {
    val result = LinkedList<Folder>()
    val stack = Stack<Path>()
    stack.add(Paths.get(rootPath))
    var flagFolderAdded: Boolean
    while (stack.isNotEmpty()) {
        flagFolderAdded = false
        val folPath = stack.pop()
        val list = withContext(Dispatchers.IO) {
            try {
                Files.list(folPath)
            } catch (e: AccessDeniedException) {
                emptyList<Path>().stream()
            }
        }
        val folders = mutableListOf<Path>()
        list.forEach { path ->
            if (path.isDirectory()) {
                folders.add(path)
            } else if (!flagFolderAdded && isVideoAudio(
                    fileType(
                        URLConnection.guessContentTypeFromName(
                            path.name
                        )
                    )
                )
            ) {
                result.add(Folder.fromPath(folPath))
                flagFolderAdded = true
            }
        }
        stack.addAll(folders)
    }
    return result
}

suspend fun listAndroidFolder(context: Context): List<Folder> {
    val data = DocumentFile.fromTreeUri(context, Uri.parse(androidFolderTreeUriStr("data")))!!
    val stack = Stack<DocumentFile>()
    stack.add(data)
    val result = LinkedList<Folder>()
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
                    result.add(Folder.fromDocFile(folder))
                    flagFolderAdded = true
                }
            }
            stack.addAll(folders)
        }
    }
    return result
}

suspend fun getMediaInFolder(folder: Path): List<File> {
    val result = LinkedList<File>()
    withContext(Dispatchers.IO) {
        Files.list(folder)
    }.forEach { path ->
        val mimeStr = URLConnection.guessContentTypeFromName(path.name)
        val mime = fileType(mimeStr)
        if (isVideoAudio(mime)) {
            result.add(File(path.fileName.name,path.toString(),folder.toString(), mime == FileType.AUDIO,false))
        }
    }
    return result
}

fun isVideoAudio(type: FileType): Boolean = type == FileType.AUDIO || type == FileType.VIDEO

suspend fun getMediaInFolder(folder: DocumentFile): List<File> {
    val result = LinkedList<File>()
    withContext(Dispatchers.IO) {
        folder.listFiles()
    }.forEach { doc ->
        val mime = fileType(doc.type)
        if (isVideoAudio(mime)) {
            result.add(File(doc.name?:"Unknown",doc.uri.toString(),folder.uri.toString(), mime == FileType.AUDIO, true))
        }
    }
    return result
}

fun fileType(mimeType: String?): FileType {
    return mimeType?.let {
        try {
            FileType.valueOf(it.split("/")[0].uppercase())
        } catch (e: Exception) {
            FileType.OTHER
        }
    } ?: FileType.OTHER
}

fun androidFolderTreeUriStr(folder: String): String {
    return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2F$folder"
}