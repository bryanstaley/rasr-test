
from flask import Flask

app = Flask(__name__)

from . import web_api
print 'done importing web_api'

@app.teardown_request
def shutdown_session(exception=None):
    _ = exception
