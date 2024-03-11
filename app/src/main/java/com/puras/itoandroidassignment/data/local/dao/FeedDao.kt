package com.puras.itoandroidassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puras.itoandroidassignment.data.local.model.Feed

@Dao
interface FeedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertList(feedList: List<Feed>)

    @Query("""DELETE FROM feeds""")
    suspend fun deleteAll()

    @Query("""SELECT * FROM feeds""")
    fun get(): List<Feed>
}