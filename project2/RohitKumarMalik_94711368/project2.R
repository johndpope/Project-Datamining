install.packages("MASS")
install.packages("datasets")
install.packages("arules")
install.packages("arulesViz")
install.packages("RWeka")

library(MASS)
library(arules)
library(arulesViz)
library(datasets)
library(RWeka)


set_seed<-function() {
  set.seed(9468)  
}
set_seed

datapath<-"//home//rkmalik//Desktop//allworkspaces//dm//project2//"

getRetailData<-function() {
  print("================Preparing Retail Data==============")
  retail<-read.arff(file=paste(normalizePath(datapath), "//" , "retail.arff", sep=""))
  ##retail<-read.arff(file="retail.arff")
  retail<-retail[2:15]
  return (retail);  
}

getGOTData<-function(){
  print("================Preparing Game Of Thrones Data==============")
  gdata<-read.arff(file=paste(normalizePath(datapath), "//" , "game_of_thrones.arff", sep=""))
  return (gdata);  
}


getTitanicData<-function(){
  print("================Preparing Titanic Data==============")
  titanicData<-read.arff(file=paste(normalizePath(datapath), "//" , "titanic.arff", sep=""))
  ##titanicData<-read.arff(file="titanic.arff")
  return (titanicData);  
}

#Printing all of the rules
myAllAssociationRulesMining<-function(mydata) {
  print("==============Preparig the all set of Rules=============")
  rules <- apriori(mydata)
  return (rules)
}

#Rules which meet the criteria (such as support and confidence)
myBetterAssociationRulesMining<-function(mydata, appearance) {
  print("==============Preparing the Better Rules=============")
  rules <- apriori(mydata, parameter=list(supp = 0.01, conf = 0.9), appearance)
  return (rules)
}


#Removing redundant rules
findRedundantRules<-function(rules) {
  subset.matrix <- is.subset(rules, rules)
  subset.matrix[lower.tri(subset.matrix, diag=T)] <- NA
  redundant <- colSums(subset.matrix, na.rm=T) >= 1
  return (redundant)
}

############################# Retail Dataset###############
retail<-getRetailData();
head(retail)

allreatailrules<-myAllAssociationRulesMining(retail)
inspect(allreatailrules[1:20])

betterretailrules<-myBetterAssociationRulesMining(retail, appearance = list(rhs=c("Beverage=0", "Beverage=1", "Meat=0","Meat=1", "PersonalCare=0", "PersonalCare=1"), default="lhs"))
inspect(betterretailrules[1:300])

redundntretailrules<-findRedundantRules(betterretailrules)
uniquebetterretailrules<- betterretailrules[!redundntretailrules]
inspect(uniquebetterretailrules)

# Sorting based on lift
sorteduniquebetterretailrules<-sort(uniquebetterretailrules, decreasing=TRUE, by = "lift")
inspect(sorteduniquebetterretailrules)

#################################### Game Of thrones##########################

gotdata<-getGOTData();
head(gotdata)

allgotrules<-myAllAssociationRulesMining(gotdata)
inspect(allgotrules[1:20])
inspect(allgotrules)

survivegotrules<-myBetterAssociationRulesMining(gotdata, appearance = list(rhs=c("Survives=1", "Survives=0"), default="lhs"))
inspect(survivegotrules[1:20])
inspect(survivegotrules)

redundantgotrules<-findRedundantRules(survivegotrules)
uniquebettergotrules<-survivegotrules[!redundantgotrules]
inspect(uniquebettergotrules)
uniquebettergotrules

# Sorting based on lift
sorteduniquebettergotrules<-sort(uniquebettergotrules, decreasing=TRUE, by = "lift")
inspect(sorteduniquebettergotrules)
sorteduniquebettergotrules

################## Titanic dataset########################

titanicdata<-getTitanicData();
head(titanicdata)

alltitanicrules<-myAllAssociationRulesMining(titanicdata)
inspect(alltitanicrules)

bettertitanicrules<-myBetterAssociationRulesMining(titanicdata, appearance = list(rhs=c("Survived=No", "Survived=Yes"), default="lhs"))
inspect(bettertitanicrules)

redundanttitanicrules<-findRedundantRules(bettertitanicrules)
uniquebettertitanicrules<-bettertitanicrules[!redundanttitanicrules]
uniquebettertitanicrules
inspect(uniquebettertitanicrules)

# Sorting based on lift
sorteduniquebettertitanicrules<-sort(uniquebettertitanicrules, decreasing=TRUE, by = "lift")
inspect(sorteduniquebettertitanicrules)

