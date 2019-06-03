package com.example.alit.sportsstatistics.ui.team_detail

import android.os.Bundle
import com.example.alit.sportsstatistics.ui.base.BaseFragment

class TeamGameHistoryFragment : BaseFragment() {

    companion object {

        const val SEASON_EXTRA = "season"

        fun create(season: String): TeamGameHistoryFragment {
            val bundle = Bundle()
            bundle.putString(SEASON_EXTRA, season)
            val fragment =  TeamGameHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }

    }

}