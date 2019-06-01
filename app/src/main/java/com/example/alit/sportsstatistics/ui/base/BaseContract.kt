package com.example.alit.sportsstatistics.ui.base

import android.os.Bundle
import android.view.View

interface BaseContract {

    interface BaseView {

        fun startActivity(tarActivity: Class<*>)

        fun startActivity(tarActivity: Class<*>, bundle: Bundle)

        fun sendBroadcast(action: String)

        fun showSnackbar(message: String)

        fun showToast(message: String)

        fun removeFragment(tag: String, outAnim: Int)

        fun removeChildFragment(tag: String, outAnim: Int)

        fun dismissDialog(tag: String)

        fun hideKeyboard()

        fun showKeyboard()

        fun checkKeyboardVisible()

        fun onKeyboardVisibilityChecked(visible: Boolean)

        fun fadeIn(v: View, duration: Long)

        fun fadeOut(v: View, duration: Long)

        fun getStatusBarHeight() : Int

        fun getPixels(dp: Int): Int

        fun getDp(pxls: Int): Int

        fun finishActivity()

    }

    interface BasePresenter<V : BaseView> {

        fun onAttach(view: V)

        fun onDetach()

    }

}