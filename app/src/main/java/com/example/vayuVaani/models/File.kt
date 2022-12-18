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

package com.example.vayuVaani.models

import androidx.documentfile.provider.DocumentFile
import java.nio.file.Path
import kotlin.io.path.name

data class File(
    val name: String,
    val path: String,
    val fileType: FileType,
    val isUri: Boolean
) {
    companion object {
        fun fromPath(path: Path, type: FileType): File {
            return File(path.fileName.name, path.toString(), type, false)
        }

        fun fromDocFile(doc: DocumentFile, type: FileType): File {
            return File(doc.name ?: "Unnamed file", doc.uri.toString(), type, true)
        }
    }
}