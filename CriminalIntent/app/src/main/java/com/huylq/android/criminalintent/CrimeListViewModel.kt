package com.huylq.android.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    val crimes = mutableListOf<Crime>()

    init {
        for (i in 0..100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = (i and 1) == 0
            crimes += crime
        }
    }

}