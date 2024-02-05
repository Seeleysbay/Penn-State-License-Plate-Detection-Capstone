package com.example.parkingpermitapp.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import com.example.parkingpermitapp.domain.DetectionResult

//*****************************************************************************************
// Uses tensorflow lite Interpreter to set up a custom trained YOLOv8 object detection    *
// model. Takes as arguments: context from CameraPreview composable                       *
// and rotation degrees from upright position of the original ImageProxy                  *
// object.                                                                                *
//*****************************************************************************************
class CustomObjectDetector (private val context: Context, private val rotation: Int) {
    private lateinit var tflite: Interpreter
    private val imgsz = 640 //image size model was trained on
    private val outputSize =8400 // size of model's output tensor, for YOLOv8 this is the 3rd value in the tensor shape
    private val numDetectionParams = 5 // Number of parameters per detection (x, y, width, height, score)
    private val confidenceThreshold = 0.65 // Adjust this threshold as needed


    init {
        loadModelFile()
    }

    private fun loadModelFile() {
        val modelFile = "best_float32_4.tflite" // tflite YOLOv8 model name goes here
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val tfliteModel: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        tflite = Interpreter(tfliteModel)
    }
    fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val orientedBitmap = rotateBitmap(bitmap, rotation)
        val resizedBitmap = Bitmap.createScaledBitmap(orientedBitmap, imgsz, imgsz, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imgsz * imgsz * 3) // 4 bytes per channel
        byteBuffer.order(ByteOrder.nativeOrder())

        resizedBitmap?.let { resized ->
            for (y in 0 until resized.height) {
                for (x in 0 until resized.width) {
                    val pixelValue = resized.getPixel(x, y)
                    val r = Color.red(pixelValue)
                    val g = Color.green(pixelValue)
                    val b = Color.blue(pixelValue)

                    // Normalize pixel value to [0,1]
                    val rNormalized = (r/255.0f)
                    val gNormalized = (g /255.0f)
                    val bNormalized = (b /255.0f)

                    byteBuffer.putFloat(rNormalized)
                    byteBuffer.putFloat(gNormalized)
                    byteBuffer.putFloat(bNormalized)
                }
            }
        }
        return byteBuffer
    }
    fun runInference(inputData: ByteBuffer): DetectionResult? {
        val modelOutput = Array(1) { Array(numDetectionParams) { FloatArray(outputSize) } } // Adjust based on your model output
        tflite.run(inputData, modelOutput)

        var bestDetection: DetectionResult? = null


        for (i in 0 until outputSize) {
            val confidence = modelOutput[0][4][i]
            if (confidence >= confidenceThreshold) {
                if (bestDetection == null || confidence > bestDetection.confidence) {
                    val x = modelOutput[0][0][i]
                    val y = modelOutput[0][1][i]
                    val width = modelOutput[0][2][i]
                    val height = modelOutput[0][3][i]

                    Log.d("ObjectDetection", "Bounding Box: x=$x, y=$y, width=$width, height=$height")

                    bestDetection = DetectionResult(x, y, width, height, confidence)
                }
            }
        }

        return bestDetection
    }
    private fun rotateBitmap(source: Bitmap, rotationDegrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}