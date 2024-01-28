'''
Description:
This code uses EasyOCR and OpenCV to make the bounding boxes and pull the text from the plate.
Once the bounding boxes are made the largest bounding box is identified and the remaining image 
outside of the boxes is blacked ouyt and deemed unecessary. Since the plate number will always be 
the largest boundng box on the plate the text from this box is used to obtain the plate number. Then 
all of the text found is stored in a string which we eventually use to find the state.

Potential Issues:
If there are errors in pulling the exact state name then the state name will not be found from the list of 50.
'''
import cv2
import easyocr

# Find the state name in the string of text
def find_state_name(input_string):

    listofstates = [
        "ALABAMA", "ALASKA", "ARIZONA", "ARKANSAS", "CALIFORNIA",
        "COLORADO", "CONNECTICUT", "DELAWARE", "FLORIDA", "GEORGIA",
        "HAWAII", "IDAHO", "ILLINOIS", "INDIANA", "IOWA",
        "KANSAS", "KENTUCKY", "LOUISIANA", "MAINE", "MARYLAND",
        "MASSACHUSETTS", "MICHIGAN", "MINNESOTA", "MISSISSIPPI", "MISSOURI",
        "MONTANA", "NEBRASKA", "NEVADA", "NEW HAMPSHIRE", "NEW JERSEY",
        "NEW MEXICO", "NEW YORK", "NORTH CAROLINA", "NORTH DAKOTA", "OHIO",
        "OKLAHOMA", "OREGON", "PENNSYLVANIA", "RHODE ISLAND", "SOUTH CAROLINA",
        "SOUTH DAKOTA", "TENNESSEE", "TEXAS", "UTAH", "VERMONT",
        "VIRGINIA", "WASHINGTON", "WEST VIRGINIA", "WISCONSIN", "WYOMING"
    ]

    # Remove a substring from the sting read from the plate
    def remove_substring(original_string, substring_to_remove):
        return original_string.replace(substring_to_remove, '')

    # Use a loop to see if a stat is in the string
    for state in listofstates:
        if state in input_string:
            matchingState = state
            # Take that state name out (so it is not confused for a plate number)
            input_string = remove_substring(input_string, matchingState)

            # Return the rest of the string without the state and the state separately
            return matchingState

    # If no state found return none/null
    return None

# Initialize EasyOCR reader for English language
reader = easyocr.Reader(['en'])

# Define the path to the image
image_path = "REPLACE-WITH-FILE-PATH.jpg"
