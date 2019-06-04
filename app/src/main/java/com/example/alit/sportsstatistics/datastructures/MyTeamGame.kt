package com.example.alit.sportsstatistics.datastructures

open class MyTeamGame {

    var schedule: Schedule? = null
    var score: Score? = null

    inner class Schedule {
        var id: Int? = null
        var startTime: String? = null
        var awayTeam: AwayTeam? = null
        var homeTeam: HomeTeam? = null

        inner class AwayTeam {
            var abbreviation: String? = null
        }

        inner class HomeTeam {
            var abbreviation: String? = null
        }
    }

    inner class Score {
        var awayScoreTotal: Int? = null
        var homeScoreTotal: Int? = null
    }

}