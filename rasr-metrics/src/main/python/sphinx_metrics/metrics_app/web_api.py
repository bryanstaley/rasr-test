'''
Created on May 16, 2016

@author: bstaley
'''

from flask import request, Response,send_file
import time
from pipeline_metrics import timeline
from StringIO import StringIO
import tempfile
from . import app


@app.route('/rasr/metrics.png',methods=['GET'])
def get_metrics():
    http_headers = {}
    http_headers['Content-Type'] = 'image/png'
    latest = get_query_parameter('latest',request.query_string,None)
    start = get_query_parameter('start', request.query_string, None)
    end = get_query_parameter('end', request.query_string, None)
    file_pattern = get_query_parameter('file_pattern',request.query_string, '/tmp/sphinx4-results.csv')
#     if group_pattern is None:
#         return Response('group was not provided! Query String: %' % request.query_string,400,http_headers)=

    result_png = tempfile.NamedTemporaryFile()
    
    try:
        timeline.generate_figure(file_pattern,
                                 result_png.name,
                                 latest if latest is None else int(latest),
                                 start if start is None else int(start),
                                 end if end is None else int(end))
    except Exception as e:
        import traceback
        traceback.print_exc()
        return Response(e,status=500)
    
    return send_file(filename_or_fp=result_png.name, 
                     mimetype='image/png',
                     as_attachment=True, 
                     add_etags=False,
                     attachment_filename='rasr-pipeline-metrics-%s.png'%time.time())


@app.route('/generate/metrics.png',methods=['POST'])
def get_provided_metrics():
    start = get_query_parameter('start', request.query_string, None)
    end = get_query_parameter('end', request.query_string, None)
    data = StringIO(request.data)
    result_png = tempfile.NamedTemporaryFile()    
    try:
        timeline.generate_figure_from_csv(in_csv_data=data,
                                          to_file=result_png.name,
                                          start_offset=start if start is None else int(start),
                                          end_offset=end if end is None else int(end))
    except Exception as e:
        import traceback
        traceback.print_exc()
        
    return send_file(filename_or_fp=result_png.name, 
                     mimetype='image/png',
                     as_attachment=True, 
                     add_etags=False,
                     attachment_filename='rasr-pipeline-metrics-%s.png'%time.time())
    
    
    


@app.errorhandler(500)
def internal_error(error):
    app.logger.exception(error)
    return "Error: %s" % str(error), 500
  
def get_query_parameter(param,query_string,def_value=None):
    if query_string is None or len(query_string) == 0:
        return def_value
    vals = query_string.split('&')

    query_dict = {}
    for parm in vals:
        if len(parm.split('=')) != 2:
            print 'skipping query parm %s, malformed!' % parm 
            continue
        paramater,value=parm.split('=')
        if paramater in query_dict:
            print '%s already in dict!!'
        query_dict[paramater] = value
        
    if param in query_dict:
        return query_dict[param]
    return def_value