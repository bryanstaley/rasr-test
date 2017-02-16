'''
Created on Feb 10, 2017

@author: bstaley
'''
import unittest

from rasr_routes import app, routes

class Test(unittest.TestCase):


    def setUp(self):
        self.test_client = app.test_client()


    def tearDown(self):
        pass


    def testStream(self):
        with open('/tmp/a.raw','rb') as audio_file:
            resp = self.test_client.post('/rasr-ws/rasr/control/42',data=audio_file.read())
            print resp

