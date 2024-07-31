package project.stn991662556.fifaworldcup2022

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddTeamFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTeamFragment : Fragment() {

    private lateinit var firestoreDB: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_team, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()

        // Populate the continent spinner
        val continents = listOf(
            "Africa", "Antarctica", "Asia",
            "Europe", "North America", "Oceania", "South America"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, continents)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val addContinent = view.findViewById<Spinner>(R.id.addContinent)
        addContinent.adapter = adapter

        // Save button click listener
        val addButton = view.findViewById<Button>(R.id.add)
        addButton.setOnClickListener {
            saveTeam()
        }
    }

    private fun saveTeam() {
        val teamName = view?.findViewById<EditText>(R.id.addName)?.text.toString()
        val continent = view?.findViewById<Spinner>(R.id.addContinent)?.selectedItem.toString()
        val numPlayedGames = view?.findViewById<EditText>(R.id.addGames)?.text.toString().toInt()
        val numWonGames = view?.findViewById<EditText>(R.id.addWon)?.text.toString().toInt()
        val numDrawnGames = view?.findViewById<EditText>(R.id.addDrawn)?.text.toString().toInt()
        val numLostGames = view?.findViewById<EditText>(R.id.addLost)?.text.toString().toInt()

        // Create a map to represent the team data
        val teamData = hashMapOf(
            "Continent" to continent,
            "Drawn" to numDrawnGames,
            "Lost" to numLostGames,
            "Played" to numPlayedGames,
            "Won" to numWonGames,
            "TeamName" to teamName
        )

        firestoreDB.collection("Teams")
            .add(teamData) // add record to DB collection
            .addOnSuccessListener {
                // Handle success
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

        // Clear input fields
        view?.findViewById<EditText>(R.id.addName)?.text?.clear()
        view?.findViewById<EditText>(R.id.addGames)?.text?.clear()
        view?.findViewById<EditText>(R.id.addWon)?.text?.clear()
        view?.findViewById<EditText>(R.id.addDrawn)?.text?.clear()
        view?.findViewById<EditText>(R.id.addLost)?.text?.clear()
    }
}
