package com.example.parkingpermitapp.domain

// Data class used to hold Levenshtein spell check results
data class LevenshteinResult(
    var distance: Int,              // Levenshtein Distance from originalInference to state
    var state: String,              // State name used
    var originalInference: String   // original OCR inference
)