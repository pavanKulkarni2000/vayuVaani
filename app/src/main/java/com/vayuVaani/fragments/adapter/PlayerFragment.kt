package com.vayuVaani.fragments.adapter

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.vayuVaani.R
import com.vayuVaani.models.File

class PlayerFragment(private var mediaFile: File) : Fragment(R.layout.player_layout) {
    private lateinit var videoView: VideoView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.player_layout, container, false)
        videoView = view.findViewById(R.id.video_view)
        val controller = MediaController(requireContext())
        videoView.setMediaController(controller)
        if (mediaFile.isUri) {
            videoView.setVideoURI(Uri.parse(mediaFile.path))
        } else {
            videoView.setVideoPath(mediaFile.path)
        }
        videoView.start()
        return view
    }
}