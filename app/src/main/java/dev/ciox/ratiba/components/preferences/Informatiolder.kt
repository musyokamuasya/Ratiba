package dev.ciox.ratiba.components.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.Preference
import dev.ciox.ratiba.R

@SuppressLint("RestrictedApi")
class InformationHolder(
    context: Context,
    attr: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : Preference(context, attr, defStyleAttr, defStyleRes) {

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int)
            : this(context, attr, defStyleAttr, 0)

    constructor(context: Context, attr: AttributeSet?)
            : this(
        context, attr, TypedArrayUtils.getAttr(
            context,
            androidx.preference.R.attr.preferenceStyle, android.R.attr.preferenceStyle
        )
    )

    constructor(context: Context) : this(context, null)

    init {
        layoutResource = R.layout.layout_preference_info
    }

    override fun onClick() {}
    override fun performClick() {}
    override fun performClick(view: View) {}

}