'''
Description: Uses regex to check if the plate number is standard or custom.
Looks at the list of all possible US plate standard combinations
'''


import cv2
import easyocr
import re

state_plate_patterns = {
    'AL': re.compile(r'\d{1,2}[A-Z]{2}\d{3}'),  # Alabama: 1 or 2 digits, 2 letters, 3 digits
    'AK': re.compile(r'[A-Z]{3}\d{3}'),         # Alaska: 3 letters, 3 digits
    'AZ': re.compile(r'[A-Z]{3}\d{1}[A-Z]{1}\d{2}'), # Arizona: 3 letters, 1 digit, 1 letter, 2 digits
    'AR': re.compile(r'[A-Z]{3}\d{2}[A-Z]{1}'), # Arkansas: 3 letters, 2 digits, 1 letter
    'CA': re.compile(r'\d{1}[A-Z]{3}\d{3}'),    # California: 1 digit, 3 letters, 3 digits
    'CO': re.compile(r'[A-Z]{3}-\d{3}'),        # Colorado: 3 letters, dash, 3 digits
    'CT': re.compile(r'([A-Z]{2}•\d{5})|(\d{1}[A-Z]{2}•\d{1}[A-Z]{1}\d{1})'), # Connecticut: 2 patterns
    'DE': re.compile(r'\d{1,6}'),               # Delaware: 1 to 6 digits
    'DC': re.compile(r'[A-Z]{2}-\d{4}'),        # District of Columbia: 2 letters, dash, 4 digits
    'FL': re.compile(r'([A-Z]{3}\s[D]{1}\d{2})|(\d{3}\s[A-Z]{3})|([A-Z]{1}\d{2}\s\d{1}[A-Z]{2})'), # Florida: 3 patterns
    'GA': re.compile(r'[A-Z]{3}\d{4}'),         # Georgia: 3 letters, 4 digits
    'GU': re.compile(r'[A-Z]{2}\s\d{4}'),       # Guam: 2 letters, space, 4 digits
    'HI': re.compile(r'[A-Z]{1,3}[A-Z]{0,1}\d{3}'), # Hawaii: 1 to 3 letters, optionally followed by 1 letter, 3 digits
    'ID': re.compile(r'[A-Z]{1}\s\d{6}'),       # Idaho: 1 letter, space, 6 digits
    'IL': re.compile(r'[A-Z]{2}\s\d{5}'),       # Illinois: 2 letters, space, 5 digits
    'IN': re.compile(r'\d{3}[A-Z]{0,3}'),       # Indiana: 3 digits, optionally followed by up to 3 letters
    'IA': re.compile(r'[A-Z]{3}\s\d{3}'),       # Iowa: 3 letters, space, 3 digits
    'KS': re.compile(r'\d{3}\s[A-Z]{3}|(\d{4}[A-Z]{3})'), # Kansas: 3 digits, space, 3 letters, or 4 digits, 3 letters
    'KY': re.compile(r'[A-Z]{3}\d{3}'),         # Kentucky: 3 letters, 3 digits
    'LA': re.compile(r'[A-Z]{3}\s\d{3}'),       # Louisiana: 3 letters, space, 3 digits
    'ME': re.compile(r'\d{4}\s[A-Z]{2}'),       # Maine: 4 digits, space, 2 letters
    'MD': re.compile(r'1[A-Z]{2}\d{4}'),        # Maryland: 1 letter, 2 letters, 4 digits
    'MA': re.compile(r'(1[A-Z]{1}\s\d{3})|(\d{3}\s[A-Z]{3})'), # Massachusetts: 2 patterns
    'MI': re.compile(r'(1[A-Z]{3}\d{2})|([A-Z]{3}\s\d{4})'), # Michigan: 2 patterns
    'MN': re.compile(r'(123-[A-Z]{3})|(ABC-123)'), # Minnesota: 2 patterns
    'MS': re.compile(r'[A-Z]{3}\s\d{3}'),       # Mississippi: 3 letters, space, 3 digits
    'MO': re.compile(r'[A-Z]{2}\d{1}\s[A-Z]{1}\d{1}'), # Missouri: 2 letters, 1 digit, space, 1 letter, 1 digit
    'MT': re.compile(r'(0-\d{5}[A-Z]{1})|(ABC\d{3})'), # Montana: 2 patterns
    'NE': re.compile(r'(0-[A-Z]{1}\d{4})|(0-[A-Z]{1}\d{5})'), # Nebraska: 2 patterns
    'NV': re.compile(r'\d{3}·[A-Z]{2}\d{2}'),   # Nevada: 3 digits, dot, 2 letters, 2 digits
    'NH': re.compile(r'\d{3}\s\d{3,4}'),        # New Hampshire: 3 digits, space, 3 or 4 digits
    'NJ': re.compile(r'(D\d{2}-[A-Z]{3})|(ABC-[A-Z]{2})'), # New Jersey: 2 patterns
    'NM': re.compile(r'(123-[A-Z]{3})|(ABC-123)'), # New Mexico: 2 patterns
    'NY': re.compile(r'[A-Z]{3}-\d{4}'),        # New York: 3 letters, dash, 4 digits
    'NC': re.compile(r'[A-Z]{3}-\d{4}'),        # North Carolina: 3 letters, dash, 4 digits
    'ND': re.compile(r'\d{3}\s[A-Z]{3}'),       # North Dakota: 3 digits, space, 3 letters
    'MP': re.compile(r'[A-Z]{3}\s\d{3}'),       # Northern Mariana Islands: 3 letters, space, 3 digits
    'OH': re.compile(r'[A-Z]{3}\s\d{4}'),       # Ohio: 3 letters, space, 4 digits
    'OK': re.compile(r'[A-Z]{3}-\d{3}'),        # Oklahoma: 3 letters, dash, 3 digits
    'OR': re.compile(r'(\d{3}\s[A-Z]{3})|(ABC\s\d{3})'), # Oregon: 2 patterns
    'PA': re.compile(r'[A-Z]{3}-\d{4}'),  # Pennsylvania: 3 letters, dash, 4 digits
    'PR': re.compile(r'[A-Z]{3}-\d{3}'),  # Puerto Rico: 3 letters, dash, 3 digits
    'RI': re.compile(r'(1[A-Z]{2}-\d{3})|(\d{6})'),  # Rhode Island: 2 patterns
    'SC': re.compile(r'[A-Z]{3}\s\d{3}'),  # South Carolina: 3 letters, space, 3 digits
    'SD': re.compile(r'(0[A-Z]{1}\d{1}\s\d{3})|(0[A-Z]{2}\s\d{3})'),  # South Dakota: 2 patterns
    'TN': re.compile(r'[A-Z]{3}\s\d{4}'),  # Tennessee: 3 letters, space, 4 digits
    'TX': re.compile(r'[A-Z]{3}-\d{4}'),  # Texas: 3 letters, dash, 4 digits
    'UT': re.compile(r'(1[A-Z]{2}\s\d{3})|(A\d{2}\s\d{3}[A-Z]{1})'),  # Utah: 2 patterns
    'VT': re.compile(r'(\d{3}\s\d{3})|(12[A-Z]{2}\d{1})'),  # Vermont: 2 patterns
    'VI': re.compile(r'[A-Z]{3}\s\d{3}'),  # U.S. Virgin Islands: 3 letters, space, 3 digits
    'VA': re.compile(r'[A-Z]{3}-\d{4}'),  # Virginia: 3 letters, dash, 4 digits
    'WA': re.compile(r'([A-Z]{3}\d{4})|(\d{3}-[A-Z]{3})'),  # Washington: 2 patterns
    'WV': re.compile(r'[A-Z]{3}-\d{4}'),  # West Virginia: 3 letters, dash, 4 digits
    'WI': re.compile(r'([A-Z]{3}-\d{4})|(\d{3}-[A-Z]{3})'),  # Wisconsin: 2 patterns
    'WY': re.compile(r'(0-\d{5})|(0-\d{4}[A-Z]{1})')  # Wyoming: 2 patterns
}


# Function to check if plate is in standard form for the identified state
# Function to check if the plate is in a standard form
def is_plate_standard(plate_number):
    for state, pattern in state_plate_patterns.items():
        if pattern.match(plate_number):
            return True  # Plate matches a standard format
    return False  # No match found, plate does not follow a standard format

# Example usage
plate_number = "123ABC"  # Example plate number
standard_check = is_plate_standard(plate_number)

if standard_check:
    print("The plate follows a standard format.")
else:
    print("The plate does not follow a standard format.")
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
image_path = "C:/Users/RyanB/OneDrive/Documents/Computer_Science_Material/Capstone Code/Licence Plates/VA1.jpg"

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
all_texts = " ".join([detection[1].upper() for detection in ocr_results if detection[2] > confidence_threshold])

# Print the plate number
print("Plate Number:", plate_number.upper())

# Find the state name from the detected text
state_name = find_state_name(all_texts)

# Print the state name
print("State Name:", state_name.upper())

# Check if the plate number is in standard form
is_standard = is_plate_standard(plate_number)


# Print the result
if is_standard:
    print(f"The plate {plate_number} follows the standard format for {state_name}.")
elif is_standard == False:

    print(f"The plate {plate_number} does not follow the standard format for {state_name}.")
else:
    print("State name not found or no pattern defined for this state.")
