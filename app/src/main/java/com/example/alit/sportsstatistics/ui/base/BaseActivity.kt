package com.example.alit.sportsstatistics.ui.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

open class BaseActivity : AppCompatActivity(), BaseContract.BaseView {

    lateinit var displayMetrics: DisplayMetrics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMetrics = resources.displayMetrics
    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(IconicsContextWrapper.wrap(newBase))
//    }

    override fun startActivity(tarActivity: Class<*>) {
        val intent = Intent(this, tarActivity)
        startActivity(intent)
    }

    override fun startActivity(tarActivity: Class<*>, bundle: Bundle) {
        val intent = Intent(this, tarActivity)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun sendBroadcast(action: String) {
        val intent = Intent();
        intent.action = action
        sendBroadcast(intent)
    }

    override fun showSnackbar(message: String) {
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        snackbar.setAction("Ok", View.OnClickListener {
            snackbar.dismiss()
        })
        snackbar.show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun removeFragment(tag: String, outAnim: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(0, outAnim)
        fragmentTransaction.remove(supportFragmentManager.findFragmentByTag(tag))
        fragmentTransaction.commit()
    }

    override fun removeChildFragment(tag: String, outAnim: Int) {

    }

    override fun dismissDialog(tag: String) {
        Log.d("testtest", "called dismissDialog")
        try {
            val fragment = supportFragmentManager.findFragmentByTag(tag) as BaseDialogFragment
            fragment.dismiss()
        } catch (e: Exception) {
            Log.d("dialog", "fragment was not a dialog")
        }
    }

    override fun hideKeyboard() {
        if (currentFocus != null) {
            val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (imm != null) imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    override fun showKeyboard() {
        if (currentFocus != null) {
            val imm: InputMethodManager? = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (imm != null) imm.toggleSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED, 0)
        }
    }

    override fun checkKeyboardVisible() {
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
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
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun getPixels(dp: Int): Int {
        return (dp * displayMetrics.density).toInt()
    }

    override fun getDp(pxls: Int): Int {
        return (pxls / displayMetrics.density).toInt()
    }

    override fun finishActivity() {
        finish()
    }
}