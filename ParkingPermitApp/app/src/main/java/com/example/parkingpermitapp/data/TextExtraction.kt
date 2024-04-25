package com.example.parkingpermitapp.data
//https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/similarity/LevenshteinDistance.html

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.ColorMatrix
import android.graphics.Paint
import com.example.parkingpermitapp.domain.LevenshteinResult
import org.apache.commons.text.similarity.LevenshteinDistance
import com.example.parkingpermitapp.data.GenericOCR


//****************************************************
// Utilizes Google ML Kit Text Recognition.          *
// Constructor receives a bitmap as a parameter.     *
// Grayscale is applied and then inference is        *
// performed on the modified bitmap. Text output     *
// is filtered for only license plate number and     *
// state.                                            *
//****************************************************

class TextExtraction(private val bitmap: Bitmap) {
    private val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val states = listOf(
        "PENNSYLVANIA", "MARYLAND", "JERSEY", "YORK", "MYFLORIDA.COM", "HAMPSHIRE", "MEXICO", "CAROLINA", "DAKOTA",
        "VIRGINIA", "VIRGINIA", "ALASKA", "ALABAMA", "ARKANSAS", "ARIZONA", "CALIFORNIA", "COLORADO",
        "CONNECTICUT", "DELAWARE", "GEORGIA", "HAWAII", "IOWA", "IDAHO", "ILLINOIS",
        "INDIANA", "KANSAS", "KENTUCKY", "LOUISIANA", "MASSACHUSETTS", "MAINE", "MICHIGAN",
        "MINNESOTA", "MISSOURI", "MISSISSIPPI", "MONTANA", "NEBRASKA", "NEVADA", "OHIO", "OKLAHOMA",
        "OREGON", "RHODE ISLAND", "TENNESSEE", "TEXAS", "UTAH", "VERMONT",
        "WASHINGTON", "WISCONSIN", "WYOMING"
    )


    fun processImage(onResult: (String) -> Unit, onError: (Exception) -> Unit) {
        val bmpGrayscale = toGrayscale(bitmap)
        val image = InputImage.fromBitmap(bmpGrayscale, 0)
        Log.d("Image Dimensions","MLKIT image width: ${image.width}, MLKIT image height: ${image.height}")
        recognizer.process(image)
            .addOnSuccessListener { result ->
                //result contains all text in image, output will contain text after post OCR processing
                //val generic = GenericOCR();
                //val output = generic.getOutPutText(result)

                val postOCR = EnhancedPostOCR();
                val output = postOCR.getOutPutText(result)

                onResult(output)
            }
            .addOnFailureListener { e ->
                onError(e)
                Log.e("TextExtraction", "Error during text recognition: ${e.localizedMessage}")
            }
    }
    //Converts the bitmap to grayscale for better OCR inference
    private fun toGrayscale(bitmap: Bitmap): Bitmap {
        val bmpGrayscale = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayscale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bmpGrayscale
    }
    //********************************************************
    // Iterates through every line of result.text and        *
    // returns the line that has the largest bounding box.   *
    // This should be the license plate number. Also         *
    // checks the first and last line for the state.         *
    // Returns the plate number followed by a space and      *
    // the state in upper case.                              *
    // For API purposes, might make a seperate class for     *
    // this function, easier for other projects to use w/    *
    // ML Kit(Replaced with OCR classes)                      *
    //********************************************************
    private fun getOutPutText( result: Text): String {
        var largestHeight = 0   // to find the line bounding box with greatest height
        var plateNumber = ""
        var state = ""
        var notFound = true
        var elementPrefix = ""
        val mostAccurateState: LevenshteinResult = LevenshteinResult(100,"", "")

        val blocks = result.textBlocks
        for (blockIndex in result.textBlocks.indices) {
            val block = result.textBlocks[blockIndex]
            val lines = block.lines
            for (lineIndex in block.lines.indices) {
                val line = block.lines[lineIndex]
                val lineFrame = line.boundingBox
                var yHeight = 0
                if(lineFrame != null) {
                    yHeight = lineFrame.bottom - lineFrame.top

                }
                if(yHeight > largestHeight){
                    largestHeight = yHeight
                    plateNumber = line.text // The line with the largest bounding box height
                    Log.d("Line Text:", "Plate Number: ${plateNumber}")
                    Log.d("Line Distance:", "yDistance: ${yHeight} ")
                }
                for (element in line.elements) {
                    val elementText = element.text.uppercase()
                    if("NORTH" in elementText){elementPrefix = "NORTH"}
                    if("SOUTH" in elementText){elementPrefix = "SOUTH"}
                    if("WEST" in elementText){elementPrefix = "WEST"}
                    if(elementPrefix != ""){state = elementPrefix + " "}
                    Log.d("Blocks number", "number of blocks: ${blocks.size}")
                    // Exclude elements less than 3 characters and the line containing the LP code
                    if(notFound && elementText.length > 2 && yHeight < 100){
                        //pass elementText into Levenshtein distance method
                        val temp: LevenshteinResult = checkStateAccuracy(elementText)
                        if(temp.distance < mostAccurateState.distance){
                            mostAccurateState.distance = temp.distance
                            mostAccurateState.state = temp.state
                            mostAccurateState.originalInference = temp.originalInference
                            if(mostAccurateState.distance == 0) {notFound = false}

                        }
                    }
                }
            }
        }
        Log.d("Spell Check:", "Natural OCR: ${mostAccurateState.originalInference}")
        Log.d("Spell Check:", "retval. score: ${mostAccurateState.distance}")
        Log.d("Spell Check:", "retval. state: ${mostAccurateState.state}")
        if(mostAccurateState.distance <= 2){ // distance == 0 requires an exact match, distance <= 1 allows for 1 char insert/delete/substitution, etc.
            state = state + mostAccurateState.state
        }
        return plateNumber + " " + state
    }
    //***********************************************************************
    // Uses Levenshtein Distance algorithm to calculate closeness of an     *
    // element from OCR result to a US state. The closest result is         *
    // returned.                                                            *
    //***********************************************************************
    private fun checkStateAccuracy(element: String): LevenshteinResult {
        val distance: LevenshteinDistance = LevenshteinDistance()
        val retval: LevenshteinResult = LevenshteinResult(100, "", "")
        var score: Int
        for (i in states.indices) {
            score = distance.apply(element, states[i])
            if(score < retval.distance){
                retval.distance = score
                retval.state = states[i]
                retval.originalInference = element
            }
        }
        return retval
    }
    //***************************************
    // Hash map containing all 50 states.
    //***************************************
    private fun stateCheck(elementText: String): Boolean {
        val states = hashMapOf(
            "ALABAMA" to "ALABAMA",
            "ALASKA" to "ALASKA",
            "ARIZONA" to "ARIZONA",
            "ARKANSAS" to "ARKANSAS",
            "CALIFORNIA" to "CALIFORNIA",
            "CAROLINA" to "CAROLINA",
            "COLORADO" to "COLORADO",
            "CONNECTICUT" to "CONNECTICUT",
            "DAKOTA" to "DAKOTA",
            "DELAWARE" to "DELAWARE",
            "FLORIDA" to "FLORIDA",
            "GEORGIA" to "GEORGIA",
            "HAWAII" to "HAWAII",
            "IDAHO" to "IDAHO",
            "ILLINOIS" to "ILLINOIS",
            "INDIANA" to "INDIANA",
            "IOWA" to "IOWA",
            "KANSAS" to "KANSAS",
            "KENTUCKY" to "KENTUCKY",
            "LOUISIANA" to "LOUISIANA",
            "MAINE" to "MAINE",
            "MARYLAND" to "MARYLAND",
            "MASSACHUSETTS" to "MASSACHUSETTS",
            "MICHIGAN" to "MICHIGAN",
            "MINNESOTA" to "MINNESOTA",
            "MISSISSIPPI" to "MISSISSIPPI",
            "MISSOURI" to "MISSOURI",
            "MONTANA" to "MONTANA",
            "MYFLORIDA.COM" to "FLORIDA",
            "MYFLORIDA" to "FLORIDA",
            "NEBRASKA" to "NEBRASKA",
            "NEVADA" to "NEVADA",
            "NEW HAMPSHIRE" to "NEW HAMPSHIRE",
            "HAMPSHIRE" to "NEW HAMPSHIRE",
            "NEW JERSEY" to "NEW JERSEY",
            "JERSEY" to "NEW JERSEY",
            "NEW MEXICO" to "NEW MEXICO",
            "MEXICO" to "NEW MEXICO",
            "NEW YORK" to "NEW YORK",
            "YORK" to "NEW YORK",
            "NORTH CAROLINA" to "NORTH CAROLINA",
            "NORTH DAKOTA" to "NORTH DAKOTA",
            "OHIO" to "OHIO",
            "OKLAHOMA" to "OKLAHOMA",
            "OREGON" to "OREGON",
            "PENNSYLVANIA" to "PENNSYLVANIA",
            "RHODE ISLAND" to "RHODE ISLAND",
            "SOUTH CAROLINA" to "SOUTH CAROLINA", //
            "SOUTH DAKOTA" to "SOUTH DAKOTA", //
            "SUNSHINE STATE" to "FLORIDA",
            "TENNESSEE" to "TENNESSEE",
            "TEXAS" to "TEXAS",
            "UTAH" to "UTAH",
            "VERMONT" to "VERMONT",
            "VIRGINIA" to "VIRGINIA",
            "WASHINGTON" to "WASHINGTON",
            "WEST VIRGINIA" to "WEST VIRGINIA",
            "WEST" to "WEST VIRGINIA",
            "WISCONSIN" to "WISCONSIN",
            "WYOMING" to "WYOMING"
        )
        Log.d("State Text:", "State: ${elementText}")
        return states.containsKey(elementText)

    }
}
