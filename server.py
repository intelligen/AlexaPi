#! /usr/bin/env python

from flask import Flask
from flask import request
import os
app = Flask(__name__)
x=0

'''This is a really crude server that i wrote up to transfer audio from my phone to the Pi. '''

@app.route('/upload', methods = ['POST'])
def upload_message():
    global x
    print(request.headers['Content-Type'])
    if request.headers['Content-Type'].find('media/mpeg') != -1:
        x+=1
        f = open(str(x), 'wb')
        f.write(request.data)
        f.close()
        return "Binary message written!"
    else:
        return "415 Unsupported Media Type ;)"

@app.route('/download',methods = ['GET'])
def download_message():
    global x
    if x >= 1:
        request.data = open(str(x),'rb').read()
        x-=1
        request.status_code = 200
        return request.data
    else:
        return "No Audio"
@app.route('/',methods = ['GET'])
def Hello():
    return "Hello"
if __name__ == '__main__':
    global x
    x=0
    app.run()
