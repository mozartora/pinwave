package com.pinwave.core.util

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Exports a composable captured in a [GraphicsLayer] as a PNG and hands it to
 * the Android share sheet (spec §46). The card design lives in
 * [com.pinwave.core.ui.components.ShareTrendCard].
 */
object ShareCardSharer {

    suspend fun share(context: Context, layer: GraphicsLayer, trendTitle: String) {
        val bitmap: ImageBitmap = layer.toImageBitmap()
        val file = File(File(context.cacheDir, "share"), "pinwave_trend.png")
        file.parentFile?.mkdirs()
        FileOutputStream(file).use { out ->
            bitmap.asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(context, "com.pinwave.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "$trendTitle — spotted on Pinwave. See what's next.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share trend"))
    }
}
