package project.stn991662556.fifaworldcup2022

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
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
import android.widget.EditText
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteTeamFragment : Fragment() {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private lateinit var teamList: MutableList<DocumentSnapshot>
    private lateinit var deleteCountryEditText: EditText
    private lateinit var continentSpinner: Spinner
    private lateinit var teamTable: TableLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delete_team, container, false)

        deleteCountryEditText = view.findViewById(R.id.deleteCountry)
        continentSpinner = view.findViewById(R.id.continentSpinnerDelete)
        teamTable = view.findViewById(R.id.team_table)

        val continents = listOf(
            "All", "Africa", "Antarctica", "Asia",
            "Europe", "North America", "Oceania", "South America"
        )
        val continentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, continents)
        continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        continentSpinner.adapter = continentAdapter

        continentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                refreshTeamList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        deleteCountryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchTeams(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun deleteTeams() {
        val country = deleteCountryEditText.text.toString().trim()
        val continent = continentSpinner.selectedItem.toString()

        val query = if (country.isNotEmpty()) {
            firestoreDB.collection("Teams")
                .whereEqualTo("TeamName", country)
        } else {
            firestoreDB.collection("Teams")
                .whereEqualTo("Continent", continent)
        }

        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestoreDB.collection("Teams")
                        .document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("DeleteTeamFragment", "DocumentSnapshot successfully deleted!")
                            refreshTeamList()
                        }
                        .addOnFailureListener { e ->
                            Log.w("DeleteTeamFragment", "Error deleting document", e)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DeleteTeamFragment", "Error getting documents: ", exception)
            }
    }

    private fun refreshTeamList() {
        teamTable.removeAllViews()

        val selectedContinent = continentSpinner.selectedItem.toString()

        val query = if (selectedContinent.isNotEmpty() && selectedContinent != "All") {
            firestoreDB.collection("Teams")
                .whereEqualTo("Continent", selectedContinent)
        } else {
            firestoreDB.collection("Teams")
        }

        query.get()
            .addOnSuccessListener { documents ->
                teamList = documents.toMutableList()
                for (document in documents) {
                    val rowView = layoutInflater.inflate(R.layout.delete_row_team, null)

                    val teamNameTextView: TextView = rowView.findViewById(R.id.teamNameTextView)
                    val continentTextView: TextView = rowView.findViewById(R.id.continentTextView)
                    val deleteButton: Button = rowView.findViewById(R.id.deleteButton)

                    teamNameTextView.text = document.getString("TeamName")
                    continentTextView.text = document.getString("Continent")

                    deleteButton.setOnClickListener {
                        showDeleteConfirmationDialog(document)
                    }

                    teamTable.addView(rowView)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DeleteTeamFragment", "Error getting documents: ", exception)
            }
    }

    private fun searchTeams(searchQuery: String) {
        val filteredTeams = teamList.filter {
            val teamName = it.getString("TeamName") ?: ""
            teamName.contains(searchQuery, ignoreCase = true)
        }
        refreshTeamListWithFilteredData(filteredTeams)
    }

    private fun refreshTeamListWithFilteredData(filteredTeams: List<DocumentSnapshot>) {
        teamTable.removeAllViews()
        for (document in filteredTeams) {
            val rowView = layoutInflater.inflate(R.layout.delete_row_team, null)

            val teamNameTextView: TextView = rowView.findViewById(R.id.teamNameTextView)
            val continentTextView: TextView = rowView.findViewById(R.id.continentTextView)
            val deleteButton: Button = rowView.findViewById(R.id.deleteButton)

            teamNameTextView.text = document.getString("TeamName")
            continentTextView.text = document.getString("Continent")

            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(document)
            }

            teamTable.addView(rowView)
        }
    }

    private fun showDeleteConfirmationDialog(document: DocumentSnapshot) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this team?")
            .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                deleteTeam(document.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteTeam(documentId: String) {
        firestoreDB.collection("Teams")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("DeleteTeamFragment", "DocumentSnapshot successfully deleted!")
                refreshTeamList()
            }
            .addOnFailureListener { e ->
                Log.w("DeleteTeamFragment", "Error deleting document", e)
            }
    }

}
