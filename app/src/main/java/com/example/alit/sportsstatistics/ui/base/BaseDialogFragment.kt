package com.example.alit.sportsstatistics.ui.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup

open class BaseDialogFragment : DialogFragment(), BaseContract.BaseView {

    lateinit var activity: BaseActivity
    lateinit var displayMetrics: DisplayMetrics

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        activity = context as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMetrics = resources.displayMetrics
    }

    override fun startActivity(tarActivity: Class<*>) {
        activity.startActivity(tarActivity)
    }

    override fun startActivity(tarActivity: Class<*>, bundle: Bundle) {
        activity.startActivity(tarActivity, bundle)
    }

    override fun sendBroadcast(action: String) {
        activity.sendBroadcast(action)
    }

    override fun showSnackbar(message: String) {
        activity.showSnackbar(message)
    }

    override fun showToast(message: String) {
        activity.showToast(message)
    }

    override fun removeFragment(tag: String, outAnim: Int) {
        activity.removeFragment(tag, outAnim)
    }

    override fun removeChildFragment(tag: String, outAnim: Int) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(0, outAnim)
        fragmentTransaction.remove(childFragmentManager.findFragmentByTag(tag))
        fragmentTransaction.commit()
    }

    override fun dismissDialog(tag: String) {
        activity.dismissDialog(tag)
    }

    override fun hideKeyboard() {
        activity.hideKeyboard()
    }

    override fun showKeyboard() {
        activity.showKeyboard()
    }

    override fun checkKeyboardVisible() {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView.post {
            val heightDiff = rootView.rootView.height - rootView.height
            onKeyboardVisibilityChecked(heightDiff > getPixels(200))
        }
    }

    override fun onKeyboardVisibilityChecked(visible: Boolean) {

    }

    override fun fadeIn(v: View, duration: Long) {
        v.alpha = 0f
        v.visibility = View.VISIBLE
        v.animate().alpha(1f).setDuration(duration).setListener(null)
    }

    override fun fadeOut(v: View, duration: Long) {
        v.animate().alpha(0f).setDuration(duration).setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                v.visibility = View.GONE
            }
        })
    }

    override fun getStatusBarHeight(): Int {
        return activity.getStatusBarHeight()
    }

    override fun getPixels(dp: Int): Int {
        return (dp * displayMetrics.density).toInt()
    }

    override fun getDp(pxls: Int): Int {
        return (pxls / displayMetrics.density).toInt()
    }

    override fun finishActivity() {
        activity.finishActivity()
    }

    open fun onBackPressed(): Boolean {
        return false
    }
}