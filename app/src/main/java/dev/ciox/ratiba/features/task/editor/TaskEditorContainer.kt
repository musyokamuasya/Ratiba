package dev.ciox.ratiba.features.task.editor

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import dev.ciox.ratiba.databinding.ActivityContainerTaskBinding
import dev.ciox.ratiba.features.shared.abstracts.BaseActivity

@AndroidEntryPoint
class TaskEditorContainer : BaseActivity() {
    private lateinit var binding: ActivityContainerTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}