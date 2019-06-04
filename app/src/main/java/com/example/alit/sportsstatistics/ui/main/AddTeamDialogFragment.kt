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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.datastructures.MySportsTeam
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.ui.base.BaseDialogFragment
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import com.example.alit.sportsstatistics.utils.UIUtils
import com.example.alit.sportsstatistics.utils.db.tables.Team
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

    var contentShown: Boolean = false

    var loadingAnim: ValueAnimator? = null

    var currentTeam: String? = null

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
                val teamToSearch = rootView.et_fragment_main_add_team_search_text.text.toString().toLowerCase().trim()
                if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
                if (loadingAnim != null && loadingAnim!!.isRunning) loadingAnim!!.cancel()
                if (teams.containsKey(teamToSearch)) {
                    val teamAbbr = teams.get(teamToSearch)
                    currentTeam = teamToSearch
                    if (teamsCache.containsKey(teamToSearch)) {
                        if ((activity as MainActivity).getFollowedTeam(teamToSearch) != null) {
                            setFollowed(true)
                        } else {
                            setFollowed(false)
                        }
                        val team = teamsCache.get(teamToSearch)
                        rootView.tv_fragment_main_add_team_name.setText(team!!.team!!.name)
                        rootView.tv_fragment_team_add_team_city.setText(team.team!!.city)
                        try {
                            rootView.iv_fragment_main_add_team_logo.setImageDrawable(
                                    ContextCompat.getDrawable(activity, UIUtils.getId(teamAbbr!!.toLowerCase(), R.drawable::class.java))
                            )
                        } catch (e: Exception) {
                            Log.d("uiutils", "Couldn't find resource")
                        }
                        if (!contentShown) showTeamContent()
                    } else {
                        //TODO: Show loading animation. Let loading run for at least like ~200ms
                        animateCicle()
                        disposable = viewModel.getTeamStats(SportsStatisticsRepository.SEASON_2018_REGULAR, teamAbbr!!)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe ({ teamStandingResponse: TeamStandingResponse? ->
                                    if ((activity as MainActivity).getFollowedTeam(teamToSearch) != null) {
                                        setFollowed(true)
                                    } else {
                                        setFollowed(false)
                                    }
                                    val team = teamStandingResponse!!.teams!![0]
                                    rootView.tv_fragment_main_add_team_name.setText(team.team!!.name)
                                    rootView.tv_fragment_team_add_team_city.setText(team.team.city)
                                    try {
                                        rootView.iv_fragment_main_add_team_logo.setImageDrawable(
                                                ContextCompat.getDrawable(activity, UIUtils.getId(teamAbbr!!.toLowerCase(), R.drawable::class.java))
                                        )
                                    } catch (e: Exception) {
                                        Log.d("uiutils", "Couldn't find resource")
                                    }
                                    teamsCache.put(teamToSearch, team)
                                    if (!contentShown) showTeamContent()
                                    if (loadingAnim != null && loadingAnim!!.isRunning) loadingAnim!!.cancel()
                                }, {
                                    Log.d("mysports", it.message)
                                    if (loadingAnim != null && loadingAnim!!.isRunning) loadingAnim!!.cancel()
                                    showToast("Please check internet connection")
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

        rootView.cv_fragment_main_add_team_follow.setOnClickListener {
            if (currentTeam != null) {
                val teamCheck = (activity as MainActivity).getFollowedTeam(currentTeam!!)
                if (teamCheck != null) {
                    viewModel.deleteTeamRoom(teamCheck)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({
                                setFollowed(false)
                            }, {
                                Log.d("room", it.message)
                            })
                } else {
                    val team = teamsCache.get(currentTeam!!)
                    val teamRoom = Team()
                    teamRoom.teamID = team!!.team!!.id
                    teamRoom.name = team.team!!.name
                    teamRoom.abbr = team.team.abbreviation
                    teamRoom.city = team.team.city
                    viewModel.insertTeamRoom(teamRoom)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({
                                setFollowed(true)
                            }, {
                                Log.d("room", it.message)
                            })
                }
            }
        }
    }

    fun showTeamContent() {
        val outerWrapperParams = rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams
        val anim = ValueAnimator.ofInt(outerWrapperParams.height, teamContentHeight).setDuration(200)
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener {
            outerWrapperParams.height = it.animatedValue as Int
            rootView.ll_fragment_main_add_team_team_outer_wrapper.layoutParams = outerWrapperParams
        }
        anim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
                contentShown = true
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
                setFollowed(false)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
                contentShown = false
            }
        })
        anim.start()
    }

    fun animateCicle() {
        val params = rootView.iv_fragment_main_loading_circle.layoutParams as RelativeLayout.LayoutParams
        loadingAnim = ValueAnimator.ofInt(getPixels(20), getPixels(50)).setDuration(400)
        loadingAnim!!.interpolator = AccelerateDecelerateInterpolator()
        loadingAnim!!.addUpdateListener {
            params.marginEnd = it.animatedValue as Int
            rootView.iv_fragment_main_loading_circle.layoutParams = params
        }
        loadingAnim!!.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                rootView.iv_fragment_main_loading_circle.visibility = View.INVISIBLE
                params.marginEnd = getPixels(20)
                rootView.iv_fragment_main_loading_circle.layoutParams = params
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
                rootView.iv_fragment_main_loading_circle.visibility = View.VISIBLE
            }
        })
        loadingAnim!!.repeatCount = ValueAnimator.INFINITE
        loadingAnim!!.repeatMode = ValueAnimator.REVERSE
        loadingAnim!!.start()
    }

    fun setFollowed(followed: Boolean) {
        if (followed) {
            val drawable = ContextCompat.getDrawable(activity, R.drawable.joined_button_background)
            rootView.tv_fragment_main_add_team_follow_text.setTextColor(ContextCompat.getColor(activity, R.color.accent))
            rootView.tv_fragment_main_add_team_follow_text.background = drawable
        } else {
            rootView.tv_fragment_main_add_team_follow_text.setTextColor(ContextCompat.getColor(activity, R.color.icons))
            rootView.tv_fragment_main_add_team_follow_text.setBackgroundColor(ContextCompat.getColor(activity, R.color.accent))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
    }
}