package com.rohyme.pickerlib

import com.google.gson.annotations.SerializedName

data class DaysModel(
    @SerializedName("days")
    var days: ArrayList<Day> = ArrayList()
) {
    data class Day(
        @SerializedName("arDay")
        var arDay: String = "",
        @SerializedName("arDayAbbrev")
        var arDayAbbrev: String = "",
        @SerializedName("dayID")
        var dayID: Int = 0,
        @SerializedName("enDay")
        var enDay: String = "",
        @SerializedName("enDayAbbrev")
        var enDayAbbrev: String = "",
        @SerializedName("deDay")
        var deDay: String = "",
        @SerializedName("deDayAbbrev")
        var deDayAbbrev: String = "",
        var isSelected :Boolean = true
    )
}