package dev.ciox.ratiba.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.ciox.ratiba.features.event.Event
import dev.ciox.ratiba.features.event.EventPackage

@Dao
interface EventDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Delete
    suspend fun remove(event: Event)

    @Update
    suspend fun update(event: Event)

    @Query("SELECT eventID FROM events WHERE name = :event AND DATE(schedule) = DATE(:schedule) COLLATE NOCASE")
    suspend fun checkNameUniqueness(event: String?, schedule: String?): List<String>

    @Query("SELECT * FROM events")
    suspend fun fetch(): List<Event>

    @Query("SELECT * FROM events")
    suspend fun fetchPackage(): List<EventPackage>

    @Transaction
    @Query("SELECT * FROM events LEFT JOIN subjects ON events.subject == subjects.subjectID WHERE isEventArchived = 0 ORDER BY schedule ASC")
    fun fetchLiveData(): LiveData<List<EventPackage>>

    @Transaction
    @Query("SELECT * FROM events LEFT JOIN subjects ON events.subject == subjects.subjectID WHERE isEventArchived = 1 ORDER BY schedule ASC")
    fun fetchArchivedLiveData(): LiveData<List<EventPackage>>

}