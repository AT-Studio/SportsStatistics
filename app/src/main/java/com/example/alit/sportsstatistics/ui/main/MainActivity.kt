package com.example.alit.sportsstatistics.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.utils.db.tables.Team
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    lateinit var viewModel: MainViewModel

    var adapter: TeamsRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.getAllTeamsRoom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teams ->
                    for (team in teams) {
                        Log.d("room", team.name)
                    }
                    if (teams.isEmpty()) {
                        rv_activity_main.visibility = View.INVISIBLE
                        tv_activity_main_no_teams.visibility = View.VISIBLE
                    } else {
                        tv_activity_main_no_teams.visibility = View.INVISIBLE
                        rv_activity_main.visibility = View.VISIBLE
                    }
                    if (adapter == null) {
                        initializeRecyclerView(teams)
                    } else {
                        adapter!!.updateTeams(teams)
                    }
                }, {
                    Log.d("room", it.message)
                })

        fab_activity_main.setOnClickListener {
            AddTeamDialogFragment.create().show(supportFragmentManager, AddTeamDialogFragment.ADD_TEAM_FRAGMENT_TAG)
        }
    }

    fun initializeRecyclerView(teams: List<Team>) {
        adapter = TeamsRecyclerViewAdapter(teams as ArrayList<Team>)
        val layoutManager = LinearLayoutManager(this)
        rv_activity_main.layoutManager = layoutManager
        rv_activity_main.adapter = adapter
    }

    fun getFollowedTeam(name: String): Team? {
        if (adapter != null) {
            Log.d("room", "adapter is not null")
            return adapter!!.getTeam(name)
        }
        Log.d("room", "adapter is null")
        return null
    }
}
