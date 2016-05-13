#Jacob Christensen
#CS 3270
#Imports
import collections #for ordered dictionary

#Constants
pathTerr = "territories.txt"
pathStat = "gameState.txt"

#Variables
territories = dict()
players = dict() # Using a dictionary instead of list for players to keep player numbers consistent
maxArms = 0
maxArmsPlayer = ''
maxTerrs = 0
maxTerrsPlayer = ''
continents = set()
contConquer = dict()

#Classes
class Territory:
	'A Risk-esque territory'
	def __init__ (self, terrName, terrKey, terrGroup):
		self.name = terrName
		self.key = terrKey
		self.group = terrGroup
		self.owner = '0'
	def __str__ (self):
		return self.name
	def setOwner (self, newOwn):
		if self.owner != '0':
			players[self.owner].remTerr(self)
		self.owner = newOwn
		players[newOwn].addTerr(self)
	def setArmies (self, newArm):
		self.armies = int(newArm)
		
class Player:
	'Controller of territories'
	def __init__ (self, idnum):
		self.id = idnum
		self.armies = 0
		self.myTerrs = dict()
	def __str__ (self):
		return 'Player ' + self.id
	def addArmies(self, newArm):
		self.armies += newArm
	def addTerr(self, newTerr):
		self.myTerrs[newTerr.key] = newTerr
	def remTerr(self, oldTerr):
		del self.myTerrs[oldTerr.key]

#----	
#MAIN
#----
file = open(pathTerr, "r")
line = file.readline()
while line:
	linSplit = line.split(',')
	inTerrName = linSplit[0]
	inTerrKey = linSplit[1]
	inTerrGroup = linSplit[2].rstrip('\n')
	territories[inTerrKey] = Territory(inTerrName, inTerrKey, inTerrGroup)
	line = file.readline()
file.close()

file = open(pathStat, "r")
line = file.readline()
while line:
	linSplit = line.split(',')
	inTerrKey = linSplit[0]
	inPlayID = linSplit[1]
	inPlayArm = linSplit[2]
	if inPlayID not in players:
		players[inPlayID] = Player(inPlayID)
	if inTerrKey in territories:
		territories[inTerrKey].setOwner(inPlayID)
		territories[inTerrKey].setArmies(inPlayArm)
		players[inPlayID].addArmies(territories[inTerrKey].armies)
	line = file.readline()
file.close()

#continent conquer calculations. I'm sure there's a better way to do this, but this works :L
for key in territories:
	continents.add(territories[key].group)
	
continents = list(continents)
		
for cont in continents:
	contTemp = set()
	for key in territories:
		if territories[key].group == cont:
			contTemp.add(territories[key].owner)
	if len(contTemp) == 1:
		contTemp = list(contTemp)
		contConquer[cont] = contTemp[0]

players = collections.OrderedDict(sorted(players.items()))
			

print 'Total number of territories: ' + str(len(territories))

for key in players:
	if players[key].armies > maxArms:
		maxArms = players[key].armies
		maxArmsPlayer = players[key].id
	elif players[key].armies == maxArms:
		maxArmsPlayer += ', ' + players[key].id
	if len(players[key].myTerrs) > maxTerrs:
		maxTerrs = len(players[key].myTerrs)
		maxTerrsPlayer = players[key].id
	elif len(players[key].myTerrs) == maxTerrs:
		maxTerrsPlayer += ', ' + players[key].id
	print '\nPlayer ' + players[key].id
	print 'Has ' + str(players[key].armies) + ' armies'
	print 'Owns ' + str(len(players[key].myTerrs)) + ' territories'
	terrs = 'Names of territories: '
	for k in players[key].myTerrs:
		terrs += players[key].myTerrs[k].name
		terrs += ', '
	terrs = terrs[:-2]
	print terrs
	for cont in contConquer:
		if contConquer[cont] == players[key].id:
			print 'Owns all territories in ' + cont
	
print '\nPlayer ' + maxArmsPlayer + ' has the most armies (' + str(maxArms) + ')'
print 'Player ' + maxTerrsPlayer + ' owns the most territories (' + str(maxTerrs) + ')'
#for key in territories:
#	print territories[key]
