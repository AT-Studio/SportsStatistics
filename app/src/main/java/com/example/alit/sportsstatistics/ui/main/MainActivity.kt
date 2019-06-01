package com.example.alit.sportsstatistics.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        fab_activity_main.setOnClickListener {
            AddTeamDialogFragment.create().show(supportFragmentManager, AddTeamDialogFragment.ADD_TEAM_FRAGMENT_TAG)

//            viewModel.getTeamStats("NE").subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe ({
////                        tv_activity_main_no_teams.setText(it.team!!.name)
//                        tv_activity_main_no_teams.setText(it!!.teams!![0].team!!.name)
//                    }, {
//                        Log.d("mysports", it.message)
//                    })
        }
    }
}
