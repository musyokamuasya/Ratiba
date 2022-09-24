package dev.ciox.ratiba.features.event

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import dev.ciox.ratiba.features.subject.Subject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventPackage @JvmOverloads constructor(
    @Embedded
    var event: Event,
    @Embedded
    var subject: Subject? = null
) : Parcelable {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventPackage>() {
            override fun areItemsTheSame(oldItem: EventPackage, newItem: EventPackage): Boolean {
                return oldItem.event.eventID == newItem.event.eventID
            }

            override fun areContentsTheSame(oldItem: EventPackage, newItem: EventPackage): Boolean {
                return oldItem == newItem
            }
        }
    }
}