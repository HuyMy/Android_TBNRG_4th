package com.huylq.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"
private const val NORMAL_CRIME = 0
private const val SERIOUS_CRIME = 1

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI()

        return view
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    private inner class NormalCrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        override fun onClick(p0: View?) {
            Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class SeriousCrimeHolder(view: View)
        :RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)
        private val callPoliceButton: Button = view.findViewById(R.id.call_police_button)

        init {
            itemView.setOnClickListener(this)
            callPoliceButton.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        override fun onClick(p0: View?) {
            if (p0?.id == R.id.call_police_button) {
                Toast.makeText(context, "${crime.title} will be reported to police", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>)
        :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == SERIOUS_CRIME) {
                val view = layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false)
                SeriousCrimeHolder(view)
            } else {
                val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                NormalCrimeHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime = crimes[position]
            val viewType = getItemViewType(position)
            if (viewType == SERIOUS_CRIME) {
                (holder as SeriousCrimeHolder).bind(crime)
            } else {
                (holder as NormalCrimeHolder).bind(crime)
            }
        }

        override fun getItemCount() = crimes.size

        override fun getItemViewType(position: Int) = when {
            crimes[position].requiresPolice -> SERIOUS_CRIME
            else -> NORMAL_CRIME
        }
    }
}