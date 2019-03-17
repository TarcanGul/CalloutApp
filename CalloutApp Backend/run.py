from __future__ import print_function
import sys
from flask import Flask
from flask import jsonify, request, abort, url_for, redirect
from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os
from werkzeug.utils import secure_filename
import datetime
import pickle
import os.path
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from google.oauth2 import id_token
from google.auth.transport import requests

CLIENT_ID = '659045646039-5l58ktdmt7n5ca6879pec7jmdibp9of2.apps.googleusercontent.com'
app = Flask(__name__)
# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/calendar.events']

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

@app.route("/calendar/<token>", methods=['POST'])
def addEventToCalendar(token):
    """Shows basic usage of the Google Calendar API.
    Prints the start and name of the next 10 events on the user's calendar.
    """
    try:
        # Specify the CLIENT_ID of the app that accesses the backend:
        idinfo = id_token.verify_oauth2_token(token, requests.Request(), CLIENT_ID)

        # Or, if multiple clients access the backend server:
        # idinfo = id_token.verify_oauth2_token(token, requests.Request())
        # if idinfo['aud'] not in [CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]:
        #     raise ValueError('Could not verify audience.')
    
        if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
           raise ValueError('Wrong issuer.')

        #If auth request is from a G Suite domain:
        # if idinfo['hd'] != GSUITE_DOMAIN_NAME:
        #     raise ValueError('Wrong hosted domain.')

        # ID token is valid. Get the user's Google Account ID from the decoded token.
        userid = idinfo['sub']
    except ValueError:
        # Invalid token
        print("Value error", file=sys.stderr)
        return "Value error"
    creds = None
    # The file token.pickle stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)

    service = build('calendar', 'v3', credentials=creds)

    # Call the Calendar API
    now = datetime.datetime.utcnow().isoformat() + 'Z' # 'Z' indicates UTC time
    events_result = service.events().list(calendarId='primary', timeMin=now,
                                        maxResults=10, singleEvents=True,
                                        orderBy='startTime').execute()
    events = events_result.get('items', [])

    if not events:
        print("No upcoming event", file=sys.stderr)
        return 'No upcoming events found.'
    print(events, file=sys.stderr)
    return events


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)