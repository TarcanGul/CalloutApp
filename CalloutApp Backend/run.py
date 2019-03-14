from flask import Flask
from flask import jsonify, request, abort, url_for, redirect
app = Flask(__name__)

from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os
from werkzeug.utils import secure_filename

#This has to be a global path but this line makes it compatible with other server hosts.
UPLOAD_FOLDER = os.path.dirname(os.path.realpath(__file__)) + "\\upload"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

#Root directory.
@app.route("/")
def hello():
    return "<h1>Hello!</h1>"

@app.route("/model", methods=['POST'])
def getImageInput():
    image = request.files['image']
    filename = secure_filename(image.filename)

    #Images will appear in 'upload' folder
    #We can delete the file after we are done: to be implemented
    image.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

    #This method will return JSON
    return sendParsingInformation(filename)

def sendParsingInformation(image):
        
        dir_path = os.path.dirname(os.path.realpath(__file__))
         #We can put image instead of 'PandaFlyer'
        parsedText = pytesseract.image_to_string(Image.open(os.path.join(dir_path,'PandaFlyer.jpg')))
        parsedList = parsedText.split()
        #Creating extractor object
        extractor = InfoExtractor()
        extractor.extractWords(parsedList)
        
        date = extractor.getDate()
        time = "3 pm"
        locations = extractor.getLocations()

        return jsonify(date=date, time=time, locations=locations)

    
@app.route("/debug", methods=['GET'])
def printList():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    #This is where the file input will enter.
    #if not request.json or not 'image' in request.json:
        #   abort(400)
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(dir_path,'PandaFlyer.jpg')))
    parsedList = parsedText.split()
    return str(parsedList)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)