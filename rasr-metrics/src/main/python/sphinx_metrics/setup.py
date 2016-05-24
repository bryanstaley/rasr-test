from distutils.core import setup
import os

setup(name='rasr_metrics',
      version='1.0',
      description='''.
                 ''',
      author='Bryan Staley',
      author_email='bryan.staley@ndpgroup.com',
      scripts=['pipeline_generator.py'],
      packages=['pipeline_metrics',
                'metrics_app'],
      package_dir={'pipeline_metrics': 'pipeline_metrics',
                   'metrics_app':'metrics_app'}
)
