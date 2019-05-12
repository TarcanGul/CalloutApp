from __future__ import print_function
import sys
from flask import Flask
from flask import jsonify, request, abort, url_for, redirect
from dataExtractor import InfoExtractor
from PIL import Image
import pytesseract 
import os
from dateTimeFilter import turnToDateTime
from werkzeug.utils import secure_filename
import datetime
import pickle
import os.path
from googleapiclient.discovery import build
from google.auth.transport.requests import Request
from google.oauth2 import id_token
from google.oauth2.credentials import Credentials
from google.auth.transport import requests
import json
import httplib2
from google_auth_oauthlib.flow import Flow
from google.auth.transport.requests import Request
from oauth2client import client

CLIENT_SECRET_FILE = 'client_secret.json'

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
    print("Image saved!", file=sys.stderr)
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
        time = extractor.getTime()
        locations = extractor.getLocations()
        print("Before jsonify", file=sys.stderr)
        os.remove(os.path.join(app.config['UPLOAD_FOLDER'], image))
        return jsonify(date=date, time=time, locations=locations)

    
@app.route("/debug", methods=['GET'])
def printList():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    parsedText = pytesseract.image_to_string(Image.open(os.path.join(dir_path,'PandaFlyer.jpg')))
    parsedList = parsedText.split()
    return str(parsedList)


@app.route("/calendar", methods=['GET', 'POST'])
def addEventToCalendar():
    if request.method == 'POST':
        """
        We are taking the id token to identify if we already have the credentials for the user. 
        """
        token = request.form['token']
        auth_code = request.form['authcode']
        
        try:
            # Specify the CLIENT_ID of the app that accesses the backend 
            with open(CLIENT_SECRET_FILE, 'r') as f:
                client_dict = json.load(f)

            client_id = client_dict["web"]["client_id"]
            print("Client ID:" + client_id, file=sys.stderr)
            idinfo = id_token.verify_oauth2_token(token, requests.Request(), client_id)
        
            if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
                raise ValueError('Wrong issuer.')

            # ID token is valid. Get the user's Google Account ID from the decoded token.
            userid = idinfo['sub']
            user_email = idinfo['email']
            print("userid: " + userid, file=sys.stderr)
            print("email: " + user_email, file=sys.stderr)
        except ValueError as e:
            # Invalid token
            print("Auth error" + str(e), file=sys.stderr)
            return "Value error"
         

        #Starting the flow. 
        flow = Flow.from_client_secrets_file(
            CLIENT_SECRET_FILE,
            scopes=SCOPES,
            redirect_uri='urn:ietf:wg:oauth:2.0:oob')

        #Fetching the code the client sends to us. 
        flow.fetch_token(code=auth_code)

        creds = None
                
        if os.path.exists('token.pickle'):
            with open('token.pickle', 'rb') as token:
                creds = pickle.load(token)
        if not creds or not creds.valid:
            if creds and creds.expired and creds.refresh_token:
                creds.refresh(Request())
            else:
                creds = flow.credentials
            # Save the credentials for the next run
            with open('token.pickle', 'wb') as token:
                pickle.dump(creds, token)
        
        
        print("Adding new event", file=sys.stderr)
        date = request.form['date']
        time = request.form['start_time']
        end_time = request.form['end_time']
        location = request.form['location']
        title = request.form['title']
        print("Date: " + date, file=sys.stderr)
        print("Time: " + time, file=sys.stderr)
        print("Location: " + location, file=sys.stderr)

        # Call the Calendar API
        datetime_request = turnToDateTime(date, time)
        datetime_end_request = turnToDateTime(date, end_time)
        
        insert_body = {
            "summary" : title,
            "start" : {
                "dateTime" : datetime_request,
                "timeZone" : "America/Indiana/Indianapolis",
                
            },
            "end" : {
                "dateTime" : datetime_end_request,
                "timeZone" : "America/Indiana/Indianapolis",
            },
            "location" : location
        }
        service = build('calendar', 'v3', credentials=creds)
        
        now = datetime.datetime.utcnow().isoformat() + "Z"
        print("Insert body:" + str(json.dumps(insert_body)), file=sys.stderr)
        events_add = service.events().insert(calendarId="primary", body=json.loads(str(json.dumps(insert_body)))).execute()
        print("Events add status: " + str(events_add), file=sys.stderr)
        events_result = service.events().list(calendarId="primary", timeMin=now,
                                            maxResults=10, singleEvents=True,
                                            orderBy='startTime').execute()
        events = events_result.get('items', [])

        if not events:
            print("No upcoming event", file=sys.stderr)
            return 'No upcoming events found.'
        print("Events: " + str(events), file=sys.stderr)
        print("Event added!", file=sys.stderr)
        return jsonify(events)
    else:
        return "This is a get request."


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000)