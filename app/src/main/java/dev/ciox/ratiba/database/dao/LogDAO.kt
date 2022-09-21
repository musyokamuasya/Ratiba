package dev.ciox.ratiba.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.ciox.ratiba.features.log.Log

@Dao
interface LogDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: Log)

    @Delete
    suspend fun remove(log: Log)

    @Update
    suspend fun update(log: Log)

    @Query("SELECT * FROM logs")
    suspend fun fetchCore(): List<Log>

    @Query("DELETE FROM logs")
    suspend fun removeLogs()

    @Query("SELECT * FROM logs ORDER BY dateTimeTriggered ASC")
    fun fetch(): LiveData<List<Log>>
}