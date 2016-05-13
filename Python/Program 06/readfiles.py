# Jacob Christensen
# CS 3270

#Imports
import sqlite3
import os
import sys

#Constants

#Variables

#Functions
def dbg(database): #DEBUG see what's actually in the database
	result = database.cursor().execute('SELECT * FROM files')
	for row in result:
		print row

#Classes

#==MAIN==
if len(sys.argv) != 3:
	print 'Usage: readfiles.py <directory> <db>'
	sys.exit(0)
	
db = sqlite3.connect(sys.argv[2])
query = db.cursor()
query.execute('CREATE TABLE IF NOT EXISTS files(name text, extension text, path text)')
for root, directories, files in os.walk(sys.argv[1]):
	for x in files:
		name, extension = os.path.splitext(x)
		if extension == "":
			extension = None
		if name[0] != '.':
			query.execute('INSERT INTO files VALUES (?,?,?)', (name, extension, root))
db.commit()
#dbg(db) #DEBUG
db.close()