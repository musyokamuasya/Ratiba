package dev.ciox.ratiba.database.converter

import androidx.room.TypeConverter
import dev.ciox.ratiba.features.subject.Subject

class ColorConverter private constructor() {

    companion object {
        @JvmStatic
        @TypeConverter
        fun toColor(actualColor: Int): Subject.Tag? {
            return Subject.Tag.convertColorToTag(actualColor)
        }

        @JvmStatic
        @TypeConverter
        fun fromColor(tag: Subject.Tag): Int {
            return tag.color
        }
    }

}