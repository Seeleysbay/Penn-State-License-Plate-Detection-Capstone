'''
Description:
Reads and filters the plate image uploaded on the flask website while making a 
red box around the plate number and green boxes around additional text.
'''

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

                # Set a confidence threshold for OCR results
                confidence_threshold = 0.2

                # Initialize variables for the largest bounding box
                largest_area = 0
                largest_box = None
                plate_number = ""  # Variable to store the plate number

                # Find the largest bounding box
                for detection in ocr_results:
                    if detection[2] > confidence_threshold:
                        top_left = tuple([int(val) for val in detection[0][0]])
                        bottom_right = tuple([int(val) for val in detection[0][2]])
                        area = (bottom_right[0] - top_left[0]) * (bottom_right[1] - top_left[1])

                        if area > largest_area:
                            largest_area = area
                            largest_box = detection[0]
                            plate_number = detection[1]

                # Process each detection, draw rectangles and put text
                for detection in ocr_results:
                    if detection[2] > confidence_threshold:
                        top_left = tuple([int(val) for val in detection[0][0]])
                        bottom_right = tuple([int(val) for val in detection[0][2]])
                        text = detection[1]

                        # Check if it's the largest box
                        color = (0, 0, 255) if detection[0] == largest_box else (0, 255, 0)

                        # Draw rectangles around the text
                        cv2.rectangle(img, top_left, bottom_right, color, 2)

                        # Put text near the bounding box
                        cv2.putText(img, text, (top_left[0], top_left[1] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, color, 2)

                # Save the processed image
                processed_image_path = 'static/processed_image.jpg'
                cv2.imwrite(processed_image_path, img)

                # Find the state name
                all_texts = " ".join([detection[1] for detection in ocr_results]).strip().upper()
                state_name = find_state_name(all_texts)

                return render_template('results.html',
                                       image_path='processed_image.jpg',
                                       plate_number=plate_number,
                                       state=state_name if state_name else "No state found")

    except Exception as e:
        print(f"An error occurred: {e}")
        return f"An error occurred: {e}"
