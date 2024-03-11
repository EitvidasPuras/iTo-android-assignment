package com.puras.itoandroidassignment.data.mapper

import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.domain.entity.EntryResponse
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale

fun EntryResponse.toEntry(feedKey: String): Entry {
    return Entry(
        feedKey = feedKey,
        githubId = this.id,
        published = this.published.toDisplayableDate(),
        link = this.link,
        title = this.title,
        media = this.media,
        author = this.author,
        content = this.content
    )
}

private fun String?.toDisplayableDate(): String? {
    if (this == null) return null

    val zonedTime = ZonedDateTime.parse(this).toString()
    /* Github uses three different time & date formats for their feeds for some reason */
    val zonedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val notZonedFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val noSecondsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.getDefault())

    val formatToDisplay = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
    var formattedDate = ""
    try {
        val date = zonedFormat.parse(zonedTime)
        formattedDate = formatToDisplay.format(date!!)
    } catch (e: ParseException) {
        try {
            val date = notZonedFormat.parse(zonedTime)
            formattedDate = formatToDisplay.format(date!!)
        } catch (e: ParseException) {
            try {
                val date = noSecondsFormat.parse(zonedTime)
                formattedDate = formatToDisplay.format(date!!)
            } catch (e: ParseException) {
                Timber.e("toDisplayableDate() : ${e}")
            }
        }
    }

    return formattedDate
}