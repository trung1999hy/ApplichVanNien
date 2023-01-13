/*
 * Created by PS Solutions on 12/06/2021.
 * Copyright © 2021 Softbank. All rights reserved.
 *
 */

package com.example.universalcalendar.extensions

import android.os.SystemClock
import android.view.View

const val DEFAULT_INTERVAL = 1000

class SafeClickListener(
    private var defaultInterval: Int = DEFAULT_INTERVAL,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}
