package com.huylq.android.criminalintent

import java.text.SimpleDateFormat
import java.util.*

data class Crime (
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
) {

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH)
        return dateFormat.format(date)
    }
}