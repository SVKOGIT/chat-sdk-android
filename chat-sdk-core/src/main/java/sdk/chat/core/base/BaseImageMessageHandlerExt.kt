package sdk.chat.core.base

import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.runBlocking
import sdk.chat.core.session.ChatSDK
import java.io.File

fun compressImage(file: File): File {
    val context = ChatSDK.ctx()

    return runBlocking {
        Compressor.compress(context, file) {
            quality(80)
            format(Bitmap.CompressFormat.JPEG)
        }
    }
}