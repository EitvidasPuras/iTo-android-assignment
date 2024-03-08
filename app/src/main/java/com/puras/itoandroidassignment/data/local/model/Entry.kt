package com.puras.itoandroidassignment.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val githubId: String?,
    val published: String?,
    val link: String?,
    val title: String?
)
