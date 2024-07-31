package project.stn991662556.fifaworldcup2022

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(private val teams: List<Team>) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.team_item, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount(): Int = teams.size

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val teamNameTextView: TextView = itemView.findViewById(R.id.teamItemNameTextView)
        private val continentTextView: TextView = itemView.findViewById(R.id.continentItemTextView)
        private val playedTextView: TextView = itemView.findViewById(R.id.playedTextView)
        private val wonTextView: TextView = itemView.findViewById(R.id.wonTextView)
        private val drawnTextView: TextView = itemView.findViewById(R.id.drawnTextView)
        private val lostTextView: TextView = itemView.findViewById(R.id.lostTextView)
        private val pointsTextView: TextView = itemView.findViewById(R.id.pointsTextView)

        fun bind(team: Team) {
            teamNameTextView.text = team.teamName
            continentTextView.text = team.continent
            playedTextView.text = team.played.toString()
            wonTextView.text = team.won.toString()
            drawnTextView.text = team.drawn.toString()
            lostTextView.text = team.lost.toString()
            pointsTextView.text = team.calculatePoints().toString()
        }
    }
}