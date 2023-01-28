package com.huylq.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.huylq.android.criminalintent.databinding.FragmentCrimeListBinding
import com.huylq.android.criminalintent.databinding.ListItemCrimeBinding
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    private lateinit var adapter: CrimeAdapter
    private lateinit var binding: FragmentCrimeListBinding

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCrimeListBinding?>(
            inflater,
            R.layout.fragment_crime_list,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            crimeRecyclerView.layoutManager = LinearLayoutManager(context)
            newButton.setOnClickListener { createNewCrime() }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_crime_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_crime -> {
                        createNewCrime()
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = CrimeAdapter()
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner
        ) { crimes ->
            crimes?.let {
                if (it.isNotEmpty()) {
                    Log.i(TAG, "Got ${crimes.size} crimes")
                    adapter.submitList(crimes)
                    binding.apply {
                        crimeRecyclerView.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    }
                } else {
                    binding.apply {
                        crimeRecyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.crimeRecyclerView.adapter = adapter
    }

    private fun navigateToCrimeDetail(crimeId: UUID) {
        val action = CrimeListFragmentDirections.actionOpenCrimeDetail(crimeId)
        findNavController().navigate(action)
    }

    private fun createNewCrime() {
        val crime = Crime()
        crimeListViewModel.addCrime(crime)
        navigateToCrimeDetail(crime.id)
    }

    private inner class CrimeHolder(private val binding: ListItemCrimeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            binding.crime = crime
        }

        override fun onClick(p0: View?) {
            navigateToCrimeDetail(crime.id)
        }
    }

    private inner class CrimeAdapter() : ListAdapter<Crime, CrimeHolder>(CrimeDiff()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val binding: ListItemCrimeBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.list_item_crime, parent, false)
            return CrimeHolder(binding)
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