package dev.ciox.ratiba.database.dao

import androidx.room.*
import dev.ciox.ratiba.features.schedule.Schedule

@Dao
interface ScheduleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg schedule: Schedule)

    @Delete
    suspend fun remove(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)

    @Query("DELETE FROM schedules WHERE subject = :id")
    suspend fun removeUsingSubjectID(id: String)

    @Query("SELECT * FROM schedules")
    suspend fun fetch(): List<Schedule>

    @Query("SELECT * FROM schedules WHERE subject = :id")
    suspend fun fetchUsingID(id: String?): List<Schedule>
}