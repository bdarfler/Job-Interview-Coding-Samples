#! /usr/bin/env python
'''
Created on May 1, 2010

@author: Benjamin Darfler
'''

import getopt, sys

def usage():
    print 'Usage: filesearch.py'
    print '\t-h, --help\tDisplay this help and exit.'
    print '\t-i, --input\tSet the input file.\tdefault: input.txt' 
    print '\t-p, --patterns\tSet the patterns file.\tdefault: patterns.txt'
    print '\t-m, --mode\tSet the operation mode.\tdefault: 1'
    print '\t\t\tMode 1 outputs any line that exactly matches any of the patterns'
    print '\t\t\tMode 2 outputs any line that contains any of the patterns'
    print '\t\t\tMode 3 outputs any line that is at most one character away from any of the patterns'

def read_opts(argv):
    try:                                
        opts, args = getopt.getopt(argv, 'hi:p:m:', ['help', 'input=', 'patterns=','mode=']) 
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(1)
    return opts

def get_mode(arg):
    try:
        mode = int(arg)
    except ValueError:
        print 'Mode must be 1, 2 or 3'
        sys.exit()
    if mode not in (1,2,3):
        print 'Mode must be 1, 2 or 3'
        sys.exit()
    return mode

def parse_opts(opts):
    input = 'input.txt'
    patterns = 'patterns.txt'
    mode = 1
    for opt, arg in opts:
        if opt in ('-h', '--help'):
            usage()
            sys.exit()
        elif opt in ('-i', '--input'):
            input = arg
        elif opt in ('-p', '--patterns'):
            patterns = arg
        elif opt in ('-m', '--mode'):
            mode = get_mode(arg)
    return input, patterns, mode

def open_file(path):
    try:
        file = open(path)
    except IOError:
        print path+' does not exist'
        sys.exit(1)
    return file

def mode_one(input_path, patterns_path):
    patterns_file = open_file(patterns_path)
    patterns = set([])
    for line in patterns_file:
        patterns.add(line.strip())
    input_file = open_file(input_path)
    for line in input_file:
        if line.strip() in patterns:
            print line
            
def mode_two(input_path, patterns_path):
    patterns_file = open_file(patterns_path)
    patterns = []
    for line in patterns_file:
        patterns.append(line.strip())
    input_file = open_file(input_path)
    for line in input_file:
        stripped = line.strip()
        for pattern in patterns:
            if stripped.find(pattern) is not -1:
                print stripped

def mode_three(input_path, patterns_path):
    patterns_file = open_file(patterns_path)
    patterns = []
    for line in patterns_file:
        patterns.append(line.strip())
    input_file = open_file(input_path)
    for line in input_file:
        stripped = line.strip()
        for pattern in patterns:
            if lev(stripped, pattern) <= 1:
                print stripped

# http://antoniocangiano.com/2009/05/18/memoization-in-ruby-and-python/
def memoize(function):
    cache = {}
    def decorated_function(*args):
        if args in cache:
            return cache[args]
        else:
            val = function(*args)
            cache[args] = val
            return val
    return decorated_function

# http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Python
@memoize
def lev(a, b):
    if not a: return len(b)
    if not b: return len(a)
    return min(lev(a[1:], b[1:])+(a[0] != b[0]), lev(a[1:], b)+1, lev(a, b[1:])+1)

if __name__ == '__main__':
    opts = read_opts(sys.argv[1:])
    input, patterns, mode = parse_opts(opts)
    if mode is 1:
        mode_one(input, patterns)
    elif mode is 2:
        mode_two(input, patterns)
    elif mode is 3:
        mode_three(input, patterns)