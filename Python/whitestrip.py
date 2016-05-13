import sys

def stripWSpace(input):
	input = "".join(input.split())
	
	return input

path = sys.argv[1]
file = open(path, 'r')
outfile = open ('out.txt', 'w')
input = file.readline()
while input:
	outfile.write(stripWSpace(input))
	outfile.write('\n')
	input = file.readline()