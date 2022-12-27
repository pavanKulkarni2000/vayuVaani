package com.vayuVaani.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vayuVaani.models.File
import com.vayuVaani.models.Folder
import com.vayuVaani.models.PlayBack

class CentralViewModel : ViewModel() {

    private val _folderList = MutableLiveData<List<Folder>>()
    val folderList: LiveData<List<Folder>> = _folderList

    private val _folderMediaList = MutableLiveData<List<File>>()
    val folderMediaList: LiveData<List<File>> = _folderMediaList

    private val _favouriteMediaList = MutableLiveData<List<File>>()
    val favouriteMediaList: LiveData<List<File>> = _favouriteMediaList

    // You should call this to update you liveData
    fun updateFolders(newInfo: List<Folder>) {
        _folderList.value = newInfo
    }

    // You should call this to update you liveData
    fun updateFolderMedia(newList: List<File>) {
        _folderMediaList.value = newList
    }

    // You should call this to update you liveData
    fun updateFavouriteMedia(newList: List<File>) {
        _favouriteMediaList.value = newList
    }

    companion object {
        var currentPlayBack: PlayBack? = null
    }
}