package project.stn991662556.fifaworldcup2022

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayTeamsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DisplayTeamsFragment : Fragment() {
    private lateinit var sortOptions: RadioGroup
    private lateinit var teamTable: TableLayout
    private lateinit var teams: MutableList<Team>
    private lateinit var adapter: TeamAdapter
    private val firestoreDB = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_teams, container, false)

        sortOptions = view.findViewById(R.id.sortOptions)
        teamTable = view.findViewById(R.id.team_table)

        teams = mutableListOf()
        adapter = TeamAdapter(teams)

        sortOptions.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.sortByName -> sortTeamsByName()
                R.id.sortByContinent -> sortTeamsByContinent()
                R.id.sortByPoints -> sortTeamsByPoints()
            }
        }

        loadTeamData()

        return view
    }

    private fun loadTeamData() {
        firestoreDB.collection("Teams")
            .get()
            .addOnSuccessListener { snapshot ->
                teams.clear()
                for (doc in snapshot.documents) {
                    val team = Team.fromSnapshot(doc) // Convert DocumentSnapshot to Team object
                    teams.add(team)

                    // Log the document ID to verify
                    val documentId = doc.id
                    Log.d("Document ID", documentId)
                }
                adapter.notifyDataSetChanged()
                displayTeamsInTable()
            }
            .addOnFailureListener { exception ->
                Log.e("DisplayTeamsFragment", "Error getting team data", exception)
            }
    }

    // Display teams in the TableLayout
    private fun displayTeamsInTable() {
        teamTable.removeAllViews()

        for (team in teams) {
            val row = TableRow(requireContext())
            val layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            row.layoutParams = layoutParams

            val nameTextView = TextView(requireContext())
            nameTextView.text = team.teamName
            nameTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val continentTextView = TextView(requireContext())
            continentTextView.text = team.continent
            continentTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val playedTextView = TextView(requireContext())
            playedTextView.text = team.played.toString()
            playedTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val wonTextView = TextView(requireContext())
            wonTextView.text = team.won.toString()
            wonTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val drawnTextView = TextView(requireContext())
            drawnTextView.text = team.won.toString()
            drawnTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val lostTextView = TextView(requireContext())
            lostTextView.text = team.lost.toString()
            lostTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val pointsTextView = TextView(requireContext())
            pointsTextView.text = team.calculatePoints().toString()
            pointsTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            row.addView(nameTextView)
            row.addView(continentTextView)
            row.addView(playedTextView)
            row.addView(wonTextView)
            row.addView(drawnTextView)
            row.addView(lostTextView)
            row.addView(pointsTextView)

            teamTable.addView(row)
        }
    }

    // Sorting functions
    private fun sortTeamsByName() {
        teams.sortBy { it.teamName }
        displayTeamsInTable()
    }

    private fun sortTeamsByContinent() {
        teams.sortBy { it.continent }
        displayTeamsInTable()
    }

    private fun sortTeamsByPoints() {
        teams.sortByDescending { it.calculatePoints() }
        displayTeamsInTable()
    }
}
