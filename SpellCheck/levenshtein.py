import Levenshtein as lev

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

def find_closest_word(input_word, word_list):
    # Initialize minimum distance and closest word
    min_distance = float('inf')
    closest_word = None

    # Special handling for common misreading of "Louisiana" due to cursive text
    # Commonly changed to arizona or Indiana as closest word
    if "IOW" in input_word.upper() or "ZINA" in input_word.upper():
        # Consider checking for more specific patterns if needed
        input_word = "LOUISIANA"  # Assume "Louisiana" if the pattern matches

    # Iterate through each word in the word list
    for word in word_list:
        # Ensure the length difference does not exceed 2 letters
        if abs(len(input_word) - len(word)) <= 2:
            # Calculate Levenshtein distance
            distance = lev.distance(input_word, word)
            # Update minimum distance and closest word if current word is closer
            if distance < min_distance:
                min_distance = distance
                closest_word = word

    return closest_word

# Example usage
input_word = "Iowzina"
closest_word = find_closest_word(input_word.upper(), listofstates)

print(f"The closest word to '{input_word}' is '{closest_word}'.")


