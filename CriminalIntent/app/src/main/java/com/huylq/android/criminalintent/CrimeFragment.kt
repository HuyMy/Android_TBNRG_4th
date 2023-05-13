package com.huylq.android.criminalintent

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.huylq.android.criminalintent.databinding.FragmentCrimeBinding
import java.util.*

private const val TAG = "CrimeFragment"

private const val DATE_FORMAT = "EEE, MMM, dd"
class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var binding: FragmentCrimeBinding
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this)[CrimeDetailViewModel::class.java]
    }

    private val getContact = registerForActivityResult(ActivityResultContracts.PickContact()) { contactUri ->
        if (contactUri == null)
            return@registerForActivityResult

        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val cursor = requireActivity().contentResolver
            .query(contactUri, queryFields, null, null, null)

        cursor?.use {
            if (it.count > 0) {
                it.moveToFirst()
                val suspect = it.getString(0)
                crime.suspect = suspect
                crimeDetailViewModel.saveCrime(crime)
                binding.crimeSuspect.text = suspect
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val safeArgs: CrimeFragmentArgs by navArgs()
        val crimeId = safeArgs.crimeId as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        binding = DataBindingUtil.inflate<FragmentCrimeBinding?>(inflater, R.layout.fragment_crime, container, false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
                viewModel = crimeDetailViewModel
                view = this@CrimeFragment
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
                viewLifecycleOwner
        ) {
            it?.let {
                crime = it
                updateUI()
            }
        }

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.crime_detail_dest)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains(ARG_DATE)) {
                val resultDate = navBackStackEntry.savedStateHandle.get<Date>(ARG_DATE);
                resultDate?.run {
                    crime.date = this
                    crimeDetailViewModel.saveCrime(crime)
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI() {
        binding.apply {
            crimeSolved.jumpDrawablesToCurrentState()
            if (crime.suspect.isNotEmpty())
                crimeSuspect.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    fun onDateButtonClicked() {
        val action = CrimeFragmentDirections.actionOpenDatePicker(crime.date)
        findNavController().navigate(action)
    }

    fun onReportButtonClicked() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
        }.also { intent ->
            val chooserIntent =
                Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooserIntent)
        }
    }

    fun onSuspectButtonClicked() {
        getContact.launch(null)
    }
}