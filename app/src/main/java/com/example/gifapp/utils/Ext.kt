package com.example.gifapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.gifapp.BuildConfig
import com.example.gifapp.R
import kotlinx.coroutines.delay
import kotlin.math.min

fun isApiLevelAtLeast(api: Int): Boolean {
    return BuildConfig.VERSION_CODE >= api
}

fun Context.toast(message: String?) {
    if (message == null) return
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(message: String?) {
    requireContext().toast(message)
}

fun Context.color(color: Int) = ContextCompat.getColor(this, color)
fun Fragment.color(color: Int) = requireContext().color(color)

fun Context.drawable(drawableRes: Int, tintColorRes: Int? = null): Drawable {
    val drawable = ContextCompat.getDrawable(this, drawableRes)!!
    tintColorRes?.let {
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable),
            ContextCompat.getColor(this, it)
        )
    }
    return drawable
}
fun Fragment.drawable(drawableRes: Int, tintColorRes: Int? = null): Drawable {
    return requireContext().drawable(drawableRes, tintColorRes)
}

fun FragmentManager.isFragmentInBackstack(tag: String): Boolean {
    for (entry in 0 until backStackEntryCount) {
        if (tag == getBackStackEntryAt(entry).name) return true
    }
    return false
}

fun FragmentActivity.simpleNavigate(fragmentClass: Class<out Fragment>, bundle: Bundle = Bundle()) {
    val tag = fragmentClass.name

    if (supportFragmentManager.isFragmentInBackstack(tag)) {
        supportFragmentManager.popBackStackImmediate(tag, 0)
    } else {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmentClass, bundle)
            .addToBackStack(tag)
            .commit()
    }
}

fun View.color(color: Int) = context.color(color)

fun logDebug(string: String) {
    Log.i("tag_gif_debug", string)
}
