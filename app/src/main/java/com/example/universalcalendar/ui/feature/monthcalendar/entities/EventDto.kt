package com.example.universalcalendar.ui.feature.monthcalendar.entities

import com.example.universalcalendar.ui.adapter.EventAdapter

class EventDto(
    var viewType: EventAdapter.ItemViewType,
    var dayOfWeek: String?,
    var day: String?,
    var month: String?,
    var year: String?,
    var timeStart: String? = "",
    var timeEnd: String? = "",
    var contentEvent: String?,
    var address: String? = ""
)