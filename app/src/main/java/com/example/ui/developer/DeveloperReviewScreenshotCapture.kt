package com.example.ui.developer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.ByteArrayOutputStream

fun captureDeveloperReviewScreenshot(activity: Activity): ByteArray {
    val content = activity.findViewById<View>(android.R.id.content)
        ?: throw IllegalStateException("developer_screenshot_content_missing")
    if (content.width <= 0 || content.height <= 0) {
        throw IllegalStateException("developer_screenshot_content_not_laid_out")
    }

    val bitmap = Bitmap.createBitmap(content.width, content.height, Bitmap.Config.ARGB_8888)
    return try {
        val canvas = Canvas(bitmap)
        content.draw(canvas)
        ByteArrayOutputStream().use { output ->
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 82, output)
            if (!compressed) throw IllegalStateException("developer_screenshot_compress_failed")
            output.toByteArray()
        }
    } finally {
        bitmap.recycle()
    }
}

tailrec fun Context.findDeveloperReviewActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findDeveloperReviewActivity()
    else -> null
}
