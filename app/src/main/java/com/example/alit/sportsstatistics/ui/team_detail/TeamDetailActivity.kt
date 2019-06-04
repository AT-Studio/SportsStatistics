package com.example.alit.sportsstatistics.ui.team_detail

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import com.example.alit.sportsstatistics.utils.UIUtils.Companion.getId
import com.example.alit.sportsstatistics.utils.db.tables.TeamStandings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_team_detail.*

class TeamDetailActivity : BaseActivity() {

    companion object {

        const val TEAM_NAME_EXTRA = "name"
        const val TEAM_CITY_EXTRA = "city"
        const val TEAM_ABBREVIATION_EXTRA = "team"

        const val STAT_UNAVAILABLE = "UA"

    }

    lateinit var viewModel: TeamDetailViewModel

    lateinit var disposables: ArrayList<Disposable>

    lateinit var teamName: String
    lateinit var teamCity: String
    lateinit var teamAbbr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)

        val bundle = intent.extras
        if (bundle == null || bundle.isEmpty) finish()

        teamName = bundle.getString(TEAM_NAME_EXTRA)
        teamCity = bundle.getString(TEAM_CITY_EXTRA)
        teamAbbr = bundle.getString(TEAM_ABBREVIATION_EXTRA)

        try {
            iv_activity_team_detail_logo.setImageDrawable(
                    ContextCompat.getDrawable(this, getId(teamAbbr.toLowerCase(), R.drawable::class.java))
            )
        } catch (e: Exception) {
            Log.d("uiutils", "Couldn't find resource")
        }
        tv_activity_team_detail_name.setText(teamName)
        tv_activity_team_detail_title.setText(teamName)
        tv_activity_team_detail_city.setText(teamCity)

        viewModel = ViewModelProviders.of(this).get(TeamDetailViewModel::class.java)

        disposables = ArrayList<Disposable>()
        tv_activity_team_detail_season.setText(SportsStatisticsRepository.SEASON_LATEST)
        getTeamStandingsForSeason(SportsStatisticsRepository.SEASON_LATEST)

        var teamNameY = 0

        val dashParams = cl_activity_team_detail_dashboard.layoutParams as CollapsingToolbarLayout.LayoutParams
        dashParams.topMargin = getPixels(56) + getStatusBarHeight()
        cl_activity_team_detail_dashboard.layoutParams = dashParams

        appbar_activity_team_detail.post {
            val appbarParams = appbar_activity_team_detail.layoutParams as CoordinatorLayout.LayoutParams
            appbarParams.height = cl_activity_team_detail_dashboard.height + getPixels(56) + getStatusBarHeight()
            appbar_activity_team_detail.layoutParams = appbarParams

            teamNameY = tv_activity_team_detail_name.y.toInt() + tv_activity_team_detail_name.height
        }

        cl_activity_team_detail_dashboard.post {
            val rankParams = tv_activity_team_detail_rank.layoutParams as ConstraintLayout.LayoutParams
            rankParams.width = tv_activity_team_detail_rank_widther.width
            tv_activity_team_detail_rank.layoutParams = rankParams
        }

        appbar_activity_team_detail.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) == appbar_activity_team_detail.totalScrollRange) {
                rl_activity_team_detail_tb_inner.elevation = 0f
                v_activity_team_detail_divider.visibility = View.INVISIBLE
                ViewCompat.setElevation(appbar_activity_team_detail, resources.getDimension(R.dimen.toolbar_elevation))
            } else if (verticalOffset == 0) {
                rl_activity_team_detail_tb_inner.elevation = 0f
                ViewCompat.setElevation(appbar_activity_team_detail, 0f)
                v_activity_team_detail_divider.visibility = View.VISIBLE
            } else {
                rl_activity_team_detail_tb_inner.elevation = resources.getDimension(R.dimen.toolbar_elevation)
                ViewCompat.setElevation(appbar_activity_team_detail, 0f)
                v_activity_team_detail_divider.visibility = View.VISIBLE
            }
            if (Math.abs(verticalOffset) > teamNameY) {
                tv_activity_team_detail_title.visibility = View.VISIBLE
            } else {
                tv_activity_team_detail_title.visibility = View.INVISIBLE
            }
        }

        val handler = Handler()
        handler.postDelayed({
            vp_activity_team_detail.adapter = TeamGameHistoryStatePagerAdapter(supportFragmentManager)
            vp_activity_team_detail.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    tv_activity_team_detail_season.setText((vp_activity_team_detail.adapter as TeamGameHistoryStatePagerAdapter).getPageTitle(position))
                    getTeamStandingsForSeason((vp_activity_team_detail.adapter as TeamGameHistoryStatePagerAdapter).seasons[position])
                    if (position == 0) {
                        iv_activity_team_detail_arrow_left.visibility = View.INVISIBLE
                    } else {
                        iv_activity_team_detail_arrow_left.visibility = View.VISIBLE
                    }
                    if (position == (vp_activity_team_detail.adapter as TeamGameHistoryStatePagerAdapter).count - 1) {
                        iv_activity_team_detail_arrow_right.visibility = View.INVISIBLE
                    } else {
                        iv_activity_team_detail_arrow_right.visibility = View.VISIBLE
                    }
                }
            })
        }, 200)

        iv_activity_team_detail_back.setOnClickListener {
            finish()
        }

        iv_activity_team_detail_arrow_left.visibility = View.INVISIBLE
        iv_activity_team_detail_arrow_left.setOnClickListener {
            if (vp_activity_team_detail.currentItem > 0) vp_activity_team_detail.arrowScroll(ViewPager.FOCUS_LEFT)
        }

        iv_activity_team_detail_arrow_right.setOnClickListener {
            if (vp_activity_team_detail.currentItem < (vp_activity_team_detail.adapter as TeamGameHistoryStatePagerAdapter).count - 1) {
                vp_activity_team_detail.arrowScroll(ViewPager.FOCUS_RIGHT)
            }
        }

        cv_activity_team_detail_follow.setOnClickListener {
            val disposable = viewModel.deleteTeamRoom(teamAbbr)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        finish()
                    }, {
                        Log.d("room", it.message)
                    })
            disposables.add(disposable)
        }
    }

    fun getTeamStandingsForSeason(season: String) {
        val disposable = viewModel.getTeamStandingRoom(teamAbbr, season)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teamStanding ->
                    tv_activity_team_detail_name.setText(teamName)
                    tv_activity_team_detail_title.setText(teamName)
                    tv_activity_team_detail_city.setText(teamCity)
                    if (teamStanding.wins!! == -1) {
                        setTeamStandingsAvailable(false)
                    }
                    else {
                        setTeamStandingsAvailable(true)
                        tv_activity_team_detail_wins.setText(teamStanding.wins!!.toString())
                        tv_activity_team_detail_losses.setText(teamStanding.losses!!.toString())
                        tv_activity_team_detail_ties.setText(teamStanding.ties!!.toString())
                        tv_activity_team_detail_rank.setText(teamStanding.rank!!.toString())
                    }
                }, {
                    Log.d("room", it.message)
                }, {
                    val disposable = viewModel.getTeamStats(season, teamAbbr)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({ teamStanding ->
                                if (teamStanding.teams.isNullOrEmpty()) {
                                    setTeamStandingsAvailable(false)
                                    val teamStandingRoom = TeamStandings()
                                    teamStandingRoom.teamAbbr = teamAbbr
                                    teamStandingRoom.season = season
                                    teamStandingRoom.gamesPlayed = -1
                                    teamStandingRoom.wins = -1
                                    teamStandingRoom.losses = -1
                                    teamStandingRoom.ties = -1
                                    teamStandingRoom.rank = -1
                                    val disposable = viewModel.insertTeamStandingRoom(teamStandingRoom)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe ({
                                            }, {
                                                Log.d("room", it.message)
                                            })
                                    disposables.add(disposable)
                                } else {
                                    setTeamStandingsAvailable(true)
                                    val team = teamStanding.teams[0]
                                    tv_activity_team_detail_wins.setText(team.stats!!.standings!!.wins!!.toString())
                                    tv_activity_team_detail_losses.setText(team.stats.standings!!.losses!!.toString())
                                    tv_activity_team_detail_ties.setText(team.stats.standings.ties!!.toString())
                                    tv_activity_team_detail_rank.setText(team.overallRank!!.rank!!.toString())
                                    val teamStandingRoom = TeamStandings()
                                    teamStandingRoom.teamAbbr = teamAbbr
                                    teamStandingRoom.season = season
                                    teamStandingRoom.gamesPlayed = team.stats.gamesPlayed
                                    teamStandingRoom.wins = team.stats.standings.wins
                                    teamStandingRoom.losses = team.stats.standings.losses
                                    teamStandingRoom.ties = team.stats.standings.ties
                                    teamStandingRoom.rank = team.overallRank.rank
                                    val disposable = viewModel.insertTeamStandingRoom(teamStandingRoom)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe ({
                                            }, {
                                                Log.d("room", it.message)
                                            })
                                    disposables.add(disposable)
                                }
                            }, {
                                Log.d("mySports", it.message)
                                showToast("Please check internet connection")
                                setTeamStandingsAvailable(false)
                            })
                    disposables.add(disposable)
                })
        disposables.add(disposable)
    }

    fun updateTeamStandingsForSeason(season: String) {
        val disposable = viewModel.getTeamStats(season, teamAbbr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teamStanding ->
                    if (teamStanding.teams.isNullOrEmpty()) {
                        setTeamStandingsAvailable(false)
                        val teamStandingRoom = TeamStandings()
                        teamStandingRoom.teamAbbr = teamAbbr
                        teamStandingRoom.season = season
                        teamStandingRoom.gamesPlayed = -1
                        teamStandingRoom.wins = -1
                        teamStandingRoom.losses = -1
                        teamStandingRoom.ties = -1
                        teamStandingRoom.rank = -1
                        val disposable = viewModel.insertTeamStandingRoom(teamStandingRoom)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({
                                }, {
                                    Log.d("room", it.message)
                                })
                        disposables.add(disposable)
                    } else {
                        setTeamStandingsAvailable(true)
                        val team = teamStanding.teams[0]
                        tv_activity_team_detail_wins.setText(team.stats!!.standings!!.wins!!.toString())
                        tv_activity_team_detail_losses.setText(team.stats.standings!!.losses!!.toString())
                        tv_activity_team_detail_ties.setText(team.stats.standings.ties!!.toString())
                        tv_activity_team_detail_rank.setText(team.overallRank!!.rank!!.toString())
                        val teamStandingRoom = TeamStandings()
                        teamStandingRoom.teamAbbr = teamAbbr
                        teamStandingRoom.season = season
                        teamStandingRoom.gamesPlayed = team.stats.gamesPlayed
                        teamStandingRoom.wins = team.stats.standings.wins
                        teamStandingRoom.losses = team.stats.standings.losses
                        teamStandingRoom.ties = team.stats.standings.ties
                        teamStandingRoom.rank = team.overallRank.rank
                        val disposable = viewModel.insertTeamStandingRoom(teamStandingRoom)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({
                                }, {
                                    Log.d("room", it.message)
                                })
                        disposables.add(disposable)
                    }
                }, {
                    Log.d("mySports", it.message)
                    showToast("Please check internet connection")
                    setTeamStandingsAvailable(false)
                })
        disposables.add(disposable)
    }

    fun setTeamStandingsAvailable(available: Boolean) {
        if (available) {
            tv_activity_team_detail_wins.setTextColor(ContextCompat.getColor(this, R.color.primary_text))
            tv_activity_team_detail_losses.setTextColor(ContextCompat.getColor(this, R.color.primary_text))
            tv_activity_team_detail_ties.setTextColor(ContextCompat.getColor(this, R.color.primary_text))
            tv_activity_team_detail_rank.setTextColor(ContextCompat.getColor(this, R.color.primary_text))
        } else {
            tv_activity_team_detail_wins.setText(STAT_UNAVAILABLE)
            tv_activity_team_detail_losses.setText(STAT_UNAVAILABLE)
            tv_activity_team_detail_ties.setText(STAT_UNAVAILABLE)
            tv_activity_team_detail_rank.setText(STAT_UNAVAILABLE)
            tv_activity_team_detail_wins.setTextColor(ContextCompat.getColor(this, R.color.tertiary_text))
            tv_activity_team_detail_losses.setTextColor(ContextCompat.getColor(this, R.color.tertiary_text))
            tv_activity_team_detail_ties.setTextColor(ContextCompat.getColor(this, R.color.tertiary_text))
            tv_activity_team_detail_rank.setTextColor(ContextCompat.getColor(this, R.color.tertiary_text))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (disposable in disposables) {
            if (!disposable.isDisposed) disposable.dispose()
        }
    }

    inner class TeamGameHistoryStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        val seasons = arrayOf(SportsStatisticsRepository.SEASON_LATEST, SportsStatisticsRepository.SEASON_2018_REGULAR,
                SportsStatisticsRepository.SEASON_2018_PLAYOFFS, SportsStatisticsRepository.SEASON_2017_REGULAR,
                SportsStatisticsRepository.SEASON_2017_PLAYOFFS, SportsStatisticsRepository.SEASON_2016_REGULAR,
                SportsStatisticsRepository.SEASON_2016_PLAYOFFS, SportsStatisticsRepository.SEASON_2015_REGULAR,
                SportsStatisticsRepository.SEASON_2015_PLAYOFFS)

        override fun getItem(position: Int): Fragment {
            return TeamGameHistoryFragment.create(teamAbbr, seasons[position])
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return seasons[position]
        }

        override fun getCount(): Int {
            return seasons.size
        }

    }
}
