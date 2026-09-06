package com.example.ui.screens

import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.model.WeekendEchoMotif

@DrawableRes
internal fun WeekendEchoMotif.drawableRes(): Int = when (this) {
    WeekendEchoMotif.COUCH_BLANKET -> R.drawable.weekend_echo_couch
    WeekendEchoMotif.CINEMA -> R.drawable.weekend_echo_cinema
    WeekendEchoMotif.CITY -> R.drawable.weekend_echo_city
    WeekendEchoMotif.COUNTRY -> R.drawable.weekend_echo_country
    WeekendEchoMotif.ROADTRIP -> R.drawable.weekend_echo_roadtrip
    WeekendEchoMotif.TRAIN -> R.drawable.weekend_echo_train
    WeekendEchoMotif.CAMPING -> R.drawable.weekend_echo_camping
    WeekendEchoMotif.HOTEL -> R.drawable.weekend_echo_hotel
}
