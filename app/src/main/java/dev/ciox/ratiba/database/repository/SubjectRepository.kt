package dev.ciox.ratiba.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.ciox.ratiba.components.utils.PreferenceManager
import dev.ciox.ratiba.database.dao.ScheduleDAO
import dev.ciox.ratiba.database.dao.SubjectDAO
import dev.ciox.ratiba.features.notifications.subject.ClassNotificationWorker
import dev.ciox.ratiba.features.schedule.Schedule
import dev.ciox.ratiba.features.shared.abstracts.BaseWorker
import dev.ciox.ratiba.features.subject.Subject
import dev.ciox.ratiba.features.subject.SubjectPackage
import dev.ciox.ratiba.features.subject.widget.SubjectWidgetProvider
import dev.ciox.ratiba.features.task.widget.TaskWidgetProvider.Companion.triggerRefresh
import javax.inject.Inject

class SubjectRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val subjects: SubjectDAO,
    private val schedules: ScheduleDAO,
    private val preferenceManager: PreferenceManager,
    private val workManager: WorkManager
) {

    fun fetchLiveData(): LiveData<List<SubjectPackage>> = subjects.fetchLiveData()

    fun fetchArchivedLiveData(): LiveData<List<SubjectPackage>> = subjects.fetchArchivedLiveData()

    suspend fun fetch(): List<SubjectPackage> = subjects.fetchAsPackage()

    suspend fun checkCodeExists(code: String?): List<String> = subjects.checkCodeUniqueness(code)

    suspend fun insert(subject: Subject, scheduleList: List<Schedule>) {
        subjects.insert(subject)
        scheduleList.forEach {
            schedules.insert(it)
        }

        SubjectWidgetProvider.triggerRefresh(context)

        if (preferenceManager.subjectReminder) {
            scheduleList.forEach {
                it.subject = subject.code

                val data = BaseWorker.convertScheduleToData(it)
                val request = OneTimeWorkRequest.Builder(ClassNotificationWorker::class.java)
                    .setInputData(data)
                    .addTag(subject.subjectID)
                    .addTag(it.scheduleID)
                    .build()

                workManager.enqueueUniqueWork(
                    it.scheduleID, ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    suspend fun remove(subject: Subject) {
        subjects.remove(subject)

        SubjectWidgetProvider.triggerRefresh(context)

        workManager.cancelAllWorkByTag(subject.subjectID)
    }

    suspend fun update(subject: Subject, scheduleList: List<Schedule> = emptyList()) {
        subjects.update(subject)
        schedules.removeUsingSubjectID(subject.subjectID)
        if (scheduleList.isNotEmpty())
            scheduleList.forEach { schedules.insert(it) }

        SubjectWidgetProvider.triggerRefresh(context)

        if (preferenceManager.subjectReminder) {
            scheduleList.forEach {
                workManager.cancelAllWorkByTag(it.scheduleID)

                it.subject = subject.code
                val data = BaseWorker.convertScheduleToData(it)
                val request = OneTimeWorkRequest.Builder(ClassNotificationWorker::class.java)
                    .setInputData(data)
                    .addTag(subject.subjectID)
                    .addTag(it.scheduleID)
                    .build()
                workManager.enqueueUniqueWork(
                    it.scheduleID, ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }
}