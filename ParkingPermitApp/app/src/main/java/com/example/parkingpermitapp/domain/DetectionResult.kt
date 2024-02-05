package com.example.parkingpermitapp.domain


//Data class to hold output of YOLOv8 object detection model
data class DetectionResult(
    val x: Float, // x coordinate of the center of the bounding box
    val y: Float, // y coordinate of the center of the bounding box
    val width: Float, // width of bounding box
    val height: Float, // height of bounding box
    val confidence: Float, //confidence score, how sure the model is that object detected is a LP
)