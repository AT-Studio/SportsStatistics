package com.example.alit.sportsstatistics.ui.team_detail

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseFragment
import com.example.alit.sportsstatistics.utils.db.tables.TeamGame
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_team_detail_game_history.*
import kotlinx.android.synthetic.main.fragment_team_detail_game_history.view.*

class TeamGameHistoryFragment : BaseFragment() {

    companion object {

        const val TEAM_ABBR_EXTRA = "teamAbbr"
        const val SEASON_EXTRA = "season"

        fun create(teamAbbr: String, season: String): TeamGameHistoryFragment {
            val bundle = Bundle()
            bundle.putString(TEAM_ABBR_EXTRA, teamAbbr)
            bundle.putString(SEASON_EXTRA, season)
            val fragment =  TeamGameHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }

    }

    lateinit var teamAbbr: String
    lateinit var season: String

    lateinit var rootView: View

    lateinit var viewModel: TeamDetailViewModel

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        teamAbbr = bundle!!.getString(TEAM_ABBR_EXTRA)
        season = bundle.getString(SEASON_EXTRA)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_team_detail_game_history, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("gamehistory", "created frag")

        rootView.rv_fragment_team_detail_game_history.layoutManager = LinearLayoutManager(activity)

        viewModel = ViewModelProviders.of(this).get(TeamDetailViewModel::class.java)

        disposable = viewModel.getTeamGamesRoom(teamAbbr, season)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teamGames ->
                    Log.d("room", "on Success, games in room: ${teamGames.size}")
                    if (teamGames.isEmpty()) {
                        disposable = viewModel.getTeamGames(season, teamAbbr)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({ teamGamesResponse ->
                                    if (teamGamesResponse.games.isNullOrEmpty()) {
                                        Log.d("room", "there were no games")
                                        rv_fragment_team_detail_game_history.visibility = View.INVISIBLE
                                        tv_fragment_team_detail_game_history_no_games.visibility = View.VISIBLE
                                    } else {
                                        Log.d("room", "there were games: ${teamGamesResponse.games!!.size}")
                                        tv_fragment_team_detail_game_history_no_games.visibility = View.INVISIBLE
                                        rv_fragment_team_detail_game_history.visibility = View.VISIBLE
                                        val teamGamesRoom = ArrayList<TeamGame>()
                                        for (game in teamGamesResponse.games!!) {
                                            val teamGameRoom = TeamGame()
                                            teamGameRoom.id = game.schedule!!.id
                                            Log.d("room", teamGameRoom.id.toString())
                                            teamGameRoom.startTime = getDateFromStartTime(game.schedule!!.startTime!!)
                                            Log.d("room", teamGameRoom.startTime)
                                            teamGameRoom.awayTeamAbbr = game.schedule!!.awayTeam!!.abbreviation
                                            Log.d("room", teamGameRoom.awayTeamAbbr)
                                            teamGameRoom.homeTeamAbbr = game.schedule!!.homeTeam!!.abbreviation
                                            Log.d("room", teamGameRoom.homeTeamAbbr)
                                            teamGameRoom.awayScore = game.score!!.awayScoreTotal
                                            Log.d("room", teamGameRoom.awayScore.toString())
                                            teamGameRoom.homeScore = game.score!!.homeScoreTotal
                                            Log.d("room", teamGameRoom.homeScore.toString())
                                            teamGameRoom.teamAbbr = teamAbbr
                                            teamGameRoom.season = season
                                            teamGamesRoom.add(teamGameRoom)
                                        }
                                        Log.d("room", "setting adapter")
                                        rootView.rv_fragment_team_detail_game_history.adapter =
                                                TeamGamesRecyclerViewAdapter(teamGamesRoom)
                                        Log.d("room", "count ${rootView.rv_fragment_team_detail_game_history.adapter.itemCount}")
                                        Log.d("room", "storing in room")
                                        disposable = viewModel.insertAllTeamGamesRoom(teamGamesRoom)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe ({
                                                    Log.d("room", "finished inserting game")
                                                }, {
                                                    Log.d("room", it.message)
                                                })
                                    }
                                }, {
                                    Log.d("mySports", it.message)
                                })
                    } else {
                        tv_fragment_team_detail_game_history_no_games.visibility = View.INVISIBLE
                        rv_fragment_team_detail_game_history.visibility = View.VISIBLE
                        rootView.rv_fragment_team_detail_game_history.adapter = TeamGamesRecyclerViewAdapter(teamGames as ArrayList<TeamGame>)
                    }
                }, {
                    Log.d("room", it.message)
                }, {
                    Log.d("room", "on Complete, getting games from INERNET")
                })
    }

    fun getDateFromStartTime(startTime: String): String {
        val year = startTime.substring(0, 4)
        val month = startTime.substring(5, 7)
        val day = startTime.subSequence(8, 10)
        return month + "/" + day
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
    }
}