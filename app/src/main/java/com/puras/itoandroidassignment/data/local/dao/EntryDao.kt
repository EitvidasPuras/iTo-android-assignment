package com.puras.itoandroidassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puras.itoandroidassignment.data.local.model.Entry

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertList(feedList: List<Entry>)

    @Query("""DELETE FROM entries WHERE feedKey = :key""")
    suspend fun delete(key: String)

    @Query("""SELECT * FROM entries WHERE feedKey = :key""")
    fun get(key: String): List<Entry>
}