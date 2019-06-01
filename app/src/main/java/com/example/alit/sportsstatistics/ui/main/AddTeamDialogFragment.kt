package com.example.alit.sportsstatistics.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.app.DialogFragment
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.datastructures.MySportsTeam
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.ui.base.BaseDialogFragment
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main_add_team.*
import kotlinx.android.synthetic.main.fragment_main_add_team.view.*


class AddTeamDialogFragment : BaseDialogFragment() {

    companion object {

        const val ADD_TEAM_FRAGMENT_TAG = "addTeam"

        fun create(): AddTeamDialogFragment {
            return AddTeamDialogFragment()
        }

    }

    lateinit var viewModel: MainViewModel

    lateinit var rootView: View

    var disposable: Disposable? = null;

    var teamContentHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_main_add_team, container, false)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window
        val params = window.attributes

        params.gravity = Gravity.BOTTOM
        window.attributes = params
        window.setBackgroundDrawableResource(R.color.transparent)

        dialog.setCanceledOnTouchOutside(true)

        dialog.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val teamsCache = hashMapOf<String, MySportsTeam>()

        val teams = hashMapOf<String, String>()

        val teamsArr = resources.getStringArray(R.array.teamsList)
        for (team: String in teamsArr) {
            val split = team.split(",")
            teams.put(split[0], split[1])
        }

        rootView.et_fragment_main_add_team_search_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val teamToSearch = rootView.et_fragment_main_add_team_search_text.text.toString().toLowerCase()
                if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
                if (teams.containsKey(teamToSearch)) {
                    val teamAbbr = teams.get(teamToSearch)
                    if (teamsCache.containsKey(teamToSearch)) {
                        val team = teamsCache.get(teamToSearch)
                        rootView.tv_fragment_main_add_team_name.setText(team!!.team!!.name)
                        rootView.tv_fragment_team_add_team_city.setText(
                                team!!.team!!.city + ", " + team.team!!.abbreviation
                        )
                        rootView.iv_fragment_main_add_team_logo.setImageDrawable(
                                ContextCompat.getDrawable(activity, getId(teamAbbr!!.toLowerCase(), R.drawable::class.java))
                        )
                        showTeamContent()
                    } else {
                        //TODO: Show loading animation. Let loading run for at least like ~200ms
                        disposable = viewModel.getTeamStats(SportsStatisticsRepository.SEASON_2018_REGULAR, teamAbbr!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({ teamStandingResponse: TeamStandingResponse? ->
                                    val team = teamStandingResponse!!.teams!![0]
                                    rootView.tv_fragment_main_add_team_name.setText(team.team!!.name)
                                    rootView.tv_fragment_team_add_team_city.setText(
                                            team.team!!.city + ", " + team.team!!.abbreviation
                                    )
                                    rootView.iv_fragment_main_add_team_logo.setImageDrawable(
                                            ContextCompat.getDrawable(activity, getId(teamAbbr.toLowerCase(), R.drawable::class.java))
                                    )
                                    teamsCache.put(teamToSearch, team)
                                    showTeamContent()
                                }, {
                                    Log.d("mysports", it.message)
                                })
                    }
                } else {
                    if (rootView.ll_fragment_main_add_team_team_outer_wrapper.visibility == View.VISIBLE) hideTeamContent()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        rootView.cv_fragment_main_add_team_follow.post {
            cv_fragment_main_add_team_follow.radius = cv_fragment_main_add_team_follow.height.toFloat() / 2
        }

        rootView.ll_fragment_main_add_team_team_outer_wrapper.visibility = View.INVISIBLE

        rootView.ll_fragment_main_add_team_team_outer_wrapper.post {
            teamContentHeight = rootView.ll_fragment_main_add_team_team_outer_wrapper.height

            val wrapperParams = rootView.cl_fragment_main_add_team_team_wrapper.layoutParams as LinearLayout.LayoutParams
            wrapperParams.height = teamContentHeight
            rootView.cl_fragment_main_add_team_team_wrapper.layoutParams = wrapperParams

            val outerWrapperParams = rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams
            outerWrapperParams.height = 0
            rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams = outerWrapperParams

            val holderParams = rootView.v_fragment_main_add_team_height_holder.layoutParams
            holderParams.height = teamContentHeight
            v_fragment_main_add_team_height_holder.layoutParams = holderParams
        }

        rootView.v_fragment_main_add_team_height_holder.setOnClickListener {
            dismissDialog(ADD_TEAM_FRAGMENT_TAG)
            hideKeyboard()
        }

//        val handler = Handler();
//        handler.postDelayed({
//            val params = rootView.et_fragment_main_add_team.layoutParams as FrameLayout.LayoutParams
//            val heightAnim = ValueAnimator.ofInt(params.height, getPixels(100)).setDuration(200)
//            heightAnim.addUpdateListener {
//                params.height = it.animatedValue as Int
//                rootView.et_fragment_main_add_team.layoutParams = params
//            }
//            heightAnim.start()
//        }, 1000)
    }

    fun showTeamContent() {
        val outerWrapperParams = rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams
        val anim = ValueAnimator.ofInt(outerWrapperParams.height, teamContentHeight).setDuration(200)
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            outerWrapperParams.height = it.animatedValue as Int
            rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams = outerWrapperParams
            Log.d("addteam", "animheight: ${outerWrapperParams.height}")
        }
        anim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
                rootView.ll_fragment_main_add_team_team_outer_wrapper.visibility = View.VISIBLE
            }
        })
        anim.start()
    }

    fun hideTeamContent() {
        val outerWrapperParams = rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams
        val anim = ValueAnimator.ofInt(outerWrapperParams.height, 0).setDuration(200)
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            outerWrapperParams.height = it.animatedValue as Int
            rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams = outerWrapperParams
        }
        anim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                rootView.ll_fragment_main_add_team_team_outer_wrapper.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        anim.start()
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
    }
}