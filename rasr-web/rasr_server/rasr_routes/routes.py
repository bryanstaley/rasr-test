'''
Created on Feb 10, 2017

@author: bstaley
'''

import logging
import datetime
import socket
import os
from flask import request

import google.auth
import google.auth.transport.grpc
import google.auth.transport.requests
from google.cloud.grpc.speech.v1beta1 import cloud_speech_pb2
from google.rpc import code_pb2
import grpc
import pytz


from rasr_routes import app
from rasr_routes import transaction
from flask.helpers import make_response
import json


# set up a file logger
fh = logging.FileHandler('/tmp/rasr-server.log')
formatter = logging.Formatter('%(asctime)s - %(name)s: %(levelname)s %(message)s')
fh.setFormatter(formatter)
fh.setLevel(logging.DEBUG)
# make flask aware of it
app.logger.addHandler(fh)

# The Speech API has a streaming limit of 60 seconds of audio*, so keep the
# connection alive for that long, plus some more to give the API time to figure
# out the transcription.
# * https://g.co/cloud/speech/limits#content
DEADLINE_SECS = 60 * 3 + 5
SPEECH_SCOPE = 'https://www.googleapis.com/auth/cloud-platform'


AUDIO_ARCHIVE_PATH = '/tmp'
    

def handle_response(recognize_stream):
    for resp in recognize_stream:
        print resp.error
        print resp.error.code
        if resp.error.code != code_pb2.OK:
            raise RuntimeError('Server error: ' + resp.error.message)

        if not resp.results:
            continue
        
        # Display the top transcription
        result = resp.results[0]
        transcript = result.alternatives[0].transcript
        
        print 'Result {}'.format(result)
        print 'Transcript {}'.format(transcript)
        
    return result,transcript

def nlp_request(config='ziggis-revel',phrase=''):
    '''http://menu.rasr.io/order?config=ziggis-revel&phrase=skinny%20americano%20decaf'''
    import urllib2
    import urllib
    print 'google transcipt',phrase
    parms = {'config':config,
             'phrase':phrase}

    nlp_request_str = 'http://menu.rasr.io/order?{}'.format(urllib.urlencode(parms))
    nlp_request_str = nlp_request_str.replace('+','%20')
    print 'phrase',phrase
    print 'encoded parms',urllib.urlencode(parms)
    print 'nlp req',nlp_request_str
    return urllib2.urlopen(nlp_request_str).read()

def stream_from_file(request_stream,bytes_size):
    a = request_stream.read(bytes_size)
    
    while a and a > 0:
        yield a
        a = request_stream.read(bytes_size)


def request_stream(data_stream, rate, interim_results=True,archive_file_loc=None):
    """Yields `StreamingRecognizeRequest`s constructed from a recording audio
    stream.

    Args:
        data_stream: A generator that yields raw audio data to send.
        rate: The sampling rate in hertz.
        interim_results: Whether to return intermediate results, before the
            transcription is finalized.
    """
    # The initial request must contain metadata about the stream, so the
    # server knows how to interpret it.
    recognition_config = cloud_speech_pb2.RecognitionConfig(
        # There are a bunch of config options you can specify. See
        # https://goo.gl/KPZn97 for the full list.
        encoding='LINEAR16',  # raw 16-bit signed LE samples
        sample_rate=16000,  # the rate in hertz
        # See http://g.co/cloud/speech/docs/languages
        # for a list of supported languages.
        language_code='en-US',  # a BCP-47 language tag
    )
    streaming_config = cloud_speech_pb2.StreamingRecognitionConfig(
        interim_results=interim_results,
        config=recognition_config,
    )

    yield cloud_speech_pb2.StreamingRecognizeRequest(
        streaming_config=streaming_config)

    if archive_file_loc:
        with open(archive_file_loc,'wb+') as audio_archive:
            
            for data in data_stream:
                # Subsequent requests can all just have the content
                yield cloud_speech_pb2.StreamingRecognizeRequest(audio_content=data)
                # print ":".join("{:02x}".format(ord(c)) for c in data)
                audio_archive.write(data)
    else:
        for data in data_stream:
            # Subsequent requests can all just have the content
            yield cloud_speech_pb2.StreamingRecognizeRequest(audio_content=data)
            # print ":".join("{:02x}".format(ord(c)) for c in data)

def make_channel(host, port):
    """Creates a secure channel with auth credentials from the environment."""
    # Grab application default credentials from the environment
    credentials, _ = google.auth.default(scopes=[SPEECH_SCOPE])

    # Create a secure channel using the credentials.
    http_request = google.auth.transport.requests.Request()
    target = '{}:{}'.format(host, port)

    return google.auth.transport.grpc.secure_authorized_channel(
        credentials, http_request, target)

service = cloud_speech_pb2.SpeechStub(make_channel('speech.googleapis.com', 443))

#@app.route('/rasr/recognize', methods=['POST'])
@app.route('/rasr-ws/rasr/control/<string:nodeid>', methods=['POST'])
def post_recognize_stream(nodeid):
    
    archive= request.args.get('archive',True)
    archive_name = os.path.join(AUDIO_ARCHIVE_PATH,generate_archive_name(nodeid, ext='raw')) if archive != '0' else None
    req_stream_generator= stream_from_file(request.stream, 1024)
    requests = request_stream(req_stream_generator,16000,archive_file_loc=archive_name)
    recognize_stream = service.StreamingRecognize(requests, DEADLINE_SECS)
    #signal.signal(signal.SIGINT, lambda *_: recognize_stream.cancel())
    transaction_start_dt = datetime.datetime.utcnow()
    try:
        (result,transcript) = handle_response(recognize_stream)
        print result,transcript
        nlp_response = nlp_request(phrase=transcript)
        #nlp_response = {}
        rasr_response={}
        rasr_response['DM']={'prompts':[],
                             'variables':{},
                             'nodeId':nodeid,
                             'LP':json.loads(nlp_response),
                             'result':True,
                             'processedBytes':-1}
        #recognize_stream.cancel()
        transaction_end_dt = datetime.datetime.utcnow()

        try:
            transaction.record_transaction(siteId=nodeid, 
                                           start=transaction_start_dt, 
                                           end=transaction_end_dt, 
                                           audioQuality=None, 
                                           audioFormat='raw', 
                                           bitSize=16000, 
                                           clientIp='Unknown', 
                                           rasrInstanceHostname=socket.gethostname(), 
                                           audioFileName=archive_name, 
                                           audioFileLoc=AUDIO_ARCHIVE_PATH, 
                                           userAgent='Unknown')
            print 'foo'
        except Exception as e:
            print e
        return make_response(json.dumps(rasr_response),200)

        
    except grpc.RpcError as e:
        code = e.code()
        # CANCELLED is caused by the interrupt handler, which is expected.
        if code is not code.CANCELLED:
            raise

def generate_archive_name(nodeId,time=None,uid=None,ext=None):
    import uuid
    time = time or datetime.datetime.utcnow()
    uid = uid or uuid.uuid1()
    ext = ext or 'raw'
    
    return 'grpc-{0}-{1:%Y-%m-%dT%H:%M:%S}-{2}.{3}'.format(nodeId,time,uid,ext)
