# CalloutApp
Take a photo of a callout poster from your Android phone and the app will add the event to your Google Calendar. Based on https://github.com/Sid-V/Boilermake2018

<h3>How it works?</h3>
<p>CalloutApp is a Android client app which is working with a Python Flask server in the backend to get basic information about an event callout poster taken via the camera. The basic information returned via the algorithm in the server is date, starting time, ending time and location. Location is handled via a MySQL table which manually setup. The MySQL database stores names of the places in a particular city.This database is then used to compare the strings we get from the image and deciding possible names for the place. Thus, it is a list, and user is able to choose from the possible names. After the user being happy with the event information, he can send the event to his Google Calendar directly. After the user is authorized with his Google account, the event will appear in the calendar automatically! OAuth2 Auth Code protocol is used to authorize the server to use the user's calendar.</p>

<h3>Showcase</h3>
<p>Here is the camera screen. Since I don't have an Android phone right now, I am using my app via emulator and in emulator the default behavior for camera screen is a generated 3D Graphics simulation. </p>
<img src="http://i67.tinypic.com/90sif8.jpg" width="50%" height="500"> 
<img src="http://i67.tinypic.com/5tzayu.jpg" width="50%" height="500">

<p>There is no other good way to test the app via emulator camera other than changing which image the server processes manually. So I did a manual testing with this particular event:</p>
<img src="http://i64.tinypic.com/dgj8fa.jpg" width="50%" height="500">

<p>After clicking "Parse and Send to Google Calendar", we sent the image to a server. Server is right now is just hosted in a local environment, in the future deployment is needed.</p> 
<img src="http://i66.tinypic.com/33ypanc.jpg" width="50%" height="500">

<p>After waiting, we get this screen prefilled:</p>
<img src="http://i65.tinypic.com/rvc360.jpg" width="50%" height="500">

<p>Title is now added manually by the user. Now the event is ready to be sent. You can also choose to not have a title and send it directly.</p>
<img src="http://i68.tinypic.com/k365hw.jpg" width="50%" height="500">

<p>Here is the event added in the user's account:</p>
<img src="http://i67.tinypic.com/15znr84.jpg" width="50%" height="50%">
