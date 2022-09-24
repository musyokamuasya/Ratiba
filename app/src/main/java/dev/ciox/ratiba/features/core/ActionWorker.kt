package dev.ciox.ratiba.features.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ciox.ratiba.components.service.NotificationActionService
import dev.ciox.ratiba.database.repository.TaskRepository
import dev.ciox.ratiba.features.shared.abstracts.BaseWorker

@HiltWorker
class ActionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: TaskRepository
) : BaseWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val action = inputData.getString(NotificationActionService.EXTRA_ACTION)
        val taskID = inputData.getString(NotificationActionService.EXTRA_TASK_ID)
        if (action.isNullOrBlank() || taskID.isNullOrBlank())
            return Result.success()

        if (action == NotificationActionService.ACTION_FINISHED)
            repository.setFinished(taskID, true)

        return Result.success()
    }

}