package com.huylq.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.Calendar

class DatePickerFragment : DialogFragment() {

    private val calendar = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val resultDate = calendar.time

            val action = DatePickerFragmentDirections.actionOpenTimePicker(resultDate)
            findNavController().navigate(action)
        }

        val safeArgs: DatePickerFragmentArgs by navArgs()
        val date = safeArgs.date
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