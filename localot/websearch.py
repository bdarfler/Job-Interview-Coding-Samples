#! /usr/bin/env python
'''
Created on May 1, 2010

@author: Benjamin Darfler
'''

import urllib2, urllib, json, re

def search(term):
    return urllib2.urlopen('http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q='+urllib.quote(term)).read()

def print_results(json):
    results = json['responseData']['results']
    if results:
        result = results[0]
        print 'Top result\n'+ result['titleNoFormatting'] +' <' + result['url'] +'>'
    else:
        print "No results found"
    print '-------------------------'   

if __name__ == '__main__':
    try:
        print 'Welcome to search, powered by Google\n-------------------------'
        while True:
            term = raw_input("What would you like to search for?\n")
            if term == "":
                print "Searching for nothing is pretty boring"
                continue
            try:
                response = search(term)
            except:
                print "There was a problem connecting to Google"
                continue
            print_results(json.loads(response))
    except KeyboardInterrupt:
        print ''