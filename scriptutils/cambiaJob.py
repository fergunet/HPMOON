import glob
import os
import re

original = "23"
copiarDe = "24"



for filename in glob.iglob('job.'+original+'.32_none_2048_zdt3_*'):
     replaced = re.sub('job.'+original,'job.'+copiarDe, filename)
     print "Copiando contenido de " +replaced+ " a "+filename
     os.system("cp "+replaced+" "+filename)
