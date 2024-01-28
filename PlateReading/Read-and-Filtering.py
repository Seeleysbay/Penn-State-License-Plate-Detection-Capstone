'''
Description: 
This code uses EasyOCR and OpenCV to make the bounding boxes and pull the sext from the plate. 
Once the bounding boxes are made the largest bounding box is identified and then the color is turned red. 
Since the plate number will always be the largest boundng box on the plate the text from this box is used to 
obtain the plate number. Then all of the text found is stored in a string which we eventually use to find 
the state from.

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
image_path = "REPLACE-WITH-FILEPATH.jpg"

# Read in the image and perform OCR
img = cv2.imread(image_path)
ocr_results = reader.readtext(image_path)

# Set a confidence threshold for OCR results
confidence_threshold = 0.2

# Initialize variables for the largest bounding box
largest_area = 0
largest_box = None
largest_text = ""
plate_number = ""  # Variable to store the plate number

# Find the largest bounding box
for detection in ocr_results:
    if detection[2] > confidence_threshold:
        # Calculate the area of the bounding box
        top_left = tuple([int(value) for value in detection[0][0]])
        bottom_right = tuple([int(value) for value in detection[0][2]])
        area = (bottom_right[0] - top_left[0]) * (bottom_right[1] - top_left[1])

        if area > largest_area:
            largest_area = area
            largest_box = detection[0]
            largest_text = detection[1]
            plate_number = detection[1]  # Store the text of the largest bounding box

# Process each detection and draw rectangles
for detection in ocr_results:
    if detection[2] > confidence_threshold:
        top_left = tuple([int(value) for value in detection[0][0]])
        bottom_right = tuple([int(value) for value in detection[0][2]])
        text = detection[1]

        # Check if it's the largest box
        if detection[0] == largest_box:
            color = (0, 0, 255)  # Red color for the largest box
        else:
            color = (0, 255, 0)  # Green color for other boxes

        # Draw rectangles around the text
        img = cv2.rectangle(img, top_left, bottom_right, color, 2)

      # Display the image with annotations
cv2.imshow("img", img)
cv2.waitKey(0)
cv2.destroyAllWindows()

# Initialize an empty string to hold all texts
all_texts = ""

for detection in ocr_results:
    text = detection[1]  # The text is the second element in the tuple
    all_texts += text + " "  # Concatenate each text with a space

# Trim any extra space at the end and print the result
all_texts = all_texts.strip().upper()
print("All Detected Texts:", all_texts)

# Print the plate number
print("Plate Number:", plate_number)
state_name = find_state_name(all_texts)
print("State:", state_name)
