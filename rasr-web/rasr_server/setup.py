'''
Created on Feb 10, 2017

@author: bstaley
'''

from distutils.core import setup
import os

version = '1.0'


setup(name='rasr_server',
      version=version,
      install_requires=['flask',
                        'grpcio==1.1.0',
                        'PyAudio==0.2.10',
                        'grpc-google-cloud-speech-v1beta1==0.14.0',
                        'six==1.10.0',
                        'requests==2.13.0',
                        'google-auth==0.6.0',],
      description='''Test RASR python stream service.
                 ''',
      author='Bryan Staley',
      author_email='bryan.staley@ndpgroup.com',
      scripts=['app.wsgi', 'rasr_server.py'],
      packages=['rasr_routes'],
      package_dir={'rasr_routes': 'rasr_routes'},
      package_data={},
      )