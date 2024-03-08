package com.puras.itoandroidassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.puras.itoandroidassignment.data.local.model.Feed

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feed: Feed)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertList(feedList: List<Feed>)

    @Delete
    suspend fun delete(feed: Feed)

    @Query("""DELETE FROM feeds""")
    suspend fun deleteAll()

    @Update
    suspend fun update(feed: Feed)

    @Query("""SELECT * FROM feeds""")
    fun getFeeds(): List<Feed>
}