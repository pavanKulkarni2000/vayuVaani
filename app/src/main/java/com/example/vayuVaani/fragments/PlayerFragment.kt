package com.example.vayuVaani.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.vayuVaani.R
import com.example.vayuVaani.models.File
import com.example.vayuVaani.util.TAG

class PlayerFragment(private var mediaFile:File):Fragment() {
    private lateinit var videoView: VideoView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.player_layout,container,false)
        videoView=view.findViewById(R.id.video_view)
        val controller = MediaController(requireContext())
        controller.setMediaPlayer(videoView)
        videoView.setMediaController(controller)
        if(mediaFile.isUri){
            videoView.setVideoURI(Uri.parse(mediaFile.path))
        }else{
            videoView.setVideoPath(mediaFile.path)
        }
        videoView.start()
        return view
    }
}