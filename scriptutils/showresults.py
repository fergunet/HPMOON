from __future__ import division
import os
import sys
import paramiko
import time
import glob
from numpy import array
from numpy import genfromtxt
import numpy

numberOfIslands = ["8","16","32","64","128"]
disjoint = ["none","true","false","false_variable"]
dimension = "512"
problems = ["zdt1","zdt2","zdt3","zdt6"]
numJobs = 30
statistics = ["HV","SPREAD","igd","sols","gens"]

def getStats(columnF,stat):
    #print columnF+" " +stat
    if "false_variable" in columnF and ( columnF.startswith("8_") or columnF.startswith("16_") ):
        return[-1,-1]
    fisland = open(columnfile,"r")
    try:
        #nums = [[float(val) for val in line.split('\t')] for line in fisland.readlines()]
        #print nums
        #anums = array(nums)
        data = genfromtxt(columnF, dtype=float, delimiter='\t', names=True) 
        #print data
        #print type(data)
        #print data.shape
        #hv= numpy.mean(data[:]['HV'])
        #spread = numpy.mean(data[:]['SPREAD'])
        #igd = numpy.mean(data[:]['igd'])
        #sols = numpy.mean(data[:]['sols'])
        #gens = numpy.mean(data[:]['gens'])

        #hvstd= numpy.std(data[:]['HV'])
        #spreadstd = numpy.std(data[:]['SPREAD'])
        #igdstd = numpy.std(data[:]['igd'])
        #solsstd = numpy.std(data[:]['sols'])
        #gensstd = numpy.std(data[:]['gens'])
        #numpy.mean(data[0], axis=0)
        #return[hv,hvstd,spread,spreadstd,igd,igdstd,sols,solsstd,gens,gensstd]
        return[numpy.mean(data[:][stat]),numpy.std(data[:][stat])]
        
    finally:
        fisland.close()

def printAllStats(d,s):
    d['none'][s]

for p in problems:
    for ni in numberOfIslands:
        result = ""
        for s in statistics:
            for d in disjoint:
                columnfile = ni+"_"+d+"_"+dimension+"_"+p+".txt" #64_none_2048_zdt1.txt
                stats = getStats(columnfile,s)
                result = result+str(stats[0])+" "+str(stats[1])+" "
        print result


                        