from flask import Flask
app = Flask(__name__)

from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os

#Root directory.
@app.route("/", methods = ['POST', 'GET'])
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
    #time = extractor.getTime()
    locations = extractor.getLocations()
    return date + hello()
def hello():
    return "<h1>Hello!</h1>"