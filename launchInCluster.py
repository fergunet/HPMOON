from __future__ import division
import os
import sys

populationSize = 1024
numberOfIslands = [4]
#numberOfIslands = [8,32,128]
#disjoint = ["true","false","none"]
#dimension = [512,2048]
#problems = ["zdt1","zdt2","zdt3","zdt6"]
#DONT FORGET TIME!!!!! AND NUMJOBS

baseFile = "baseFileTimeIsland.params"
baseServerFileName = "baseServer.params"

serverIp = "localhost"
serverPort = ""
islandIdHeader = "isla"
jdk = "/home/pgarcia/jdk1.8.0_45/bin/java"


exch.num-islands = 3 
exch.island.0.id = isla0
exch.island.0.num-mig = 2
exch.island.0.mig.0 = isla1
exch.island.0.mig.1 = isla2


def generateServerFile(serverfilename, numIsls):
    with open(baseServerFileName) as f:
        lines = f.readlines()
        lines = [l for l in lines]
        with open(runfile, "w") as f1:
            f1.writelines(lines)
            f1.write("exch.server-addr = "+serverIp+"\n")
            f1.write("exch.server-port = "+serverPort+"\n")
            f1.write("exch.num-islands = "+`numIsls`+"\n");
            f1.write("\n")
            for i in range(0,numIsls):
                f1.write("exch.island."+i+".id = "+islandIdHeader+`i`+"\n");
                f1.write("exch.island."+i+".num-mig = "+`numIsls-1`+"\n");
                for o in range(0,numIsls):
                    if(o != i):
                        f1.write("exch.island."+i+".mig.+`o`+ = "+islandIdHeader+`i`+"\n");
                f1.write("\n");
                

#def keepLastLines(numLines):
    

for ni in numberOfIslands:
    serverFileName = "server"+`ni`+".stats"
    generateServerFile(serverFileName,numIsls)
    for d in disjoint:
        for dim in dimension:
            for p in problems:
                fileheader = `ni`+"_"+d+"_"+`dim`+"_"+p
                runfile = fileheader+"_.params"
                subpopsize = populationSize//ni
                mutationProb = 1/dim
                chunkSize = dim//ni
                if d == "true":
                    mutationProb = 1/chunkSize
                if d == "false":
                    mutationProb = 1/(chunkSize*3)
                with open(baseFile) as f:
                    lines = f.readlines()
                    lines = [l for l in lines]
                    with open(runfile, "w") as f1:
                        f1.writelines(lines)
                        f1.write("pop.subpops = "+`ni`+"\n");
                        f1.write("eval.problem.type = "+p+"\n");
                        f1.write("stat.front ="+fileheader+"_front.stat\n");
                        f1.write("stat.file ="+fileheader+"_salida.stat\n");
                        f1.write("pop.subpop.0.species.genome-size = "+`dim`+"\n");
                        f1.write("pop.subpop.0.species.mutation-prob = "+`mutationProb`+"\n");
                        f1.write("pop.subpop.0.species.pipe.disjoint = "+d+"\n");
                        f1.write("pop.subpop.0.species.pipe.source.0.disjoint = "+d+"\n");
                        if p == "zdt4":
                            f1.write("pop.subpop.0.species.min-gene = -5\n");	
                            f1.write("pop.subpop.0.species.max-gene = 5\n");
                            f1.write("pop.subpop.0.species.min-gene.0 = 0\n");
                            f1.write("pop.subpop.0.species.max-gene.0 = 1\n");
                        else:
                            f1.write("pop.subpop.0.species.min-gene = 0\n");	
                            f1.write("pop.subpop.0.species.max-gene = 1\n");                            
                        f1.write("AQUI VA LO DE HPMOON NUM ISLANDS Y ESO")
                os.system(jdk+" ec.Evolve -file "+runfile)
