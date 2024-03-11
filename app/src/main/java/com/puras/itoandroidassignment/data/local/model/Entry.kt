package com.puras.itoandroidassignment.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val feedKey: String,
    val githubId: String?,
    val published: String?,
    val link: String?,
    val title: String?,
    val media: String?,
    val author: String?,
    val content: String?
)
