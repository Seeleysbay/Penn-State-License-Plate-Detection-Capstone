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
