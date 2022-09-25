package dev.ciox.ratiba.features.notifications.subject

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ciox.ratiba.components.extensions.jdk.isAfterNow
import dev.ciox.ratiba.components.utils.PreferenceManager
import dev.ciox.ratiba.features.log.Log
import dev.ciox.ratiba.features.notifications.NotificationWorker
import dev.ciox.ratiba.features.schedule.Schedule
import dev.ciox.ratiba.features.shared.abstracts.BaseWorker
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class ClassNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val preferenceManager: PreferenceManager,
    private val workManager: WorkManager
) : BaseWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val schedule = convertDataToSchedule(inputData)
        val log = Log().apply {
            title = schedule.subject
            content = schedule.format(applicationContext)
            type = Log.TYPE_CLASS
            isImportant = false
            data = schedule.scheduleID
        }

        val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
        request.setInputData(convertLogToData(log))

        schedule.parseDaysOfWeek().forEach {
            var triggerTime = schedule.startTime?.let { time -> Schedule.getNextWeekDay(it, time) }

            when (preferenceManager.subjectReminderInterval) {
                PreferenceManager.SUBJECT_REMINDER_INTERVAL_5_MINUTES ->
                    triggerTime = triggerTime?.minusMinutes(5)
                PreferenceManager.SUBJECT_REMINDER_INTERVAL_15_MINUTES ->
                    triggerTime = triggerTime?.minusMinutes(15)
                PreferenceManager.SUBJECT_REMINDER_INTERVAL_30_MINUTES ->
                    triggerTime = triggerTime?.minusMinutes(30)
            }

            if (triggerTime?.isAfterNow() == true)
                request.setInitialDelay(
                    Duration.between(ZonedDateTime.now(), triggerTime).toMinutes(),
                    TimeUnit.MINUTES
                )

            val weekFields = WeekFields.of(Locale.getDefault())
            val weekNumber = triggerTime?.get(weekFields.weekOfMonth())
            if (!schedule.hasWeek(weekNumber!!))
                return@forEach

            workManager.enqueueUniqueWork(
                schedule.scheduleID, ExistingWorkPolicy.APPEND,
                request.build()
            )
            reschedule(schedule.scheduleID, inputData, triggerTime)
        }

        return Result.success()
    }

    private fun reschedule(tag: String, data: Data, triggerTime: ZonedDateTime?) {
        val request = OneTimeWorkRequest.Builder(ClassNotificationWorker::class.java)
            .setInputData(data)
            .setInitialDelay(
                Duration.between(ZonedDateTime.now(), triggerTime).toMinutes(),
                TimeUnit.MINUTES
            )
        workManager.enqueueUniqueWork(
            tag, ExistingWorkPolicy.APPEND,
            request.build()
        )
    }
}