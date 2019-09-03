package ru.skillbranch.devintensive.extensions

import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.devintensive.R

fun Snackbar.setBackgroundDrawable(@DrawableRes drawableRes: Int): Snackbar = apply {
    view.setBackgroundResource(drawableRes)
}

fun Snackbar.setTextColor(@ColorInt textColor: Int): Snackbar = apply {
    view.findViewById<TextView>(R.id.snackbar_text)
        .setTextColor(textColor)
}