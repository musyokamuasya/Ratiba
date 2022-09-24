package dev.ciox.ratiba.database.dao

import androidx.room.*
import dev.ciox.ratiba.features.attachments.Attachment

@Dao
interface AttachmentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: Attachment)

    @Delete
    suspend fun remove(attachment: Attachment)

    @Update
    suspend fun update(attachment: Attachment)

    @Query("SELECT * FROM attachments")
    suspend fun fetch(): List<Attachment>

    @Query("DELETE FROM attachments WHERE task = :id")
    suspend fun removeUsingTaskID(id: String)
}