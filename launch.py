from __future__ import division
import os
import sys

populationSize = 1024
numberOfIslands = [8,32,128]
disjoint = ["true","false","none"]
dimension = [512,2048]
problems = ["zdt1","zdt2","zdt3","zdt6"]
#exch.subpop.0.select.size
#pop.subpop.0.size
#pop.subpops = 5
#eval.problem.type = zdt1
#stat.file =				$salida.stat
#stat.front =                            $paretofront.stat
#pop.subpop.0.species.genome-size = 11
#pop.subpop.0.species.mutation-prob = 1
#pop.subpop.0.species.pipe.disjoint = false
#pop.subpop.0.species.pipe.source.0.disjoint = none

baseFile = "baseFile.params"
jdk = "/home/pgarcia/jdk1.8.0_45/bin/java"

for ni in numberOfIslands:
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
                        for islandid in range(0,ni):
                            f1.write("exch.subpop."+`islandid`+".select.size = 2\n")
                            f1.write("pop.subpop."+`islandid`+".size ="+`subpopsize`+"\n")
                os.system(jdk+" ec.Evolve -file "+runfile)
                           