package com.huylq.android.criminalintent

import android.view.View
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Crime (
        @PrimaryKey val id: UUID = UUID.randomUUID(),
        var title: String = "",
        var date: Date = Date(),
        var isSolved: Boolean = false
) {

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy - HH:mm", Locale.ENGLISH)
        return dateFormat.format(date)
    }

    fun getVisibility(): Int {
        return if (isSolved)
            View.VISIBLE
        else
            View.GONE
    }
}