package com.example.alit.sportsstatistics.ui.main

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.*
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseDialogFragment


class AddTeamDialogFragment : BaseDialogFragment() {

    companion object {

        const val ADD_TEAM_FRAGMENT_TAG = "addTeam"

        fun create(): AddTeamDialogFragment {
            return AddTeamDialogFragment()
        }

    }

    lateinit var rootView: View

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
}