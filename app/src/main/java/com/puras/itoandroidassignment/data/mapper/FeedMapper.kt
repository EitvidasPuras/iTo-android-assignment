package com.puras.itoandroidassignment.data.mapper

import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.entity.LinkResponse

fun Map.Entry<String, LinkResponse>.toFeed(): Feed {
    return Feed(
        key = this.key,
        link = this.value.href
    )
}
