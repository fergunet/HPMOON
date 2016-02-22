tabla = read.table("8_none_512_zdt3.txt",head=T)
files <- list.files(pattern="*_false_2048*")
for(f in files) {
  table = read.table(f,head=T)
  summary(table)
  cat(f,mean(table$HV),sd(table$HV),mean(table$SPREAD),sd(table$SPREAD),mean(table$igd),sd(table$igd),mean(table$sols),sd(table$sols),mean(table$time),sd(table$time),mean(table$gens),sd(table$gens),'\n',sep="\t")
}
