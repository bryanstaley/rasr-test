import os

def application(environ, start_response):
    for key in ['GOOGLE_APPLICATION_CREDENTIALS']:
        print 'setting',key
        os.environ[key] = environ.get(key)
        if os.environ[key] is None:
                raise Exception("Environment %s does not exist" % key)

    from rasr_routes import app as _application
    return _application(environ, start_response)
