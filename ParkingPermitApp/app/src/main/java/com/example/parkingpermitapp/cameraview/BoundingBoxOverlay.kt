package com.example.parkingpermitapp.cameraview

import android.util.Log
import androidx.compose.runtime.Composable
import com.example.parkingpermitapp.domain.DetectionResult
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke


//********************************************************************
// Receives a DetectionResult object as an argument and uses         *
// the (x,y) coordinate along with the width and height to draw      *
// a bounding box over the Android View camera preview in real time. *
// Canvas size matches Camera Preview size exactly.                  *
//********************************************************************
@Composable
fun BoundingBoxOverlay(detectionResult: DetectionResult?) {
    Canvas(modifier = Modifier.fillMaxSize())
    {
        if(detectionResult != null){
            val canvasWidth = size.width
            val canvasHeight = size.height

            val scaledX = detectionResult.x * canvasWidth
            val scaledY = detectionResult.y * canvasHeight
            val scaledWidth = detectionResult.width * canvasWidth
            val scaledHeight = detectionResult.height * canvasHeight
            Log.d(
                "CanvasBoxCoor",
                "Canvas Box: canvasWidth=$canvasWidth, canvasHeight=$canvasHeight"
            )
            drawRect(
                color = Color.Red,
                topLeft = Offset(
                    scaledX - (scaledWidth / 2),
                    scaledY - (scaledHeight / 2)
                ),
                size = Size(width = scaledWidth, height = scaledHeight),
                style = Stroke(width = 5f)
            )
        }
        else{
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(0f, 0f),
                size = Size(width = size.width, height = size.height)
            )
        }
    }
}