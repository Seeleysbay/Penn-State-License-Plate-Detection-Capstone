'''
Description:
This code uses EasyOCR and OpenCV to make the bounding boxes and pull the text from the plate.
Once the bounding boxes are made the largest bounding box is identified and the remaining image 
outside of the boxes is blacked ouyt and deemed unecessary. Since the plate number will always be 
the largest boundng box on the plate the text from this box is used to obtain the plate number. Then 
all of the text found is stored in a string which we eventually use to find the state.

Potential Issues:
If there are errors in pulling the exact state name then the state name will not be found from the list of 50.
'''
