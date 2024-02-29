package com.example.parkingpermitapp.cameraview

import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import com.example.parkingpermitapp.domain.DetectionResult
import com.example.parkingpermitapp.data.CustomObjectDetector
import com.example.parkingpermitapp.ui.theme.Navy
import java.util.concurrent.ExecutorService
import com.example.parkingpermitapp.data.DisplayResult
import com.example.parkingpermitapp.data.TextExtraction
import androidx.compose.material3.Text
import com.example.parkingpermitapp.data.BitmapFunctions
import androidx.compose.ui.unit.sp
import com.example.parkingpermitapp.network.PlatesAPI
import com.example.parkingpermitapp.network.RetrofitClient


@Composable
fun AppFunctions(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    //Listeners
    val detectionResultState = remember { mutableStateOf<DetectionResult?>(null) }
    var ocrResultState = remember { mutableStateOf<String>("") }
    val isAnalysisActive = remember { mutableStateOf(true) }

    val imageAnalysisExecutor = Executors.newSingleThreadExecutor()
    val cameraPreviewWidth = 360 //width of the camera preview in app display, applied in .dp
    val cameraPreviewHeight = 360 //height of the camera preview in app display, applied in .dp
    val platesApi =
        RetrofitClient.getClient("https://pennstateocr-api.azurewebsites.net/").create(
            PlatesAPI::class.java
        )

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Scan A License Plate",
            modifier = Modifier.padding(5.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Navy,
            fontSize = 20.sp
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CameraPreview(
                modifier = Modifier
                    .width(cameraPreviewWidth.dp)
                    .height(cameraPreviewHeight.dp)
                    .clipToBounds(),
                imageAnalysisExecutor,
                detectionResultState,
                ocrResultState,
                isAnalysisActive
            )

            if (ocrResultState.value.isNotEmpty()) {
                isAnalysisActive.value = false
                DisplayResult(ocrResultState = ocrResultState, platesApi) {
                    // trailing lambda, when close button is clicked
                    // Handle the close action, e.g., clear the OCR result
                    ocrResultState.value = ""
                    isAnalysisActive.value = true
                }
            }

        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier,
                  imageAnalysisExecutor: ExecutorService,
                  detectionResultState: MutableState<DetectionResult?>,
                  ocrResultState: MutableState<String>,
                  isAnalysisActive: MutableState<Boolean>
) {
    val context = LocalContext.current
    Box(modifier = modifier){
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    val imageAnalysis = ImageAnalysis.Builder()
                        // possible issue if this resolution isn't supported on a device and CameraX
                        // chooses something different. Need to maintain an aspect ratio of 1 for proper bounding box scaling
                        .setTargetResolution(Size(1080, 1080))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    try {
                        // Unbind all use cases before rebinding
                        cameraProvider.unbindAll()



                        //Image Analysis, inside the Lambda expression, an ImageProxy object is converted to a bitmap
                        // and passed to the appropriate CustomObjectDetector class functions for preprocessing and
                        // inference. If License Plate is detected, bitmap is cropped to object detection boundaries,
                        // and passed to a TextExtraction object where ML Kit Text Recognition inference is done.
                        imageAnalysis.setAnalyzer(imageAnalysisExecutor, ImageAnalysis.Analyzer { imageProxy ->

                                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                val bitmapFunctions = BitmapFunctions()
                                val bitmap = bitmapFunctions.imageProxyToBitmap(imageProxy)

                                val tfLiteModel = CustomObjectDetector(context, rotationDegrees) //initialization of CustomObjectDetector object
                                val preprocessBitmap = tfLiteModel.preprocessImage(bitmap)
                                val result = tfLiteModel.runInference(preprocessBitmap) //this is a DetectionResult object
                                if(result != null){
                                    Log.d("ObjectDetection", "Detection confidence: ${result.confidence}")
                                    detectionResultState.value = result
                                    //create a cropped bitmap
                                    val croppedBitmap = BitmapFunctions.grayOutBitmapOutsideBoundingBox(bitmap, result, rotationDegrees)

                                    //pass TextExtraction object for ML Kit Text Recognition inference
                                    if(croppedBitmap != null && isAnalysisActive.value) {
                                        val textExtraction = TextExtraction(croppedBitmap)
                                        textExtraction.processImage(
                                            onResult = { extractedText ->
                                                // Handle the extracted text
                                                Log.d("text output", extractedText)
                                                if (extractedText.isNotEmpty()) {
                                                    //result of ML Kit Text Recognition inference
                                                    //this string is passed to DisplayResult composable function
                                                    ocrResultState.value = extractedText
                                                }
                                            },
                                            onError = { error ->
                                                //
                                            }
                                        )
                                    }
                                }
                                else{
                                    detectionResultState.value = null
                                }

                            imageProxy.close()
                        })

                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                            context as LifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            imageAnalysis,
                            preview
                        )
                    } catch (exc: Exception) {
                        // Handle exceptions
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()

        )
        BoundingBoxOverlay(detectionResult = detectionResultState.value)
    }
}