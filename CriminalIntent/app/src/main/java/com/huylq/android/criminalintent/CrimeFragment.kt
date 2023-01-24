package com.huylq.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var binding: FragmentCrimeBinding
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this)[CrimeDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            crimeDate.text = crime.getFormattedDate()
            crimeSolved.jumpDrawablesToCurrentState()
        }
    }

    fun onDateButtonClicked() {
        val action = CrimeFragmentDirections.actionOpenDatePicker(crime.date)
        findNavController().navigate(action)
    }
}