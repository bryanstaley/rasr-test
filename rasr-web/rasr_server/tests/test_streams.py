'''
Created on Feb 14, 2017

@author: bstaley
'''
import unittest
import io
from io import SEEK_END, SEEK_CUR, SEEK_SET


class Test(unittest.TestCase):


    def setUp(self):
        pass


    def tearDown(self):
        pass


    def testStreams(self):
        sz = 10000
        bio = io.BytesIO()
        rw_bio = io.BufferedRandom(bio)
        rw_bio.write('foo')
        
        
        res = rw_bio.read(2)
        while res and len(res) > 0:
            print res
            z = rw_bio.write('aa')
            res = rw_bio.read(2)
            