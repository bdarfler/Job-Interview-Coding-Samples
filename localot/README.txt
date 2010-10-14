WEBSEARCH
	
	DESCRIPTION
		
		This application executes a search against Google using the user supplied search term(s).
		
	RATIONAL
	
		This application was implemented as a Python shell script for ease of coding, reading and execution.
		The Google ajax api was used for its large market share and its easy to process json result.
	
	HOW TO RUN
		
		The websearch application can be run from the commandline via './websearch.py'.
		After that the interactive prompts should guide you.
		
	REQUIREMENTS
	
		Python 2.6+
		
FILESEARCH

	DESCRIPTION
	
		This application allows searching an input file for lines that match any patterns in a given patterns file.
		
	RATIONAL
	
		This application was implemented as a Python shell script for ease of coding, reading and execution.
		Memoization was used to improve the performance of the recursive method call for calculating the Levenshtein distance.
		Methods taken from online sources have been cited and the idea for memoization came from http://www.cs.colorado.edu/~martin/csci5832/edit-dist-blurb.html.

	HOW TO RUN
	
		The filesearch application can be run from the commmandline via './filesearch.py'
		The help flag can be specified via './filesearch.py --help' for further information.
		
	REQUIREMENTS
	
		Python 2.6+
		
PYTHON

	If you are lucky enough to be running OS X 10.6 then you have python 2.6+ installed.
	If not then follow the instructions at http://python.org/download/ 