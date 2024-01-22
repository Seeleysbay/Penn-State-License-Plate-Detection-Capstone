import easyocr
import cv2

def read_license_plate(image_path):
    reader = easyocr.Reader(['en'])  # Initialize the EasyOCR reader
    image = cv2.imread(image_path)   # Read the image file
    results = reader.readtext(image) # Use EasyOCR to read text from the image

    plate_numbers = []
    for (bbox, text, prob) in results:
        plate_numbers.append(text)

    return plate_numbers

# Example usage
image_path = 'Michigan.webp'
plate_numbers = read_license_plate(image_path)
print("Recognized License Plate Numbers:", plate_numbers)
