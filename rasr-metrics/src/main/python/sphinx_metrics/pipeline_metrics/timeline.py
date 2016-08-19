'''
Created on May 13, 2016

@author: bstaley
'''
import os
import matplotlib
try:
    matplotlib.use("Qt4Agg")
except Exception:
    print "Unable to run with Qt4App backend..Try 'sudo yum install PyQt4' and run again.  Interactive plots not available (hint: use the -o option)!"
    matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from collections import namedtuple
import numpy as np

DataTuple = namedtuple("DataTuple",['source','record_time','num_calls','sample_time'])

color_map = {}
color_map['Feature Transform']='peachpuff'
color_map['Feature Extraction']='indigo'
color_map['Windower']='green'
color_map['Speech Marker']='tan'
color_map['Preemphzsizer']='lightpink'
color_map['Dither']='lime'
color_map['Speech Classifier']='skyblue'
color_map['Stream Data Source']='cyan'
color_map['Dct2']='red'
color_map['Mel Filter Bank']='darkorange'
color_map['Fft']='forestgreen'
color_map['Grow']='mediumvioletred'
color_map['Score']='darkred'
color_map['Prune']='deepskyblue'

def get_color(in_type):
    from matplotlib.colors import rgb2hex
    
    if in_type not in color_map:
        color_map[in_type]=rgb2hex(np.random.rand(3,1))
    return color_map[in_type]


def parse_csv_file(csv_data_file,start_offset,end_offset):
    with open(csv_data_file,'r') as csv_file:
        return parse_csv_data(csv_file, start_offset, end_offset)
 
def parse_csv_data(csv_data,start_offset,end_offset):     
    import csv
    results = []
    data_reader = csv.reader(csv_data,
                             delimiter=',')
        
    for r in data_reader:
        results.append(DataTuple(source=r[1],
                                 record_time=int(r[0]),
                                 num_calls=int(r[2]),
                                 sample_time=float(r[3])))
        
    #fix max/min
    filtered_results = []
    
    if not start_offset and not end_offset:
        return results
    
    times = [x.record_time for x in results]
    max_time = max(times)
    min_time = min(times)
    for r in results:
        if not start_offset or r.record_time >= min_time + start_offset:
            if not end_offset or r.record_time <= max_time - end_offset:
                filtered_results.append(r)
        
    return filtered_results

def get_sources(data):
    return list(set([a[0] for a in data]))
def get_idx(sources,in_type):
    return sources.index(in_type)
def get_min_max(data):
    l = [x.record_time for x in data]
    return min(l),max(l)

def get_legend_data(data):
    return [mpatches.Patch(color=get_color(a),label=a) for a in list(set([a[0] for a in data]))]

def get_latest_data_file(from_file,offset_from_end):
    _, base = os.path.split(from_file)
    matching_files = []
    for d in os.listdir(dir):
        if base in d:
            matching_files.append(d)
    matching_files.sort()
    print from_file,matching_files
    return os.path.join(dir,matching_files[-offset_from_end])

def generate_figure_from_csv(in_csv_data,
                             to_file,
                             start_offset=None,
                             end_offset=None):
    
    data = parse_csv_data(in_csv_data, start_offset, end_offset)
    return generate_figure_from_data(data, to_file, start_offset, end_offset)

def generate_figure_from_data(in_data,
                              to_file,
                              start_offset=None,
                              end_offset=None):
    plt.figure(figsize=(30,10))
    #plt.subplot(111)
    
    sources = get_sources(in_data)
    
     
    labels = []
    for d in in_data:
        data_type = d.source
        
        if d.source not in labels:
            plt.barh(bottom=get_idx(sources,data_type),
                     label=d.source,
                     width=d.sample_time,
                     left=d.record_time,
                     color=get_color(data_type),)
            labels.append(d.source)
        else:
            plt.barh(bottom=get_idx(sources,data_type),
                     width=d.sample_time,
                     left=d.record_time,
                     color=get_color(data_type),)
    
    plt.yticks(xrange(len(sources)),sources)
    
    _min,_max = get_min_max(in_data)
    plt.xticks(np.arange(_min,_max,(_max-_min)/5),np.arange(_min,_max,(_max-_min)/5))
    plt.legend(#handles=get_legend_data(in_data),
               bbox_to_anchor=(0., 1.02, 1., .102),
               loc=3,ncol=9,mode='expand'
           )
    
    if to_file:
        plt.savefig(to_file,
                    format='png')
    else:
        plt.show()
    
def generate_figure(from_file,
                    to_file,
                    latest=None,
                    start_offset=None,
                    end_offset=None,):
    
    if latest:
        f = get_latest_data_file(from_file,latest)
        data = parse_csv_file(csv_data_file=f,
                              start_offset=start_offset,
                              end_offset=end_offset)
    else:
        data = parse_csv_file(csv_data_file=from_file,
                              start_offset=start_offset,
                              end_offset=end_offset)

    return generate_figure_from_data(in_data=data, 
                                     to_file=to_file,
                                     start_offset=start_offset,
                                     end_offset=end_offset)
