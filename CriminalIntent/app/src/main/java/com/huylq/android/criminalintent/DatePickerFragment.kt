package com.huylq.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val resultDate: Date = GregorianCalendar(year, month, day).time

            findNavController().previousBackStackEntry?.savedStateHandle?.set(ARG_DATE, resultDate)
        }

        val safeArgs: DatePickerFragmentArgs by navArgs()
        val date = safeArgs.date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initYear = calendar.get(Calendar.YEAR)
        val initMonth = calendar.get(Calendar.MONTH)
        val initDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            dateListener,
            initYear,
            initMonth,
            initDay
        )
    }
}