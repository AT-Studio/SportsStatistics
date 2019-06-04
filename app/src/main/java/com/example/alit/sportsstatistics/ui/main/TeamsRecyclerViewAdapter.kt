package com.example.alit.sportsstatistics.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.ui.team_detail.TeamDetailActivity
import com.example.alit.sportsstatistics.utils.UIUtils
import com.example.alit.sportsstatistics.utils.db.tables.Team
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_teams_item.view.*

class TeamsRecyclerViewAdapter(var teams: ArrayList<Team>) : RecyclerView.Adapter<TeamsRecyclerViewAdapter.TeamsViewHolder>() {

    lateinit var context: Context
    lateinit var viewModel: MainViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        context = parent.context
        viewModel = ViewModelProviders.of(context as BaseActivity).get(MainViewModel::class.java)
        val itemView = LayoutInflater.from(context).inflate(R.layout.adapter_teams_item, parent, false)
        val holder = TeamsViewHolder(itemView)
        itemView.cv_adapter_teams_follow.setOnClickListener {
            viewModel.deleteTeamRoom(teams[holder.adapterPosition])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        //do nothing
                    }, {
                        Log.d("room", it.message)
                    })
        }
        itemView.setOnClickListener {
            val bundle = Bundle()
            val team = teams[holder.adapterPosition]
            bundle.putString(TeamDetailActivity.TEAM_NAME_EXTRA, team.name)
            bundle.putString(TeamDetailActivity.TEAM_CITY_EXTRA, team.city)
            bundle.putString(TeamDetailActivity.TEAM_ABBREVIATION_EXTRA, team.abbr)
            (context as BaseActivity).startActivity(TeamDetailActivity::class.java, bundle)
        }
        return holder
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        val team = teams[position]
        if (team.teamID == -1) {
            holder.addWrapper.visibility = View.VISIBLE
        } else {
            holder.addWrapper.visibility = View.INVISIBLE
            try {
                val homeLogoRequest = ImageRequestBuilder.newBuilderWithResourceId(UIUtils.getId(team.abbr!!.toLowerCase(), R.drawable::class.java))
                        .setResizeOptions(ResizeOptions.forSquareSize(holder.teamLogo.width))
                        .build()
                holder.teamLogo.controller = Fresco.newDraweeControllerBuilder()
                        .setOldController(holder.teamLogo.controller)
                        .setImageRequest(homeLogoRequest)
                        .build()
            } catch (e: Exception) {
                Log.d("uiutils", "Couldn't find resource")
            }

            holder.teamName.setText(team.name)
            holder.teamCity.setText(team.city)
        }
    }

    override fun getItemCount(): Int {
        return teams.size
    }

    fun updateTeams(teams: List<Team>) {
        this.teams = teams as ArrayList<Team>
        notifyDataSetChanged()
    }

    fun getTeam(name: String): Team? {
        for (team in teams) {
            if (team.name!!.toLowerCase().equals(name)) {
                return team
            }
        }
        return null
    }

    inner class TeamsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamLogo = itemView.iv_adapter_teams_logo
        val teamName = itemView.tv_adapter_teams_name
        val teamCity = itemView.tv_adapter_teams_city

        val addWrapper = itemView.fl_adapter_teams_add_wrapper
    }
}