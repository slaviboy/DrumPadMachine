package com.slaviboy.drumpadmachine.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

/**
 * [AsyncImage] wrapper used everywhere a network image (preset/lesson cover) is loaded, so disk
 * and memory caching stay enabled consistently across the app instead of being configured
 * per call site.
 */
@Composable
fun CachedAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: Painter? = null,
    error: Painter? = null
) {
    val context = LocalContext.current
    AsyncImage(
        model = remember(model) {
            ImageRequest.Builder(context)
                .data(model)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build()
        },
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = placeholder,
        error = error
    )
}
