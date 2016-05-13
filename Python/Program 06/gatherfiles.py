# Jacob Christensen
# CS 3270

#Imports
import sqlite3
import zipfile
import sys
import platform

#Constants
if platform.system() == 'Windows':
	separator = '\\'
else:
	separator = '/'

#Variables
extensions = []

#Functions

#Classes

#==MAIN==
if len(sys.argv) < 3:
	print 'Usage: gatherfiles.py <db> <extension> [ <extension> ...]'
	sys.exit(0)
	
db = sqlite3.connect(sys.argv[1])
query = db.cursor()
for i in range(2,len(sys.argv)):
	extensions.append(sys.argv[i])
	
for ext in extensions:
	result = query.execute('SELECT name,path FROM files WHERE extension=? GROUP BY name,path',('.' + ext,))
	#if len(result) <= 0:
	#	continue
	
	ziptarget = zipfile.ZipFile(ext + '.zip', 'a')
	for row in result:
		#print row #DEBUG
		ziptarget.write(row[1] + separator + row[0] + '.' + ext)
		
db.close()