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

package com.vayuVaani.models

import androidx.documentfile.provider.DocumentFile
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.nio.file.Path
import kotlin.io.path.name

class Folder(
    var name: String,
    @field:PrimaryKey
    var path: String,
    var items: Int,
    var isUri: Boolean
) : RealmObject {

    constructor() : this("", "", 0, false)

    override fun equals(other: Any?): Boolean = (this === other) || ((other as Folder).path == path)

    override fun hashCode(): Int = path.hashCode()

    companion object {
        fun fromPath(path: Path): Folder {
            return Folder(path.fileName.name, path.toString(), 0, false)
        }

        fun fromDocFile(doc: DocumentFile): Folder {
            return Folder(doc.name ?: "Unnamed file", doc.uri.toString(), 0, true)
        }
    }
}