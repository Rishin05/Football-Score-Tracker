package project.stn991662556.fifaworldcup2022

import com.google.firebase.firestore.DocumentSnapshot

data class Team(
    val teamName: String = "",
    val continent: String = "",
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0
) {
    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Team {
            val data = snapshot.data ?: return Team()
            return Team(
                teamName = data["TeamName"] as? String ?: "",
                continent = data["Continent"] as? String ?: "",
                played = (data["Played"] as? Long)?.toInt() ?: 0,
                won = (data["Won"] as? Long)?.toInt() ?: 0,
                drawn = (data["Drawn"] as? Long)?.toInt() ?: 0,
                lost = (data["Lost"] as? Long)?.toInt() ?: 0
            )
        }
    }

    fun calculatePoints(): Int {
        return (won * 3) + drawn
    }
}
