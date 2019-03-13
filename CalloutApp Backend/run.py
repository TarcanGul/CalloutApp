from flask import Flask
from flask import jsonify, request, abort
app = Flask(__name__)

from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os

#Root directory.
@app.route("/")
def hello():
    return "<h1>Hello!</h1>"

@app.route("/input", methods=['POST', 'GET'])
def getImageInput():
   #if not request.json or not 'image' in request.json:
   #     abort(400)
   var = request.form.get('image')
   if var == None:
       return "<h1> Bamboozled? Have a coke! </h1>"
   parsedText = pytesseract.image_to_string(Image.open(var))
   parsedList = parsedText.split()
   return "<img src=\"" + "\"" + var + "\" alt=\"Smiley face\" height=\"42\" width=\"42\">"


#Output api
@app.route("/output", methods=['GET'])
def sendParsingInformation():
    #Getting the path of the file so we can open an image file in the directory.
    if request.method == 'GET':
        dir_path = os.path.dirname(os.path.realpath(__file__))
        #This is where the file input will enter.
        #if not request.json or not 'image' in request.json:
         #   abort(400)
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