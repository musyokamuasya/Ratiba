package dev.ciox.ratiba.components.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import dev.ciox.ratiba.R
import dev.ciox.ratiba.databinding.LayoutSheetOptionsBinding
import dev.ciox.ratiba.features.shared.abstracts.BaseBottomSheet
import dev.ciox.ratiba.features.shared.adapters.MenuAdapter
import java.time.LocalTime

class NavigationSheet(manager: FragmentManager) : BaseBottomSheet(manager),
    MenuAdapter.MenuItemListener {
    private var _binding: LayoutSheetOptionsBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutSheetOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuTitleView.text = when (LocalTime.now().hour) {
            in 0..6 -> getString(R.string.greeting_default)
            in 7..12 -> getString(R.string.greeting_morning)
            in 13..18 -> getString(R.string.greeting_afternoon)
            in 19..23 -> getString(R.string.greeting_evening)
            else -> getString(R.string.greeting_default)
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(activity, R.menu.navigation_main, this@NavigationSheet)
        }
    }

    override fun onItemSelected(id: Int) {
        setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_DESTINATION to id))
        this.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "request:navigation"
        const val EXTRA_DESTINATION = "extra:destination"

        fun show(manager: FragmentManager) {
            NavigationSheet(manager).show()
        }
    }
}