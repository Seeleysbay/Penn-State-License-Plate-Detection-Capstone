'''
Description: 
This code uses EasyOCR and OpenCV to read the text from a webcam
'''

import cv2
import easyocr

# Initialize EasyOCR reader for English language
reader = easyocr.Reader(['en'])

# Initialize webcam
cap = cv2.VideoCapture(0)

# Set a confidence threshold for OCR results
confidence_threshold = 0.2

while True:
    # Capture frame-by-frame
    ret, frame = cap.read()

    # Check if frame is captured successfully
    if not ret:
        print("Failed to grab frame")
        break

    # Perform OCR on the captured frame
    ocr_results = reader.readtext(frame)

    # Process each detection
    for detection in ocr_results:
        if detection[2] > confidence_threshold:
            top_left = tuple([int(value) for value in detection[0][0]])
            bottom_right = tuple([int(value) for value in detection[0][2]])
            text = detection[1]

            # Draw rectangles and text
            frame = cv2.rectangle(frame, top_left, bottom_right, (0, 255, 0), 2)
            (text_width, text_height), _ = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX, 0.6, 1)
            text_offset_x = max(top_left[0], 0)
            text_offset_y = max(top_left[1] - text_height - 10, 0)
            frame = cv2.rectangle(frame, (text_offset_x, text_offset_y),
                                  (text_offset_x + text_width, text_offset_y + text_height + 10),
                                  (0, 255, 0), -1)
            frame = cv2.putText(frame, text, (text_offset_x, text_offset_y + text_height + 2),
                                cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)

    # Display the frame with annotations
    cv2.imshow('Frame', frame)

    # Break the loop when 'q' key is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
