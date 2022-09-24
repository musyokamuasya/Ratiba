package dev.ciox.ratiba.database.repository

import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ciox.ratiba.components.extensions.jdk.isBeforeNow
import dev.ciox.ratiba.components.utils.PreferenceManager
import dev.ciox.ratiba.database.dao.AttachmentDAO
import dev.ciox.ratiba.database.dao.TaskDAO
import dev.ciox.ratiba.features.attachments.Attachment
import dev.ciox.ratiba.features.notifications.task.TaskNotificationWorker
import dev.ciox.ratiba.features.shared.abstracts.BaseWorker
import dev.ciox.ratiba.features.task.Task
import dev.ciox.ratiba.features.task.TaskPackage
import dev.ciox.ratiba.features.task.widget.TaskWidgetProvider
import javax.inject.Inject

class TaskRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val tasks: TaskDAO,
    private val attachments: AttachmentDAO,
    private val preferenceManager: PreferenceManager,
    private val workManager: WorkManager,
    private val notificationManager: NotificationManager
) {

    fun fetchLiveData(): LiveData<List<TaskPackage>> = tasks.fetchLiveData()

    fun fetchArchived(): LiveData<List<TaskPackage>> = tasks.fetchArchivedLiveData()

    suspend fun fetchCore(): List<Task> = tasks.fetch()

    suspend fun fetchCount(): Int = tasks.fetchCount()

    suspend fun fetchAsPackage(): List<TaskPackage> = tasks.fetchAsPackage()

    suspend fun checkNameUniqueness(name: String?): List<String> = tasks.checkNameUniqueness(name)

    suspend fun insert(task: Task, attachmentList: List<Attachment> = emptyList()) {
        tasks.insert(task)
        if (attachmentList.isNotEmpty())
            attachmentList.forEach { attachments.insert(it) }

        TaskWidgetProvider.triggerRefresh(context)

        // Check if notifications for tasks are turned on and check if
        // the task is not finished, then schedule a notification
        if (preferenceManager.taskReminder && !task.isFinished && task.isDueDateInFuture() &&
            task.hasDueDate()
        ) {

            val data = BaseWorker.convertTaskToData(task)
            val request = OneTimeWorkRequest.Builder(TaskNotificationWorker::class.java)
                .setInputData(data)
                .addTag(task.taskID)
                .build()
            workManager.enqueueUniqueWork(
                task.taskID, ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    suspend fun remove(task: Task) {
        tasks.remove(task)

        TaskWidgetProvider.triggerRefresh(context)

        // If the task is important, its persistent notification should
        // be canceled.
        if (task.isImportant)
            notificationManager.cancel(task.taskID, BaseWorker.NOTIFICATION_ID_TASK)

        workManager.cancelUniqueWork(task.taskID)
    }

    suspend fun update(task: Task, attachmentList: List<Attachment> = emptyList()) {
        tasks.update(task)
        attachments.removeUsingTaskID(task.taskID)
        if (attachmentList.isNotEmpty())
            attachmentList.forEach { attachments.insert(it) }

        TaskWidgetProvider.triggerRefresh(context)

        // If we have a persistent notification,
        // we should dismiss it when the user updates
        // the task to finish
        if (task.isFinished || !task.isImportant || task.dueDate?.isBeforeNow() == true)
            notificationManager.cancel(task.taskID, BaseWorker.NOTIFICATION_ID_TASK)

        // Check if notifications for tasks is turned on and if the task
        // is not finished then reschedule the notification from
        // WorkManager
        if (preferenceManager.taskReminder && !task.isFinished) {

            workManager.cancelUniqueWork(task.taskID)
            val data = BaseWorker.convertTaskToData(task)
            val request = OneTimeWorkRequest.Builder(TaskNotificationWorker::class.java)
                .setInputData(data)
                .addTag(task.taskID)
                .build()
            workManager.enqueueUniqueWork(
                task.taskID, ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    suspend fun setFinished(taskID: String, status: Boolean) {
        if (status)
            tasks.setFinished(taskID, 1)
        else tasks.setFinished(taskID, 0)
    }

    suspend fun addAttachment(attachment: Attachment) {
        attachments.insert(attachment)
    }
}