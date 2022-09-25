package dev.ciox.ratiba.features.task.editor

import android.content.ClipboardManager
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ciox.ratiba.database.dao.ScheduleDAO
import dev.ciox.ratiba.database.repository.TaskRepository
import dev.ciox.ratiba.features.attachments.Attachment
import dev.ciox.ratiba.features.schedule.Schedule
import dev.ciox.ratiba.features.subject.Subject
import dev.ciox.ratiba.features.task.Task
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class TaskEditorViewModel @Inject constructor(
    private val clipboardManager: ClipboardManager,
    private val scheduleDao: ScheduleDAO,
    private val repository: TaskRepository
) : ViewModel() {

    private val _task: MutableLiveData<Task> = MutableLiveData(Task())
    private val _attachments: MutableLiveData<ArrayList<Attachment>> =
        MutableLiveData(arrayListOf())
    private val _subject: MutableLiveData<Subject?> = MutableLiveData(null)
    private val _isNameExists: MutableLiveData<Boolean> = MutableLiveData(false)

    val task: LiveData<Task> = _task
    val attachments: LiveData<List<Attachment>> = Transformations.map(_attachments) { it.toList() }
    val subject: LiveData<Subject?> = _subject
    val isNameExists: LiveData<Boolean> = _isNameExists

    var schedules: ArrayList<Schedule> = arrayListOf()

    fun getTask(): Task? {
        return task.value
    }

    fun setTask(task: Task?) {
        _task.value = task!!
    }

    fun getSubject(): Subject? {
        return subject.value
    }

    fun setSubject(subject: Subject?) {
        _subject.value = subject

        if (subject != null) {
            fetchSchedulesFromDatabase(subject.subjectID)
            setTaskSubjectID(subject.subjectID)
        } else {
            schedules.clear()
            setTaskSubjectID(null)
        }
    }

    fun getAttachments(): List<Attachment> {
        return attachments.value ?: emptyList()
    }

    fun setAttachments(items: ArrayList<Attachment>) {
        _attachments.value = items
    }

    fun addAttachment(attachment: Attachment) {
        val items = ArrayList(getAttachments())
        items.add(attachment)
        setAttachments(items.distinctBy { it.attachmentID }.toArrayList())
    }

    fun removeAttachment(attachment: Attachment) {
        val items = ArrayList(getAttachments())
        items.remove(attachment)
        setAttachments(items.distinctBy { it.attachmentID }.toArrayList())
    }


    fun checkNameUniqueness(name: String?) = viewModelScope.launch {
        val result = repository.checkNameUniqueness(name)
        _isNameExists.value = !result.contains(name) && result.isNotEmpty()
    }

    fun getID(): String? {
        return getTask()?.taskID
    }

    fun getName(): String? {
        return getTask()?.name
    }

    fun setName(name: String?) {
        // Check if the same value is being set
        if (name == getName())
            return

        val task = getTask()
        task?.name = name
        setTask(task)
    }

    fun getDueDate(): ZonedDateTime? {
        return getTask()?.dueDate
    }

    fun setDueDate(dueDate: ZonedDateTime?) {
        val task = getTask()
        task?.dueDate = dueDate
        setTask(task)
    }

    fun getTaskSubjectID(): String? {
        return getTask()?.subject
    }

    fun setTaskSubjectID(id: String?) {
        val task = getTask()
        task?.subject = id
        setTask(task)
    }