package com.example.vayuVaani.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vayuVaani.models.File

class CentralViewModel : ViewModel() {

    private val _folderList = MutableLiveData<List<File>>()
    val folderList: LiveData<List<File>> = _folderList

    private val _folderMediaList = MutableLiveData<List<File>>()
    val folderMediaList: LiveData<List<File>> = _folderMediaList

    private val _favouriteMediaList = MutableLiveData<List<File>>()
    val favouriteMediaList: LiveData<List<File>> = _favouriteMediaList

    // You should call this to update you liveData
    fun updateFolders(newInfo: List<File>) {
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
}