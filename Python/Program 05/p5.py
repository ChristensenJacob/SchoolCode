# Jacob Christensen
# CS 3270

#Imports
import json
import sys

#Constants

#Variables

#Functions

#Classes	
class ShoppingList:
	def __init__(self, jspath, htmlpath):
		self.pathJS = jspath
		self.pathHTML = htmlpath
		jsonFile = json.load(open(jspath))
		self.storeOrder = jsonFile['storeOrder']
		self.itemList = jsonFile['itemList']
		self.filteredList = [x for x in self.itemList if x['status'] == 0]
		#self.filteredList = sorted(self.filteredList, key=lambda k: k['name'])
		#Turns out alphabetical sorting isn't required for this project, I put it in out of habit
		tempList = []
		for x in self.storeOrder:
			for y in self.filteredList:
				if x == y['section']:
					tempList.append(y)
		self.filteredList = tempList
		
	def writeHTML(self):
		out = open(self.pathHTML, 'w')
		out.write('<html><table>\n')
		for x in self.filteredList:
			out.write('<tr><td>')
			out.write(x['name'])
			out.write('</td><td>')
			out.write(x['amountStr'])
			out.write('</td><td>')
			out.write(x['section'])
			out.write('</td><td>')
			if x['notes']:
				out.write(x['notes'])
			else:
				out.write('--')
			out.write('</td></tr>\n')
		out.write('</table></html>')
		out.close()		
		

#==MAIN==
if len(sys.argv) < 3:
	print("Insufficient Arguments (2 expected)")
	sys.exit(0)
	
shoplist = ShoppingList(sys.argv[1], sys.argv[2])

#for x in shoplist.itemList:
#	print x['name'], x['section']

#print shoplist.storeOrder
#for x in shoplist.filteredList:
#	print x['name'], x['section']
	
shoplist.writeHTML()