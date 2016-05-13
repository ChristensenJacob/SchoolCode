# Jacob Christensen
# CS 3270

#Imports
import sys
import itertools
import random
import bisect

#Constants
wordspath = 'words.txt'

#Variables
WORDSIZ = 6
MINSIZ = 3
words = []
for i in range(0, (WORDSIZ + 1)): #words[0] is all words, formerly wordsA[]
	words.append([])
found = []
endcommands = ['q', 'Q']


#Functions
def printwords():
	for i in range(MINSIZ, (WORDSIZ + 1)):
		temp = list(set(words[i]) - set(found))
		print (str(len(temp)) + ' ' + str(i) + '-letter words left')
		temp = list(set(words[i]) & set(found))
		print(temp)
	
def allwords(input, siz=1):
	print ('allwords baseWord = ' + input + ' n: ' + str(siz))
	s = []
	for i in range(int(siz), (len(input) + 1)):	
		p = itertools.permutations(input, i)
		for x in p:
			xs = ''.join(x)
			if xs not in s and xs in words[0]:
				bisect.insort(s, xs)
	
	s.sort(key = len)
	print s

def prepareLists(input, siz=1):
	s = []
	for i in range(int(siz), (len(input) + 1)):	
		if i > WORDSIZ:
			break
		p = itertools.permutations(input, i)
		for x in p:
			xs = ''.join(x)
			if len(xs) >= MINSIZ and xs not in s and xs in words[0]:
				bisect.insort(s, xs)
				bisect.insort(words[i], xs)
	words[0] = s
	
def removeDups(input):
    output = set()
    out_add = output.add
    return [ x for x in input if not (x in output or out_add(x))]
	
def testVictory():
	for x in words[0]:
		if x not in found:
			return False
	return True
			
	

#Classes

#==MAIN==
wordsfile = file(wordspath)
line = wordsfile.readline()
while line:
	line = line.strip()
	bisect.insort(words[0], line)
	if len(line) == WORDSIZ:
		words[WORDSIZ].append(line)
	line = wordsfile.readline()
	
if len(sys.argv) == 2:
	baseword = sys.argv[1]
elif len(sys.argv) == 3:
	allwords(sys.argv[1], sys.argv[2])
	sys.exit(0)
else:
	baseword = ''.join(random.sample(words[WORDSIZ], 1))
	
print ('TEST: base word is ' + baseword + '\n')

words[WORDSIZ] = []
scramword = ''.join(random.sample(baseword, len(baseword)))
keepGoing = True
prepareLists(baseword)
while (keepGoing):
	print (scramword + ':\n')
	printwords()
	input = sys.stdin.readline().strip()
	if input in endcommands:
		keepGoing = False
		break
	elif input not in found and input in words[0]:
		found.append(input)
		print ('Correct!\n')
		if testVictory():
			break
	else:
		print ('Incorrect - Not in dictionary or already guessed\n')
if keepGoing:
	print ('\nCONGRATULATIONS, YOU WIN!!')
else:
	print ('\nBetter luck next time!')