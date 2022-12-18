package com.example.vayuVaani.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vayuVaani.models.File

class FileListViewModel : ViewModel() {

    private val _listState = MutableLiveData<List<File>>()
    val fileList: LiveData<List<File>> = _listState

    private val _folderView = MutableLiveData(true)
    val folderView :LiveData<Boolean> = _folderView

    // You should call this to update you liveData
    fun updateFiles(newInfo: List<File>) {
        _listState.value = newInfo
    }

    fun updateView(folderView:Boolean) {
        _folderView.value = folderView
    }
}