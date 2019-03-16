from __future__ import print_function
from flask import Flask
from flask import jsonify, request, abort, url_for, redirect
app = Flask(__name__)
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

# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/calendar.readonly']

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


def setupCalendar():
    """Shows basic usage of the Google Calendar API.
    Prints the start and name of the next 10 events on the user's calendar.
    """
    creds = None
    # The file token.pickle stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    # If there are no (valid) credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server()
        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    service = build('calendar', 'v3', credentials=creds)

    # Call the Calendar API
    now = datetime.datetime.utcnow().isoformat() + 'Z' # 'Z' indicates UTC time
    print('Getting the upcoming 10 events')
    events_result = service.events().list(calendarId='primary', timeMin=now,
                                        maxResults=10, singleEvents=True,
                                        orderBy='startTime').execute()
    events = events_result.get('items', [])

    if not events:
        print('No upcoming events found.')
    for event in events:
        start = event['start'].get('dateTime', event['start'].get('date'))
        print(start, event['summary'])


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)