import Templates
from flask import Flask, request, render_template, send_from_directory
from database import load_all_registered_from_db, add_register_to_db
import easyocr
import cv2
import numpy as np
import io
import os

app = Flask(__name__, template_folder='Templates')

# Initialize EasyOCR reader
reader = easyocr.Reader(['en'])


@app.route('/')
def index():
    return render_template('home.html')


@app.route('/database')
def database():
    data = load_all_registered_from_db()
    return render_template('database.html', data=data)


@app.route('/AddRegister', methods=['POST', 'GET'])
def register():
    if request.method == 'POST':
        data = request.form
        add_register_to_db(data)
    data = load_all_registered_from_db()
    return render_template('AddRegister.html',  data=data)


@app.route('/upload')
def upload():
    # Render the page with the upload form
    return render_template('upload.html')


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
                for detection in ocr_results:
                    if detection[2] > 0.2:  # Confidence threshold
                        top_left = tuple([int(val) for val in detection[0][0]])
                        bottom_right = tuple([int(val) for val in detection[0][2]])
                        text = detection[1]

                        # Calculate the size of the text box
                        font_scale = 0.4  # Adjust font scale as needed
                        thickness = 1  # Adjust thickness as needed
                        (text_width, text_height), baseline = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX,
                                                                              font_scale, thickness)

                        # Calculate the position for text background
                        bg_top_left = (top_left[0], top_left[1] - text_height - baseline - 3)
                        bg_bottom_right = (bg_top_left[0] + text_width + 6, top_left[1] + 3)

                        # Draw a filled rectangle as a background for text
                        img = cv2.rectangle(img, bg_top_left, bg_bottom_right, (0, 0, 0), cv2.FILLED)

                        # Draw the text
                        text_org = (top_left[0] + 3, top_left[1] - baseline)
                        img = cv2.putText(img, text, text_org, cv2.FONT_HERSHEY_SIMPLEX, font_scale, (255, 255, 255),
                                          thickness)

                        # Draw the bounding box
                        img = cv2.rectangle(img, top_left, bottom_right, (0, 255, 0), 2)

                # Save the processed image
                processed_image_path = 'static/processed_image.jpg'
                cv2.imwrite(processed_image_path, img)

                return render_template('results.html', image_path='processed_image.jpg')

    except Exception as e:
        print(f"An error occurred: {e}")
        return f"An error occurred: {e}"


@app.route('/static/<filename>')
def static_file(filename):
    return send_from_directory('static', filename)


if __name__ == '__main__':
    app.run(debug=True)
