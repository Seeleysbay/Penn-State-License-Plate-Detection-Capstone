'''
Description:
Reads and filters the plate image uploaded on the flask website and blacks 
out the remaining image outside of the bounding boxes.
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
                processed_image_path = 'static/processed_image.jpg'
                cv2.imwrite(processed_image_path, result)

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
