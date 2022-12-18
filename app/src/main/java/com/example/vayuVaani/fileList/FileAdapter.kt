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

package com.example.vayuVaani.fileList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vayuVaani.R
import com.example.vayuVaani.models.File
import com.example.vayuVaani.models.FileType

class FileAdapter(private var files:List<File>, private val onClick: (File) -> Unit) :RecyclerView.Adapter<FileAdapter.FlowerViewHolder>(){

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    inner class FlowerViewHolder(itemView: View, val onClick: (File) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.mtrl_list_item_text)
        private val flowerImageView: ImageView = itemView.findViewById(R.id.mtrl_list_item_icon)
        private var currentFile: File? = null

        init {
            itemView.setOnClickListener {
                currentFile?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(file: File) {
            currentFile = file
            flowerTextView.text = file.name
            flowerImageView.setImageResource(when(file.fileType){
                FileType.DIR -> R.drawable.folder
                FileType.AUDIO -> R.drawable.music
                FileType.VIDEO -> R.drawable.video
                FileType.OTHER -> R.drawable.unkown
            })
        }
    }

    fun updateList(newList:List<File>){
        files=newList
        notifyDataSetChanged()
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item, parent, false)
        return FlowerViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = files[position]
        holder.bind(flower)

    }

    override fun getItemCount(): Int = files.size
}