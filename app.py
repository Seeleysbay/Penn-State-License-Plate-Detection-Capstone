from flask import Flask, request, render_template, send_from_directory, redirect, url_for
import easyocr
import cv2
import numpy as np
import io
import os
from datetime import datetime

app = Flask(__name__)

# Initialize EasyOCR reader
reader = easyocr.Reader(['en'])


@app.route('/')
def index():
    return render_template('home.html', title='Home', current_year=datetime.now().year)


@app.route('/database')
def database():
    # Placeholder for the database page
    return render_template('database.html')


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

@app.route('/upload')
def upload():
    # Get the processed image and other details if they are present in the query parameters
    processed_image = request.args.get('processed_image', None)
    plate_number = request.args.get('plate_number', None)
    state_name = request.args.get('state', None)

    # Render the page with the upload form and include the processed image if it's present
    return render_template('upload.html', processed_image=processed_image,
                           plate_number=plate_number, state_name=state_name)



@app.route('/uploader', methods=['POST'])
def upload_file():
    try:
        if request.method == 'POST':
            file = request.files['image']
            if file:
                in_memory_file = io.BytesIO()
                file.save(in_memory_file)
                data = np.frombuffer(in_memory_file.getvalue(), dtype=np.uint8)
                img = cv2.imdecode(data, cv2.IMREAD_COLOR)

                if img is None:
                    raise ValueError("Image decoding failed.")

                # Perform OCR and process the image
                ocr_results = reader.readtext(img, detail=1)

                # Initialize variables for the largest bounding box
                largest_area = 0
                plate_number = ""  # Variable to store the plate number
                mask = np.zeros_like(img)  # Create a black mask of the same size as the image

                # Find the largest bounding box and fill the mask
                for detection in ocr_results:
                    if detection[2] > 0.2:  # Confidence threshold
                        top_left = tuple([int(val) for val in detection[0][0]])
                        bottom_right = tuple([int(val) for val in detection[0][2]])
                        area = (bottom_right[0] - top_left[0]) * (bottom_right[1] - top_left[1])

                        if area > largest_area:
                            largest_area = area
                            plate_number = detection[1]
                            cv2.rectangle(mask, top_left, bottom_right, (255, 255, 255), -1)

                # Apply the mask to the original image
                result = cv2.bitwise_and(img, mask)

                # Save the processed image
                processed_image_path = 'processed_image.jpg'
                cv2.imwrite(os.path.join('static', processed_image_path), result)

                # Find the state name
                all_texts = " ".join([detection[1] for detection in ocr_results]).strip().upper()
                state_name = find_state_name(all_texts)

                # Redirect to the upload page with the processed image path
                # and any additional information you want to display
                return redirect(url_for('upload', processed_image=processed_image_path,
                                        plate_number=plate_number,
                                        state=state_name if state_name else "No state found"))

    except Exception as e:
        print(f"An error occurred: {e}")
        return f"An error occurred: {e}"



@app.route('/static/<filename>')
def static_file(filename):
    return send_from_directory('static', filename)


if __name__ == '__main__':
    app.run(debug=True)
