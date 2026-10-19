package com.v2ray.ang.ui.main

import androidx.annotation.StringRes
import com.v2ray.ang.R

internal enum class HomeDayPart {
    Morning,
    Afternoon,
    Evening,
    Night,
}

internal fun homeDayPart(hour: Int): HomeDayPart = when (hour) {
    in 5..11 -> HomeDayPart.Morning
    in 12..17 -> HomeDayPart.Afternoon
    in 18..22 -> HomeDayPart.Evening
    else -> HomeDayPart.Night
}

@StringRes
internal fun homeGreetingRes(hour: Int): Int = when (homeDayPart(hour)) {
    HomeDayPart.Morning -> R.string.home_greeting_morning
    HomeDayPart.Afternoon -> R.string.home_greeting_afternoon
    HomeDayPart.Evening -> R.string.home_greeting_evening
    HomeDayPart.Night -> R.string.home_greeting_night
}
