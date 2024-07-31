package project.stn991662556.fifaworldcup2022

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import project.stn991662556.fifaworldcup2022.databinding.FragmentEditTeamDisplayBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditTeamDisplayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTeamDisplayFragment : Fragment() {
    private lateinit var binding: FragmentEditTeamDisplayBinding
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val continents = arrayOf(
        "Africa", "Antarctica", "Asia", "Europe",
        "North America", "Oceania", "South America"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTeamDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val continentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, continents)
        continentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editContinentDisplay.adapter = continentAdapter

        val teamName = arguments?.getString("TeamName")
        val continent = arguments?.getString("Continent")
        val games = arguments?.getLong("Played", 0)
        val won = arguments?.getLong("Won", 0)
        val drawn = arguments?.getLong("Drawn", 0)
        val lost = arguments?.getLong("Lost", 0)
        val documentId = arguments?.getString("DocumentId")

        binding.editNameDisplay.text = teamName?.toEditable()
        binding.editContinentDisplay.setSelection(getContinentPosition(continent))
        binding.editGamesDisplay.text = games.toString().toEditable()
        binding.editWonDisplay.text = won.toString().toEditable()
        binding.editDrawnDisplay.text = drawn.toString().toEditable()
        binding.editLostDisplay.text = lost.toString().toEditable()

        binding.editSave.setOnClickListener {
            if (documentId.isNullOrEmpty()) {
                Log.e("EditTeamDisplayFragment", "Document ID is null or empty")
                return@setOnClickListener
            }
            saveChanges(
                documentId,
                binding.editNameDisplay.text.toString(),
                continents[binding.editContinentDisplay.selectedItemPosition],
                binding.editGamesDisplay.text.toString().toInt(),
                binding.editWonDisplay.text.toString().toInt(),
                binding.editDrawnDisplay.text.toString().toInt(),
                binding.editLostDisplay.text.toString().toInt()
            )
        }
    }

    private fun getContinentPosition(continent: String?): Int {
        return if (continent != null) {
            continents.indexOf(continent)
        } else {
            // Default position if continent is null
            0
        }
    }

    private fun saveChanges(
        documentId: String?,
        teamName: String?,
        continent: String,
        games: Int,
        won: Int,
        drawn: Int,
        lost: Int
    ) {
        if (documentId.isNullOrEmpty()) {
            Log.e("EditTeamDisplayFragment", "Document ID is null or empty")
            return
        }

        val teamRef = firestoreDB.collection("Teams").document(documentId)
        val data = hashMapOf(
            "TeamName" to teamName,
            "Continent" to continent,
            "Played" to games,
            "Won" to won,
            "Drawn" to drawn,
            "Lost" to lost
        )

        teamRef.update(data as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent().apply {
                    putExtra("dataUpdated", true)
                }
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, resultIntent)
                // No need to finish the activity here
                (requireActivity() as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(project.stn991662556.fifaworldcup2022.R.id.fragmentContainerView,  EditTeamFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .addOnFailureListener { e ->
                Log.e("EditTeamDisplayFragment", "Error updating data", e)
                Toast.makeText(requireContext(), "Failed to update data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
