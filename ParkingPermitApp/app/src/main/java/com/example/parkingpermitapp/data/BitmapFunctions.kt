package com.example.parkingpermitapp.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.parkingpermitapp.domain.DetectionResult
import java.io.ByteArrayOutputStream

//****************************************************
// Class containing functions to create a bitmap     *
// from an ImageProxy object and to Gray out every   *
// thing outside the object detection bounding box   *
// of a bitmap.                                      *
//****************************************************
class BitmapFunctions {
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer // Y
        val uBuffer = imageProxy.planes[1].buffer // U
        val vBuffer = imageProxy.planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    }
    companion object {
        fun grayOutBitmapOutsideBoundingBox(
            source: Bitmap,
            detectionResult: DetectionResult,
            rotation: Int
        ): Bitmap {
            val orientedBitmap = rotateBitmap(source, rotation)
            // Create a mutable copy of the source bitmap
            val bitmapCopy = orientedBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(bitmapCopy)
            val paint = Paint().apply {
                color = Color.GRAY
            }

            // Calculate the bounding box coordinates
            val centerX = detectionResult.x * source.width
            val centerY = detectionResult.y * source.height
            val width = detectionResult.width * source.width
            val height = detectionResult.height * source.height
            val topLeftX = centerX - (width / 2)
            val topLeftY = centerY - (height / 2)
            val bottomRightX = centerX + (width / 2)
            val bottomRightY = centerY + (height / 2)
            Log.d("Source Bitmap Dim", "Cropped Bitmap topLX ${topLeftX} Cropped Bitmap topLY ${topLeftY}")
            Log.d("Source Bitmap Dim", "Cropped Bitmap botRX ${bottomRightX} Cropped Bitmap botRY ${bottomRightY}")
            // Gray out the top portion of the image
            canvas.drawRect(0f, 0f, source.width.toFloat(), topLeftY.toFloat(), paint)

            // Gray out the bottom portion of the image
            canvas.drawRect(
                0f,
                bottomRightY,
                source.width.toFloat(),
                source.height.toFloat(),
                paint
            )

            // Gray out the left portion of the image
            canvas.drawRect(
                0f,
                topLeftY,
                topLeftX,
                bottomRightY,
                paint
            )

            // Gray out the right portion of the image
            canvas.drawRect(
                bottomRightX,
                topLeftY,
                source.width.toFloat(),
                bottomRightY,
                paint
            )
            return bitmapCopy
        }
        private fun rotateBitmap(source: Bitmap, rotationDegrees: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }

}