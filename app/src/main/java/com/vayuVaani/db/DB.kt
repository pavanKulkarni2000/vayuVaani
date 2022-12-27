package com.vayuVaani.db

import com.vayuVaani.models.File
import com.vayuVaani.models.Folder
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

private val configuration = RealmConfiguration.create(schema = setOf(Folder::class, File::class))
val realm = Realm.open(configuration)