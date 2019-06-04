package com.example.alit.sportsstatistics.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.ui.purchases.PurchaseActivity
import com.example.alit.sportsstatistics.utils.db.tables.Team
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.test.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: MainViewModel

    var adapter: TeamsRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_main)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout_activity_main,
                tb_activity_main, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        toggle.setDrawerIndicatorEnabled(false)
        toggle.setToolbarNavigationClickListener{
            drawer_layout_activity_main.openDrawer(GravityCompat.START)
        }

        drawer_layout_activity_main.addDrawerListener(toggle)
        toggle.syncState()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        rv_activity_main.layoutManager = layoutManager

        viewModel.getAllTeamsRoom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teams ->
                    if (teams.isEmpty()) {
                        rv_activity_main.visibility = View.INVISIBLE
                        tv_activity_main_no_teams.visibility = View.VISIBLE
                    } else {
                        tv_activity_main_no_teams.visibility = View.INVISIBLE
                        rv_activity_main.visibility = View.VISIBLE
                    }
                    prepareAndSetAdapter(teams as ArrayList<Team>)
                }, {
                    Log.d("room", it.message)
                })

        fab_activity_main.setOnClickListener {
            AddTeamDialogFragment.create().show(supportFragmentManager, AddTeamDialogFragment.ADD_TEAM_FRAGMENT_TAG)
        }

        nav_view_activity_main.setNavigationItemSelectedListener(this)
    }

    fun prepareAndSetAdapter(teams: ArrayList<Team>) {
        var index = 3
        while (index < teams.size) {
            val team = Team()
            team.teamID = -1
            teams.add(index, team)
            index += 4
        }
        rv_activity_main.adapter = TeamsRecyclerViewAdapter(teams)
    }

    fun getFollowedTeam(name: String): Team? {
        if (adapter != null) {
            return adapter!!.getTeam(name)
        }
        return null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_main_purchases) {
            startActivity(PurchaseActivity::class.java)
        }
        return true
    }
}
