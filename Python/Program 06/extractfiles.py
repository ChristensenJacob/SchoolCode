# Jacob Christensen
# CS 3270

#Imports
import zipfile
import re
import os
import sys

#Constants

#Variables

#Functions

#Classes

#==MAIN==
if len(sys.argv) != 3:
	print 'Usage: extractfiles.py <zip file> <reg. expr>'
	sys.exit(0)
	
ziptarget = zipfile.ZipFile(sys.argv[1], 'r')
for file in ziptarget.namelist():
	if re.match(sys.argv[2], os.path.basename(file)):
		ziptarget.extract(file, sys.argv[1] + ' output')