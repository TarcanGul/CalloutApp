from flask import Flask
from flask import jsonify
app = Flask(__name__)

from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os

#Root directory.
@app.route("/")
def hello():
    return "<h1>Hello!</h1>"

#Output api
@app.route("/output")
def sendParsingInformation():
    #Getting the path of the file so we can open an image file in the directory.
    dir_path = os.path.dirname(os.path.realpath(__file__))
    #This is where the file input will enter.
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(dir_path,'PandaFlyer.jpg')))
    parsedList = parsedText.split()

    #Creating extractor object
    extractor = InfoExtractor()
    extractor.extractWords(parsedList)

    #Print the date(might be inaccurate)
    date = extractor.getDate()
    time = "3 pm"
    locations = extractor.getLocations()
    return jsonify(date=date, time=time, locations=locations)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)