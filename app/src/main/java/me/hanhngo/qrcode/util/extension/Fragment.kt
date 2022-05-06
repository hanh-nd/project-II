package me.hanhngo.qrcode.util.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.hideAppbar() {
    (this.requireActivity() as AppCompatActivity).supportActionBar?.hide()
}

fun Fragment.showAppbar() {
    (this.requireActivity() as AppCompatActivity).supportActionBar?.show()
}