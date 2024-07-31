package project.stn991662556.fifaworldcup2022

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import project.stn991662556.fifaworldcup2022.databinding.EditRowTeamBinding
import project.stn991662556.fifaworldcup2022.databinding.FragmentEditTeamBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTeamFragment : Fragment() {
    private lateinit var binding: FragmentEditTeamBinding
    private val firestoreDB = FirebaseFirestore.getInstance()
    private lateinit var teamList: MutableList<DocumentSnapshot>

    companion object {
        const val EDIT_TEAM_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val continents = listOf(
            "All", "Africa", "Antarctica", "Asia",
            "Europe", "North America", "Oceania", "South America"
        )
        val continentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, continents)
        continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editSpinner.adapter = continentAdapter

        binding.editSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refreshTeamList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.editCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTeams(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchTeams(searchQuery: String) {
        val filteredTeams = teamList.filter {
            val teamName = it.getString("TeamName") ?: ""
            teamName.contains(searchQuery, ignoreCase = true)
        }
        refreshTeamListWithFilteredData(filteredTeams)
    }

    private fun refreshTeamListWithFilteredData(filteredTeams: List<DocumentSnapshot>) {
        binding.teamTable.removeAllViews()
        for (document in filteredTeams) {
            val rowBinding = EditRowTeamBinding.inflate(LayoutInflater.from(requireContext()), binding.teamTable, false)
            val teamNameTextView: TextView = rowBinding.editTeamNameTextView
            val continentTextView: TextView = rowBinding.editContinentTextView
            val editButton: Button = rowBinding.editButton

            teamNameTextView.text = document.getString("TeamName")
            continentTextView.text = document.getString("Continent")

            editButton.setOnClickListener {
                val teamName = document.getString("TeamName")
                val continent = document.getString("Continent")
                val games = document.getLong("Played")
                val won = document.getLong("Won")
                val drawn = document.getLong("Drawn")
                val lost = document.getLong("Lost")
                val documentId = document.id

                val fragment = EditTeamDisplayFragment().apply {
                    arguments = Bundle().apply {
                        putString("TeamName", teamName)
                        putString("Continent", continent)
                        putLong("Played", games ?: 0)
                        putLong("Won", won ?: 0)
                        putLong("Drawn", drawn ?: 0)
                        putLong("Lost", lost ?: 0)
                        putString("DocumentId", documentId)
                    }
                }

                // Use supportFragmentManager from AppCompatActivity
                (requireActivity() as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,  fragment)
                    .addToBackStack(null)
                    .commit()

                binding.editCountry.text.clear()
            }

            binding.teamTable.addView(rowBinding.root)
        }
    }

    private fun refreshTeamList() {
        binding.teamTable.removeAllViews()

        val selectedContinent = binding.editSpinner.selectedItem.toString()

        val query = if (selectedContinent.isNotEmpty() && selectedContinent != "All") {
            firestoreDB.collection("Teams")
                .whereEqualTo("Continent", selectedContinent)
        } else {
            firestoreDB.collection("Teams")
        }

        query.get()
            .addOnSuccessListener { documents ->
                teamList = documents.toMutableList()
                refreshTeamListWithFilteredData(teamList)
            }
            .addOnFailureListener { exception ->
                Log.w("EditTeamFragment", "Error getting documents: ", exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TEAM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val dataUpdated = data?.getBooleanExtra("dataUpdated", false) ?: false
            if (dataUpdated) {
                refreshTeamList()
            }
        }
    }
}
