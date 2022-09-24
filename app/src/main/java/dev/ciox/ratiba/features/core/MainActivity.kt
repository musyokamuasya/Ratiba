package dev.ciox.ratiba.features.core

import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import dev.ciox.ratiba.R
import dev.ciox.ratiba.components.bottomsheet.NavigationSheet
import dev.ciox.ratiba.components.utils.NotificationChannelManager
import dev.ciox.ratiba.databinding.ActivityMainBinding
import dev.ciox.ratiba.features.notifications.task.TaskReminderWorker
import dev.ciox.ratiba.features.shared.abstracts.BaseActivity
import dev.ciox.ratiba.features.task.TaskViewModel

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    private var controller: NavController? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller = findNavController(androidx.compose.runtime.R.id.navigationHostFragment)

        intent?.also { intent ->
            when (intent.action) {
                ACTION_WIDGET_TASK -> {
                    val task: Task? = intent.getParcelableExtra(EXTRA_TASK)
                    val subject: Subject? = intent.getParcelableExtra(EXTRA_SUBJECT)
                    val attachments: List<Attachment>? =
                        intent.getParcelableListExtra(EXTRA_ATTACHMENTS)

                    val args = bundleOf(
                        TaskEditor.EXTRA_TASK to task?.let { Task.toBundle(it) },
                        TaskEditor.EXTRA_SUBJECT to subject?.let { Subject.toBundle(it) },
                        TaskEditor.EXTRA_ATTACHMENTS to attachments
                    )

                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_task, args)
                }
                ACTION_WIDGET_EVENT -> {
                    val event: Event? = intent.getParcelableExtra(EXTRA_EVENT)
                    val subject: Subject? = intent.getParcelableExtra(EXTRA_SUBJECT)

                    val args = bundleOf(
                        EventEditor.EXTRA_EVENT to event?.let { Event.toBundle(it) },
                        EventEditor.EXTRA_SUBJECT to subject?.let { Subject.toBundle(it) }
                    )

                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_event, args)
                }
                ACTION_WIDGET_SUBJECT -> {
                    val subject: Subject? = intent.getParcelableExtra(EXTRA_SUBJECT)
                    val schedules: List<Schedule>? = intent.getParcelableListExtra(EXTRA_SCHEDULES)

                    val args = bundleOf(
                        SubjectEditor.EXTRA_SUBJECT to subject?.let { Subject.toBundle(it) },
                        SubjectEditor.EXTRA_SCHEDULE to schedules
                    )

                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_subject, args)
                }
                ACTION_SHORTCUT_TASK -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_task)
                }
                ACTION_SHORTCUT_EVENT -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_event)
                }
                ACTION_SHORTCUT_SUBJECT -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_editor_subject)
                }
                ACTION_NAVIGATION_TASK -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_main)
                }
                ACTION_NAVIGATION_EVENT -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_main)
                }
                ACTION_NAVIGATION_SUBJECT -> {
                    controller?.navigate(androidx.compose.runtime.R.id.action_to_navigation_main)
                }
            }
        }

        TaskReminderWorker.reschedule(this.applicationContext)

        with(supportFragmentManager) {
            setFragmentResultListener(NavigationSheet.REQUEST_KEY, this@MainActivity) { _, args ->
                args.getInt(NavigationSheet.EXTRA_DESTINATION).also {
                    controller?.navigate(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(NotificationChannelManager(this)) {
                register(
                    NotificationChannelManager.CHANNEL_ID_GENERIC,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                register(
                    NotificationChannelManager.CHANNEL_ID_TASK,
                    groupID = NotificationChannelManager.CHANNEL_GROUP_ID_REMINDERS
                )
                register(
                    NotificationChannelManager.CHANNEL_ID_EVENT,
                    groupID = NotificationChannelManager.CHANNEL_GROUP_ID_REMINDERS
                )
                register(
                    NotificationChannelManager.CHANNEL_ID_CLASS,
                    groupID = NotificationChannelManager.CHANNEL_GROUP_ID_REMINDERS
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean = controller?.navigateUp() ?: false

    companion object {
        const val ACTION_SHORTCUT_TASK = "action:shortcut:task"
        const val ACTION_SHORTCUT_EVENT = "action:shortcut:event"
        const val ACTION_SHORTCUT_SUBJECT = "action:shortcut:subject"

        const val ACTION_WIDGET_TASK = "action:widget:task"
        const val ACTION_WIDGET_EVENT = "action:widget:event"
        const val ACTION_WIDGET_SUBJECT = "action:widget:subject"

        const val ACTION_NAVIGATION_TASK = "action:navigation:task"
        const val ACTION_NAVIGATION_EVENT = "action:navigation:event"
        const val ACTION_NAVIGATION_SUBJECT = "action:navigation:subject"

        const val EXTRA_TASK = "extra:task"
        const val EXTRA_EVENT = "extra:event"
        const val EXTRA_SUBJECT = "extra:subject"
        const val EXTRA_ATTACHMENTS = "extra:attachments"
        const val EXTRA_SCHEDULES = "extra:schedules"
    }

}