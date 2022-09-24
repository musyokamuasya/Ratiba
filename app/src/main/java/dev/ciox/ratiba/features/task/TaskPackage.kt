package dev.ciox.ratiba.features.task

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Relation
import dev.ciox.ratiba.features.attachments.Attachment
import dev.ciox.ratiba.features.subject.Subject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskPackage @JvmOverloads constructor(
    @Embedded
    var task: Task,
    @Embedded
    var subject: Subject? = null,
    @Relation(entity = Attachment::class, parentColumn = "taskID", entityColumn = "task")
    var attachments: List<Attachment> = emptyList()
) : Parcelable {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskPackage>() {
            override fun areItemsTheSame(oldItem: TaskPackage, newItem: TaskPackage): Boolean {
                return oldItem.task.taskID == newItem.task.taskID
            }

            override fun areContentsTheSame(oldItem: TaskPackage, newItem: TaskPackage): Boolean {
                return oldItem == newItem
            }
        }
    }
}