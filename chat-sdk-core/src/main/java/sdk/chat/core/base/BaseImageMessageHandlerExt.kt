package sdk.chat.core.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.runBlocking
import sdk.chat.core.session.ChatSDK
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

@JvmOverloads
fun compressImage(file: File, maxImageSize: Int = 600): File {
    val context = ChatSDK.ctx() ?: return file
    val (width, height) = file.getImageExtras(maxImageSize)

    return runBlocking {
        Compressor.compress(context, file) {
            if (width != 0 && height != 0)
                resolution(width, height)
            format(Bitmap.CompressFormat.JPEG)
            quality(80)
        }
    }
}

data class ImageExtras(
    val width: Int,
    val height: Int
)

@JvmOverloads
fun File.getImageExtras(maxImageSize: Int = 600): ImageExtras {
    val (width, height) = getImageSize()

    val dstWidth: Int
    val dstHeight: Int

    when {
        max(width, height) < maxImageSize -> {
            dstWidth = width
            dstHeight = height
        }
        height < width -> {
            dstWidth = maxImageSize
            dstHeight = (maxImageSize.toFloat() / width * height).roundToInt()
        }
        else -> {
            dstWidth = (maxImageSize.toFloat() / height * width).roundToInt()
            dstHeight = maxImageSize
        }
    }

    return ImageExtras(dstWidth, dstHeight)
}

private fun File.getImageSize(): ImageExtras {
    val options = getOptions()
    return ImageExtras(options.outWidth, options.outHeight)
}

private fun File.getOptions(): BitmapFactory.Options {
    return BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inputStream().use {
            BitmapFactory.decodeStream(it, null, this)
        }
    }
}