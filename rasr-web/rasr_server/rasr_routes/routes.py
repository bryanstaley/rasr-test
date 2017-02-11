'''
Created on Feb 10, 2017

@author: bstaley
'''

import logging
import signal
from flask import request

import google.auth
import google.auth.transport.grpc
import google.auth.transport.requests
from google.cloud.grpc.speech.v1beta1 import cloud_speech_pb2
from google.rpc import code_pb2
import grpc
import pyaudio
from six.moves import queue

from rasr_routes import app
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


def stream_from_file(request_stream,bytes_size):
    a = request_stream.read(bytes_size)
    
    while a and a > 0:
        yield a
        a = request_stream.read(bytes_size)


def request_stream(data_stream, rate, interim_results=True):
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

@app.route('/rasr/recognize', methods=['POST'])
def post_recognize_stream():
    
    req_stream_generator= stream_from_file(request.stream, 1024)
    requests = request_stream(req_stream_generator,16000)
    recognize_stream = service.StreamingRecognize(requests, DEADLINE_SECS)
    #signal.signal(signal.SIGINT, lambda *_: recognize_stream.cancel())
    try:
        (result,transcript) = handle_response(recognize_stream)
        
        http_response = {#'final_result':result,
                         'transcript':transcript}
        
        return make_response(json.dumps(http_response),200)

        recognize_stream.cancel()
    except grpc.RpcError as e:
        code = e.code()
        # CANCELLED is caused by the interrupt handler, which is expected.
        if code is not code.CANCELLED:
            raise
