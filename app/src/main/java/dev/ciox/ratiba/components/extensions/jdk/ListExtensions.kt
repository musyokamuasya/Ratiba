package dev.ciox.ratiba.components.extensions.jdk

import dev.ciox.ratiba.features.attachments.Attachment
import dev.ciox.ratiba.features.event.Event
import dev.ciox.ratiba.features.log.Log
import dev.ciox.ratiba.features.schedule.Schedule
import dev.ciox.ratiba.features.subject.Subject
import dev.ciox.ratiba.features.task.Task

/**
 *  Extension function to create an ArrayList
 *  from the current List object
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return ArrayList(this)
}

fun <T> List<T>.getIndexByID(id: String): Int {
    this.forEachIndexed { index, it ->
        if (it is Task)
            if (it.taskID == id) return index

        if (it is Event)
            if (it.eventID == id) return index

        if (it is Subject)
            if (it.subjectID == id) return index

        if (it is Attachment)
            if (it.attachmentID == id) return index

        if (it is Log)
            if (it.logID == id) return index

        if (it is Schedule)
            if (it.scheduleID == id) return index
    }
    return -1
}