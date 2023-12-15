from flask import Flask, render_template, request, redirect, url_for, send_from_directory
import easyocr
import cv2
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)


UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(file_path)
            return redirect(url_for('result', filename=filename))
    return render_template('upload.html')

@app.route('/uploads/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

@app.route('/result/<filename>')
def result(filename):
    image_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    reader = easyocr.Reader(['en'])
    image = cv2.imread(image_path)
    results = reader.readtext(image)
    plate_numbers = ' '.join([text[1] for text in results])
    return render_template('result.html', plate_numbers=plate_numbers, filename=filename)

if __name__ == '__main__':
    app.run(debug=True)
