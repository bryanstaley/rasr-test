'''
Created on May 16, 2016

@author: bstaley
'''

import sys
import argparse
from pipeline_metrics import timeline


def parse_args(arguments):
    data=[]
    parser = argparse.ArgumentParser()
    parser.add_argument('-s','--start',
                        type=float,
                        default=0.0)
    parser.add_argument('-e','--end',
                        type=float,
                        default=100.0)
    parser.add_argument('-f','--file')
    parser.add_argument('-l','--latest',
                        type=int)
    parser.add_argument('-o','--out-file',
                        default='/tmp/a.png')
    
    return parser.parse_args(arguments)

if __name__ == '__main__':
    args = parse_args(sys.argv[1:])
    timeline.generate_figure(args.file,args.out_file, args.latest, args.start, args.end)