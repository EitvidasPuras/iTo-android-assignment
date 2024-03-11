package com.puras.itoandroidassignment.data.local.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "feeds")
/* ↓ Needed for Compose Destinations */
@Parcelize
data class Feed(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String,
    val link: String
) : Parcelable
