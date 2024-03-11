package com.puras.itoandroidassignment.presentation.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.puras.itoandroidassignment.R

@Composable
fun CacheImage(
    modifier: Modifier = Modifier,
    url: String
) {
    val error = remember { mutableStateOf(false) }
    if (!error.value) {
        AsyncImage(
            modifier = modifier,
            model = url,
            contentDescription = null,
            onError = {
                error.value = true
            }
        )
    } else {
        Box(
            modifier = Modifier.background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = R.drawable.ic_broken_image),
                contentDescription = null
            )
        }
    }
}