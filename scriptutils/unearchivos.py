from __future__ import division
import os
import sys
import paramiko
import time
import glob

populationSize = 1024
numberOfIslands = [32]
disjoint = ["true","false","none"]
dimension = [512,2048]
problems = ["zdt1","zdt2","zdt3","zdt6"]
numJobs = 30

for ni in numberOfIslands:
    for d in disjoint:
        for dim in dimension:
            for p in problems:
                for j in range(0,numJobs):
                    for islId in range(0,ni):   
                        islandFile = "job."+`j`+"."+`ni`+"_"+d+"_"+`dim`+"_"+p+"_id_"+`islId`+".front"
                        paretoFile = "job."+`j`+"."+`ni`+"_"+d+"_"+`dim`+"_"+p+"_front.stat"
                        print "opening "+islandFile+" to write in "+paretoFile
                        fisland = open(islandFile,"r")
                        try:
                            lines = fisland.readlines()
                            lines = [l for l in lines]
                            fglobal= open(paretoFile, "a")
                            try:
                                fglobal.writelines(lines)
                            finally:
                                fglobal.close()
                        finally:
                            fisland.close()
                    
                    
                    

