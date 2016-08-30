import scipy.io
import numpy
import sys

mat = scipy.io.loadmat('data_essex_3600/data_essex_3600_x110.mat')
#print type(mat)
#print len(mat['horiz_data'][0])

for i in range(0,len(mat['horiz_data'])):
	line = ""
	for j in range(0,len(mat['horiz_data'][i])):
		line = line + str(mat['horiz_data'][i][j])+"\t"
	sys.stdout.write(line[:-1])
	print

