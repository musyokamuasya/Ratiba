package dev.ciox.ratiba.features.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ciox.ratiba.features.log.Log
import dev.ciox.ratiba.features.shared.abstracts.BaseWorker
import java.time.ZonedDateTime

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: LogRepository,
    private val notificationManager: NotificationManager
) : BaseWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val log: Log = convertDataToLog(inputData)
        log.dateTimeTriggered = ZonedDateTime.now()

        repository.insert(log)
        if (log.isImportant)
            sendNotification(log, notificationManager, log.data)
        else sendNotification(log, notificationManager)

        return Result.success()
    }
}