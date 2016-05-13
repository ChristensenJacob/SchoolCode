# Jacob Christensen
# CS 3270

#Imports
import sys

#Constants

#Variables

#Functions
def badfile():
	print 'Incorrect file or no file specified'
	sys.exit(0)
	
def stripWSpace(input):
	input = input.replace("\r","")
	input = input.replace("\n","")
	input = input.replace(" ","")
	
	return input

#Classes
class Grid:

	def __init__ (self, path):
		file = open(path, "r")
		input = file.readline().upper()
		if not input:
			badfile()
		self.grid = []
		self.words = []
		self.found = []
		self.searched = False
		i = 0
		while len(input) > 1:
			self.grid.append(stripWSpace(input))
			i += 1
			input = file.readline().upper()
		input = file.readline().upper()
		i = 0
		while input:
			self.words.append(stripWSpace(input))
			self.found.append([-1,-1,-1,-1])
			input = file.readline().upper()
		self.gHeight = len(self.grid)
		self.gWidth = len(self.grid[0])
		
	def get(self, x, y):
		return self.grid[x][y]
	
	def printGrid(self):
		for line in self.grid:
			print line
			
	def printGridChars(self):
		for i in range(0, len(self.grid)):
			for j in range(0, len(self.grid[i])):
				print(self.grid[i][j]),
			print ''
				
	
	def printWords(self):
		if not self.searched:
			self.search()
		
		for i in range(0, len(self.words)):
			print (self.words[i]),
			if self.found[i][0] < 0:
				print(' not found')
			else:
				print(' found at start: ' + str(self.found[i][0]) + ',' + str(self.found[i][1]) + ' end: ' + str(self.found[i][2]) + ',' + str(self.found[i][3]))
	
	def search(self):
		for i in range(0, self.gHeight):
			for j in range(0, self.gWidth):
				for k in range(-1,2):
					for l in range(-1,2):
						if k != 0 or l != 0:
							#print('singlesearch(' + str(i) + ',' + str(j) + ',' + str(k) + ',' + str(l) + ')')
							self.singlesearch(i,j,k,l)
		self.searched = True
	
	def singlesearch(self, x, y, v, h):
		cX = x
		cY = y
		local = []
		n = 0
		keepGoing = True
		
		for i in range(0, len(self.found)):
			if self.found[i][0] < 0:
				local.append(True)
			else:
				local.append(False)
		
		while keepGoing:
			for i in range(0, len(self.words)):
				if local[i]:
					#print('checking position ' + str(n) + ' of ' + self.words[i]),
					#print('checking ' + self.grid[cX][cY] + ' and ' + self.words[i][n])
					if self.grid[cX][cY] != self.words[i][n]:
						local[i] = False
					elif len(self.words[i]) == (n + 1):
						local[i] = False
						self.found[i] = [x,y,cX,cY]
						continue
			
			cX += h
			cY += v
			if cX >= self.gWidth or cX < 0 or cY >= self.gHeight or cY < 0:
				keepGoing = False
				break
			
			keepGoing = False
			for i in range(0, len(local)):
				if local[i]:
					keepGoing = True
					
			n += 1
			
				
	
#==MAIN==
if len(sys.argv) != 2:
	badfile()

path = sys.argv[1]
grid = Grid(path)
print
grid.printGrid()
print
grid.printWords()
#print grid.grid
#print grid.words
#print grid.found