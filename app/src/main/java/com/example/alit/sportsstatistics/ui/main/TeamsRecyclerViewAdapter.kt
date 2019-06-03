package com.example.alit.sportsstatistics.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.ui.team_detail.TeamDetailActivity
import com.example.alit.sportsstatistics.utils.db.tables.Team
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
        holder.teamLogo.setImageDrawable(
                ContextCompat.getDrawable(context, getId(team.abbr!!.toLowerCase(), R.drawable::class.java))
        )
        holder.teamName.setText(team.name)
        holder.teamCity.setText(team.city)
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
                Log.d("room", "found team with name: $name")
                return team
            }
        }
        Log.d("room", "did not find team with name: $name")
        return null
    }

    fun getId(resourceName: String, c: Class<*>): Int {
        try {
            val idField = c.getDeclaredField(resourceName)
            return idField.getInt(idField)
        } catch (e: Exception) {
            throw RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e)
        }
    }

    inner class TeamsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val teamLogo = itemView.iv_adapter_teams_logo
        val teamName = itemView.tv_adapter_teams_name
        val teamCity = itemView.tv_adapter_teams_city
    }
}