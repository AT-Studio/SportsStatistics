package com.example.alit.sportsstatistics.ui.team_detail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R

class TeamGamesRecyclerViewAdapter : RecyclerView.Adapter<TeamGamesRecyclerViewAdapter.TeamGamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamGamesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_team_games_item, parent, false)
        return TeamGamesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamGamesViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 0
    }

    inner class TeamGamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}