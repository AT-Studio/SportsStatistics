package com.example.alit.sportsstatistics.datastructures

open class MySportsTeam {

    val team: Team? = null
    val stats: Stats? = null
    val overallRank: OverallRank? = null

    inner class Team {
        val id: Int? = null
        val city: String? = null
        val name: String? = null
        val abbreviation: String? = null
    }

    inner class Stats {
        val gamesPlayed: Int? = null
        val standings: Standings? = null

        inner class Standings {
            val wins: Int? = null
            val losses: Int? = null
            val ties: Int? = null
        }
    }

    inner class OverallRank {
        val rank: Int? = null
    }
}