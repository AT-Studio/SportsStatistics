package com.example.alit.sportsstatistics.ui.team_detail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.utils.UIUtils.Companion.getId
import com.example.alit.sportsstatistics.utils.db.tables.TeamGame
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.adapter_team_games_item.view.*

class TeamGamesRecyclerViewAdapter(val teamGames: ArrayList<TeamGame>) : RecyclerView.Adapter<TeamGamesRecyclerViewAdapter.TeamGamesViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamGamesViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.adapter_team_games_item, parent, false)
        return TeamGamesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamGamesViewHolder, position: Int) {
        val game = teamGames[position]
        try {
            val homeLogoRequest = ImageRequestBuilder.newBuilderWithResourceId(getId(game.homeTeamAbbr!!.toLowerCase(), R.drawable::class.java))
                    .setResizeOptions(ResizeOptions.forSquareSize(holder.homeLogo.width))
                    .build()
            holder.homeLogo.controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.homeLogo.controller)
                    .setImageRequest(homeLogoRequest)
                    .build()
        } catch (e: Exception) {
            Log.d("uiutils", "Couldn't find resource")
        }
        try {
            val awayLogoRequest = ImageRequestBuilder.newBuilderWithResourceId(getId(game.awayTeamAbbr!!.toLowerCase(), R.drawable::class.java))
                    .setResizeOptions(ResizeOptions.forSquareSize(holder.awayLogo.width))
                    .build()
            holder.awayLogo.controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.awayLogo.controller)
                    .setImageRequest(awayLogoRequest)
                    .build()
        } catch (e: Exception) {
            Log.d("uiutils", "Couldn't find resource")
        }

        holder.homeScore.setText(game.homeScore.toString())
        holder.awayScore.setText(game.awayScore.toString())

        holder.homeAbbr.setText(game.homeTeamAbbr)
        holder.awayAbbr.setText(game.awayTeamAbbr)

        holder.gameDate.setText(game.startTime)
    }

    override fun getItemCount(): Int {
        return teamGames.size
    }

    inner class TeamGamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val homeLogo = itemView.iv_adapter_team_games_home_logo
        val homeAbbr = itemView.tv_adapter_team_games_home_abbr
        val homeScore = itemView.tv_adapter_team_games_home_score_total

        val awayLogo = itemView.iv_adapter_team_games_away_logo
        val awayAbbr = itemView.tv_adapter_team_games_away_abbr
        val awayScore = itemView.tv_adapter_team_games_away_score_total

        val gameDate = itemView.tv_adapter_team_games_date
    }

}