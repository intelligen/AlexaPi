# Remote AlexaPi

# Forked form [AlexaPi](https://github.com/sammachin/AlexaPi) 
---
This is the code needed to Turn a Raspberry Pi into a client for Amazon's Alexa service, Feedback welcome.
---
 
### Requirements

You will need:
* A Raspberry Pi
* An SD Card with a fresh install of Raspbian (tested against build 2015-11-21 Jessie)
* An External Speaker with 3.5mm Jack

Next you need to obtain a set of credentials from Amazon to use the Alexa Voice service, login at http://developer.amazon.com and Goto Alexa then Alexa Voice Service
You need to create a new product type as a Device, for the ID use something like AlexaPi, create a new security profile and under the web settings allowed origins put http://localhost:5000 and as a return URL put http://localhost:5000/code you can also create URLs replacing localhost with the IP of your Pi  eg http://192.168.1.123:5000
Make a note of these credentials you will be asked for them during the install process

### Installation

Boot your fresh Pi and login to a command prompt as root.

Make sure you are in /root

Clone this repo to the Pi
`git clone https://github.com/vyasgiridhar/AlexaPi.git`
Run the setup script
`./setup.sh`

Setup the server and set the ip address of the server on the ap

Change the server ip in the android app in
`AudioReocorder/app/src/main/java/project/vyas/audiorecorder/MainActivity.java#189`

Build and install the android app.

### Issues/Bugs etc.

If your alexa isn't running on startup you can check /var/log/alexa.log for errrors.

You may need to adjust the volume and/or input gain for the microphone, you can do this with
`alsamixer`

### Advanced Install

For those of you that prefer to install the code manually or tweak things here's a few pointers...

The Amazon AVS credentials are stored in a file called creds.py which is used by auth_web.py and main.py, there is an example with blank values.

The auth_web.py is a simple web server to generate the refresh token via oAuth to the amazon users account, it then appends this to creds.py and displays it on the browser.

main.py is the 'main' alexa client it simply runs on a while True loop waiting for the server to return a audio.

The internet_on() routine is testing the connection to the Amazon auth server as I found that running the script on boot it was failing due to the network not being fully established so this will keep it retrying until it can make contact before getting the auth token.

The auth token is generated from the request_token the auth_token is then stored in a local memcache with and expiry of just under an hour to align with the validity at Amazon, if the function fails to get an access_token from memcache it will then request a new one from Amazon using the refresh token


 

