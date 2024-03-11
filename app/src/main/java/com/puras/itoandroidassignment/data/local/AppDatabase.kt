package com.puras.itoandroidassignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puras.itoandroidassignment.data.local.dao.FeedDao
import com.puras.itoandroidassignment.data.local.dao.TimelineEntryDao
import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed

@Database(entities = [Feed::class, Entry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun feedDao(): FeedDao
    abstract fun entryDao(): TimelineEntryDao
}