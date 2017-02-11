import os

activate_this = '/usr/local/www/venv/bin/activate_this.py'
execfile(activate_this, dict(__file__=activate_this))

def application(environ, start_response):
    for key in ['']:
        os.environ[key] = environ.get(key)
        if os.environ[key] is None:
        	raise Exception("Environment %s does not exist" % key)

    from rasr_routes import app as _application

    return _application(environ, start_response)