package com.example.alit.sportsstatistics.ui.team_detail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.utils.db.tables.TeamGame
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.adapter_team_games_item.view.*

class TeamGamesRecyclerViewAdapter(val teamGames: ArrayList<TeamGame>) : RecyclerView.Adapter<TeamGamesRecyclerViewAdapter.TeamGamesViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamGamesViewHolder {
        Log.d("room", "creating holder")
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.adapter_team_games_item, parent, false)
        return TeamGamesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeamGamesViewHolder, position: Int) {

        Log.d("room", "binding holder")

        val game = teamGames[position]

        Log.d("adapter", "width: ${(context as BaseActivity).getDp(holder.homeLogo.width)}")
        val homeLogoRequest = ImageRequestBuilder.newBuilderWithResourceId(getId(game.homeTeamAbbr!!.toLowerCase(), R.drawable::class.java))
                .setResizeOptions(ResizeOptions.forSquareSize(holder.homeLogo.width))
                .build()
        holder.homeLogo.controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.homeLogo.controller)
                .setImageRequest(homeLogoRequest)
                .build()

        val awayLogoRequest = ImageRequestBuilder.newBuilderWithResourceId(getId(game.awayTeamAbbr!!.toLowerCase(), R.drawable::class.java))
                .setResizeOptions(ResizeOptions.forSquareSize(holder.awayLogo.width))
                .build()
        holder.awayLogo.controller = Fresco.newDraweeControllerBuilder()
                .setOldController(holder.awayLogo.controller)
                .setImageRequest(awayLogoRequest)
                .build()

        holder.homeScore.setText(game.homeScore.toString())
        holder.awayScore.setText(game.awayScore.toString())

        holder.gameDate.setText(game.startTime)
    }

    override fun getItemCount(): Int {
        return teamGames.size
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

    inner class TeamGamesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val homeLogo = itemView.iv_adapter_team_games_home_logo
        val homeScore = itemView.tv_adapter_team_games_home_score_total

        val awayLogo = itemView.iv_adapter_team_games_away_logo
        val awayScore = itemView.tv_adapter_team_games_away_score_total

        val gameDate = itemView.tv_adapter_team_games_date

//        val homeAbbr = itemView.tv_adapter_team_games_home_abbr
//        val homeQ1 = itemView.tv_adapter_team_games_home_q1
//        val homeQ2 = itemView.tv_adapter_team_games_home_q2
//        val homeQ3 = itemView.tv_adapter_team_games_home_q3
//        val homeQ4 = itemView.tv_adapter_team_games_home_q4
//        val homeQ5 = itemView.tv_adapter_team_games_home_q5
//        val homeQTot = itemView.tv_adapter_team_games_home_qTot
//
//        val awayAbbr = itemView.tv_adapter_team_games_away_abbr
//        val awayQ1 = itemView.tv_adapter_team_games_away_q1
//        val awayQ2 = itemView.tv_adapter_team_games_away_q2
//        val awayQ3 = itemView.tv_adapter_team_games_away_q3
//        val awayQ4 = itemView.tv_adapter_team_games_away_q4
//        val awayQ5 = itemView.tv_adapter_team_games_away_q5
//        val awayQTot = itemView.tv_adapter_team_games_away_qTot
//
//        val scoreTable = itemView.tl_adapter_team_games
//        val tableDivider = itemView.v_adapter_team_games_table_divider
    }

}