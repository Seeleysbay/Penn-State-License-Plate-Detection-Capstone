package com.example.parkingpermitapp.data;

import android.graphics.Rect;
import android.util.Log;
import com.google.mlkit.vision.text.Text;
import com.example.parkingpermitapp.domain.LevenshteinResult;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.HashMap;
import java.util.List;

//********************************************************
// Generic class to evaluate baseline OCR results.       *
// Extracts the plate code by finding largest bounding   *
// box line. Returns a state only if an exact match for  *
// a state is found. No error corrections/spell checking *
// are performed.                                        *
//********************************************************

public class GenericOCR {

    private String[] states = {
            "PENNSYLVANIA", "MARYLAND", "JERSEY", "YORK", "MYFLORIDA.COM", "HAMPSHIRE", "MEXICO", "CAROLINA", "DAKOTA",
            "VIRGINIA", "VIRGINIA", "ALASKA", "ALABAMA", "ARKANSAS", "ARIZONA", "CALIFORNIA", "COLORADO",
            "CONNECTICUT", "DELAWARE", "GEORGIA", "HAWAII", "IOWA", "IDAHO", "ILLINOIS",
            "INDIANA", "KANSAS", "KENTUCKY", "LOUISIANA", "MASSACHUSETTS", "MAINE", "MICHIGAN",
            "MINNESOTA", "MISSOURI", "MISSISSIPPI", "MONTANA", "NEBRASKA", "NEVADA", "OHIO", "OKLAHOMA",
            "OREGON", "RHODE ISLAND", "TENNESSEE", "TEXAS", "UTAH", "VERMONT",
            "WASHINGTON", "WISCONSIN", "WYOMING"
    };
    //********************************************************
    // Iterates through every line of result.text and        *
    // returns the line that has the largest bounding box.   *
    // This should be the license plate number. Iterates     *
    // through all elements and checks for a state.          *
    //********************************************************
    public String getOutPutText(Text result) {
        int largestHeight = 0; // To find the line bounding box with greatest height.
        String plateNumber = "";
        String state = "";
        boolean notFound = true;
        String elementPrefix = "";
        LevenshteinResult mostAccurateState = new LevenshteinResult(100, "", "");

        List<Text.TextBlock> blocks = result.getTextBlocks();
        for (Text.TextBlock block : blocks) {
            List<Text.Line> lines = block.getLines();
            for (Text.Line line : lines) {
                Rect lineFrame = line.getBoundingBox();
                int yHeight = 0;
                if (lineFrame != null) {
                    yHeight = lineFrame.bottom - lineFrame.top;
                }
                if (yHeight > largestHeight) {
                    largestHeight = yHeight;
                    plateNumber = line.getText(); // The line with the largest bounding box height.
                    Log.d("Line Text:", "Plate Number: " + plateNumber);
                    Log.d("Line Distance:", "yDistance: " + yHeight + " ");
                }
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText().toUpperCase();
                    if (elementText.contains("NORTH")) elementPrefix = "NORTH";
                    if (elementText.contains("SOUTH")) elementPrefix = "SOUTH";
                    if (elementText.contains("WEST")) elementPrefix = "WEST";
                    if (!elementPrefix.equals("")) state = elementPrefix + " ";
                    Log.d("Blocks number", "number of blocks: " + blocks.size());
                    // Exclude elements less than 3 characters and the line containing the LP code.
                    if (notFound && elementText.length() > 2 && yHeight < 100) {
                        // Pass elementText into Levenshtein distance method.
                        LevenshteinResult temp = checkStateAccuracy(elementText);
                        if (temp.getDistance() < mostAccurateState.getDistance()) {
                            mostAccurateState.setDistance(temp.getDistance());
                            mostAccurateState.setState(temp.getState());
                            mostAccurateState.setOriginalInference(temp.getOriginalInference());
                            if (mostAccurateState.getDistance() == 0) notFound = false;
                        }
                    }
                }
            }
        }
        Log.d("Spell Check:", "Natural OCR: " + mostAccurateState.getOriginalInference());
        Log.d("Spell Check:", "retval. score: " + mostAccurateState.getDistance());
        Log.d("Spell Check:", "retval. state: " + mostAccurateState.getState());
        if (mostAccurateState.getDistance() == 0) { // Distance == 0 requires an exact match.
            state = state + mostAccurateState.getState();
        }
        state = getStateCode(state); // Convert state to two letter abbreviation, if no state found -> ""
        return plateNumber + "_" + state;
    }
    private LevenshteinResult checkStateAccuracy(String element) {
        LevenshteinDistance distance = new LevenshteinDistance();
        LevenshteinResult retval = new LevenshteinResult(100, "", "");
        int score;

        for (int i = 0; i < states.length; i++) {
            score = distance.apply(element, states[i]);
            if (score < retval.getDistance()) { // Assuming getter method for distance
                retval.setDistance(score); // Assuming setter method for
                retval.setState(states[i]); // Assuming setter method for state
                retval.setOriginalInference(element); // Assuming setter method for originalInference
            }
        }
        return retval;
    }
    private String getStateCode(String state){
        HashMap<String, String> states = new HashMap<>();
        states.put("ALABAMA", "AL");
        states.put("ALASKA", "AK");
        states.put("ARIZONA", "AZ");
        states.put("ARKANSAS", "AR");
        states.put("CALIFORNIA", "CA");
        states.put("COLORADO", "CO");
        states.put("CONNECTICUT", "CT");
        states.put("DELAWARE", "DE");
        states.put("FLORIDA", "FL");
        states.put("MYFLORIDA.COM", "FL");
        states.put("GEORGIA", "GA");
        states.put("HAWAII", "HI");
        states.put("IDAHO", "ID");
        states.put("ILLINOIS", "IL");
        states.put("INDIANA", "IN");
        states.put("IOWA", "IA");
        states.put("KANSAS", "KS");
        states.put("KENTUCKY", "KY");
        states.put("LOUISIANA", "LA");
        states.put("MAINE", "ME");
        states.put("MARYLAND", "MD");
        states.put("MASSACHUSETTS", "MA");
        states.put("MICHIGAN", "MI");
        states.put("MINNESOTA", "MN");
        states.put("MISSISSIPPI", "MS");
        states.put("MISSOURI", "MO");
        states.put("MONTANA", "MT");
        states.put("NEBRASKA", "NE");
        states.put("NEVADA", "NV");
        states.put("NEW HAMPSHIRE", "NH");
        states.put("NEW JERSEY", "NJ");
        states.put("NEW MEXICO", "NM");
        states.put("NEW YORK", "NY");
        states.put("NORTH CAROLINA", "NC");
        states.put("NORTH DAKOTA", "ND");
        states.put("OHIO", "OH");
        states.put("OKLAHOMA", "OK");
        states.put("OREGON", "OR");
        states.put("PENNSYLVANIA", "PA");
        states.put("RHODE ISLAND", "RI");
        states.put("SOUTH CAROLINA", "SC");
        states.put("SOUTH DAKOTA", "SD");
        states.put("TENNESSEE", "TN");
        states.put("TEXAS", "TX");
        states.put("UTAH", "UT");
        states.put("VERMONT", "VT");
        states.put("VIRGINIA", "VA");
        states.put("WASHINGTON", "WA");
        states.put("WEST VIRGINIA", "WV");
        states.put("WISCONSIN", "WI");
        states.put("WYOMING", "WY");

        return states.getOrDefault(state, "");
    }

}
