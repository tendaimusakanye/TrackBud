package com.tendai.common.media.extensions


fun String.createMediaId(baseRoot: String, type: String? = null, mediaId: String? = null): String {
    return "$baseRoot/$type|$this"
}

