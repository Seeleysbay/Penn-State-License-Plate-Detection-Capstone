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

        
