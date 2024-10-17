package com.abdownloadmanager.utils.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import ir.amirab.util.compose.IconSource
import ir.amirab.util.compose.fromUri
import kotlinx.serialization.Serializable

/**
 * @param path
 * this is a default download path for this category
 * @param icon
 * can be used by [IconSource]
 */
@Immutable
@Serializable
data class Category(
    val id: Long,
    val name: String,
    val icon: String,
    val path: String,
    val acceptedFileTypes: List<String> = emptyList(),
    // this is optional if nothing provided it means that every url is acceptable
    val acceptedUrlPatterns: List<String> = emptyList(),
    val items: List<Long> = emptyList(),
) {
    val hasUrlPattern = acceptedUrlPatterns.isNotEmpty()

    fun acceptFileName(fileName: String): Boolean {
        return acceptedFileTypes.any { ext ->
            fileName.endsWith(
                suffix = ".$ext",
                ignoreCase = true
            )
        }
    }

    fun withExtraItems(newItems: List<Long>): Category {
        return copy(
            items = items.plus(newItems).distinct()
        )
    }

    fun acceptUrl(url: String): Boolean {
        if (!hasUrlPattern) {
            return true
        }
        return acceptedUrlPatterns.any {
            test(
                patten = it,
                input = url
            )
        }
    }
}

private fun test(
    patten: String,
    input: String,
): Boolean {
    return patten
        .split("*")
        .joinToString(".*") { Regex.escape(it) }
        .toRegex()
        .containsMatchIn(input)
}

fun Category.iconSource(): IconSource? {
    return IconSource.fromUri(icon)
}

@Composable
fun Category.rememberIconPainter(): IconSource? {
    return remember(icon) {
        iconSource()
    }
}