package com.huylq.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var adapter: CrimeAdapter

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CrimeAdapter()
        crimeListViewModel.crimeListLiveData.observe(
                viewLifecycleOwner
        ) { crimes ->
            crimes?.let {
                Log.i(TAG, "Got ${crimes.size} crimes")
                adapter.submitList(crimes)
            }
        }
        crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = view.findViewById(R.id.crime_title)
        private val dateTextView: TextView = view.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = view.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.getFormattedDate()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(p0: View?) {
            val action = CrimeListFragmentDirections.actionOpenCrimeDetail(crime.id)
            findNavController().navigate(action)
        }
    }

    private inner class CrimeAdapter() : ListAdapter<Crime, CrimeHolder>(CrimeDiff()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)
            holder.bind(crime)
        }
    }

    private inner class CrimeDiff : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }
}