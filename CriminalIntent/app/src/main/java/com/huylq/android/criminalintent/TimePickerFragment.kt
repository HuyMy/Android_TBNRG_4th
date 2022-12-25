package com.huylq.android.criminalintent

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.Calendar

const val ARG_DATE = "date"

class TimePickerFragment: DialogFragment() {
    private val calendar = Calendar.getInstance()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)
            val resultDate = calendar.time

            findNavController().previousBackStackEntry?.savedStateHandle?.set(ARG_DATE, resultDate)
        }

        val safeArgs: TimePickerFragmentArgs by navArgs()
        val date = safeArgs.date
        calendar.time = date
        val initHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initMinute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(
            requireContext(),
            timeListener,
            initHour,
            initMinute,
            false
        )
    }
}