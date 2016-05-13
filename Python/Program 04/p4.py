# Jacob Christensen
# CS 3270

#Imports

#Constants

#Variables
count = 0

#Functions
def fcount(f):
	global count
	count = 0
	def wrapper(*args, **args2):
		global count
		count += 1
		return f(*args, **args2)
	return wrapper
	
def produce(s):
	i = 0
	while i < len(s):
		c = s[i]
		if c.isdigit():
			if s[i + 1]:
				i += 1
				n = int(c)
				c = s[i]
				for j in range(n):
					c += str(s[i])
		
		yield c
		i += 1
		


#Classes
class fcount2:
	def __init__(self, f):
		self.count = 0
		self.f = f
		self.bCount = -1
		
	def __call__(self, *args, **args2):
		self.count += 1
		if self.bCount >= 0:
			self.bCount += 1
		return self.f(*args, **args2)
		
	def __enter__(self):
		self.bCount = 0
		return self
		
	def __exit__(self, *args, **args2):
		print "with block count =",self.bCount
		self.bCount = -1
		

#==MAIN==
#Part 1
@fcount
def f(n):
    return n+2

for n in range(5):
    print f(n)
    
print 'f count =',count

@fcount
def g(n):
    return n*n

print 'g count =',count
print g(3)
print 'g count =',count

#Part 2
print
@fcount2
def f(n):
    return n+2

for n in range(5):
    print f(n)   
print 'f count =',f.count

def foo(n):
    return n*n
    
with fcount2(foo) as g:
    print g(1)
    print g(2)
print 'g count =',g.count
print 'f count =',f.count

with fcount2(f) as g:
    print g(1)
    print g(2)
print 'g count =',g.count
print 'f count =',f.count

with f:
    print f(1)
    print g(2)
print 'g count =',g.count
print 'f count =',f.count

#Part 3
print
p = produce('A2B5E3426FG0ZYW3210PQ89R')
for s in p: print s,
print