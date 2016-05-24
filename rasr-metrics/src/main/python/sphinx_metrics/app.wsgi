import os
activate_this = '/usr/local/www/venvs/rasr_metrics/bin/activate_this.py'
execfile(activate_this, dict(__file__=activate_this))


def application(environ, start_response):

    print 'trying to setting'
    for key in ['MPLCONFIGDIR']:
        print 'setting',key
        os.environ[key] = environ.get(key)
        if os.environ[key] is None:
                raise Exception("Environment %s does not exist" % key)

    from metrics_app import app as _application
    return _application(environ, start_response)

