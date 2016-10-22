#! /usr/bin/env python

import os
import random
import time
import random
from creds import *
import requests
import json
import re
from memcache import Client
from pydub import AudioSegment
import urllib


recorded = False
servers = ["127.0.0.1:11211"]
mc = Client(servers, debug=1)
path = os.path.realpath(__file__).rstrip(os.path.basename(__file__))



def internet_on():
    print "Checking Internet Connection"
    try:
        r =requests.get('https://api.amazon.com/auth/o2/token')
        print "Connection OK"
        return True
    except:
	print "Connection Failed"
    	return False

	
def gettoken():
	token = mc.get("access_token")
	refresh = refresh_token
	if token:
		return token
	elif refresh:
		payload = {"client_id" : Client_ID, "client_secret" : Client_Secret, "refresh_token" : refresh, "grant_type" : "refresh_token", }
		print payload
		url = "https://api.amazon.com/auth/o2/token"
		r = requests.post(url, data = payload)
		print r.text
		resp = json.loads(r.text)
		mc.set("access_token", resp['access_token'], 3570)
		return resp['access_token']
	else:
		return False
		

def alexa():

	url = 'https://access-alexa-na.amazon.com/v1/avs/speechrecognizer/recognize'
	headers = {'Authorization' : 'Bearer %s' % gettoken()}
	d = {
   		"messageHeader": {
       		"deviceContext": [
           		{
               		"name": "playbackState",
               		"namespace": "AudioPlayer",
               		"payload": {
                   		"streamId": "",
        			   	"offsetInMilliseconds": "0",
                   		"playerActivity": "IDLE"
               		}
           		}
       		]
		},
   		"messageBody": {
       		"profile": "alexa-close-talk",
       		"locale": "en-us",
       		"format": "audio/L16; rate=8000; channels=1"
   		}
	}
	with open(path+'recording.wav') as inf:
		files = [
				('file', ('request', json.dumps(d), 'application/json; charset=UTF-8')),
				('file', ('audio', inf, 'audio/L16; rate=8000; channels=1'))
				]	
		r = requests.post(url, headers=headers, files=files)
	if r.status_code == 200:
		for v in r.headers['content-type'].split(";"):
			if re.match('.*boundary.*', v):
				boundary =  v.split("=")[1]
		data = r.content.split(boundary)
		for d in data:
			if (len(d) >= 1024):
				audio = d.split('\r\n\r\n')[1].rstrip('--')
		with open(path+"response.mp3", 'wb') as f:
			f.write(audio)
		os.system('mpg123 -q {}response.mp3'.format(path))
	else:
		print "Bad Luck"		


'''Could record only 3gp in android.'''
def start():
	audio = "No Audio"
	while audio.find('No Audio') != -1:
		time.sleep(1)
		urllib.urlretrieve("Enter Website here",'recording.3gp')  # we keep requesting till we get a recording 
		audio = open('recording.3gp').read()
	AudioSegment.from_file('recording.3gp').export('recording.wav',format = 'wav')
	Inp=None
	alexa()

	

if __name__ == "__main__":

	while internet_on() == False:
		print "."
	token = gettoken()
	while True:
		start()

