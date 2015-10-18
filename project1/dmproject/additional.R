#install.packages("caret")
#install.packages("RWeka")
#install.packages("kknn")
#install.packages("oblique.tree")
#install.packages("e1071")

library(caret)
library(RWeka)
library(kknn)
library(oblique.tree)
library(e1071)

set.seed <- function(){
  set.seed(7162)      # My UFID is 7171-1662
}

lifeExpectancyData <- "C:\\Users\\bindu\\Desktop\\DMHomework\\Countries.csv"

read.countries.csv.file <- function(lifeExpectancyData){
  countries <- read.csv(file = lifeExpectancyData ,header = T, sep = ",")
  return (countries)
}

get.partition.countries.data <- function(countries){
  training.index <- createDataPartition(countries$Continent, p = .8, list = FALSE, times = 1)
  return (training.index)
}

get.countries.training.data <- function(training.index, countries){
  countries.training.data <- countries[training.index, ]
  return (countries.training.data)
}

get.countries.test.data <- function(training.index, countries){
  countries.test.data<- countries[-training.index, ]
  return (countries.test.data)
}

get.partition.iris.data <- function(){
  trainIndex <- createDataPartition(iris$Species, p = .8, list = FALSE, times = 1)
  return(trainIndex)
}

get.iris.training.data <- function(iris.trainIndex) {
  iris.training.data <- iris[iris.trainIndex, ]
  return (iris.training.data)
}

get.iris.test.data <- function(iris.trainIndex) {
  iris.test.data <- iris[ - iris.trainIndex, ]
  return (iris.test.data)
}


# Partition Life Expectancy DATA

partition.lifeExpectancy.data <- function(){
  countries <- read.csv(file = lifeExpectancyData ,header = T, sep = ",")
  training.index <- createDataPartition(countries$Continent, p = .8, list = FALSE, times = 1)
  countries.training.data <- countries[training.index, ]
  countries.test.data <- countries[ - training.index, ]
  return (list(countries.training.data, countries.test.data))
}

# Partition IRIS DATA
partition.iris.data <- function(){
  trainIndex <- createDataPartition(iris$Species, p = .8, list = FALSE, times = 1)
  return (trainIndex)
}


# TRAIN IRIS DATA

c45.irisData.train <- function(iris.train.data) {
  irisFit <- J48(Species~., data = iris.train.data)
  return (irisFit)
}

ripper.irisData.train <- function(iris.train.data) {
  irisJRipFit <- train(iris.train.data[,1:4], iris.train.data[,5], method = "JRip", tuneLength = 17)
  return (irisJRipFit)
}

oblique.irisData.train <- function(iris.train.data) {
  irisOblique <- oblique.tree(formula = Species~., data = iris.train.data, split.impurity = "gini", oblique.splits = "on")
  return (irisOblique)
}

naive.bayes.irisData.train <- function(iris.train.data) {
  irisnaiveBayes <- naiveBayes(iris.train.data[,1:4], iris.train.data[,5])
  return (irisnaiveBayes)
}

k.nearest.neighbots.irisData.train <- function(iris.train.data, iris.test.data) {
  irisKnnIrisModel <- kknn(formula = formula(Species~.), train = iris.train.data, test = iris.test.data, k = 7, distance = 1)
  return (irisKnnIrisModel)
}

# TRAIN LIFE-EXPECTANCY DATASET

c45.countriesData.train <- function(lifeExpectancyData.train.data) {
  lifeExpectancyfit <- J48(Continent~. , data = lifeExpectancyData.train.data)
  return (lifeExpectancyfit)
}

ripper.countriesData.train <- function(lifeExpectancyData.train.data) {
  lifeExpectancyJRipFit <- train(lifeExpectancyData.train.data[,1:7], lifeExpectancyData.train.data[,8], method = "JRip", tuneLength = 17)
  return (lifeExpectancyJRipFit)
}

oblique.countriesData.train <- function(lifeExpectancyData.train.data) {
  lifeExpectancyData.train.data <- lifeExpectancyData.train.data[, 3:8]
  lifeExpectancyOblique <- oblique.tree(formula = Continent~., data = lifeExpectancyData.train.data, split.impurity = "gini", oblique.splits = "only")
  return (lifeExpectancyOblique)
}

naive.bayes.countriesData.train <- function(lifeExpectancyData.train.data) {
  lifeExpectancyNaiveBayes <- naiveBayes(lifeExpectancyData.train.data[, 1:7], lifeExpectancyData.train.data[, 8])
  return(lifeExpectancyNaiveBayes)
}

k.nearest.neighbots.countriesData.train <- function(lifeExpectancyData.train.data, lifeExpectancyData.train.test) {
  lifeExpectancyKnn <- kknn(formula = formula(Continent~.), train = lifeExpectancyData.train.data, test = lifeExpectancyData.train.test, k = 7, distance = 1)
  return(lifeExpectancyKnn)
}

# TESTING TEST-DATASETS FOR THE IRIS & LIFE EXPECANCY DATASET

classification.data.test <- function(test.model, test.data) {
  answers <- predict(test.model, test.data)
  return (answers)
}

k.nearest.neighbors.data.test <- function(knn.test.model) {
  knnAnswer <- fitted(knn.test.model)
  return (knnAnswer)
}

oblique.tree.test <- function(oblique.tree.model, test.data){
  prediction <- predict(oblique.tree.model, test.data)
  answerVector = c()
  len = nrow(prediction)
  for(i in 1:len){
    getValue <- which.max(prediction[i,])
    answerVector[i] <- getValue
  }
  cnames = colnames(prediction)[answerVector[]]
  return (cnames)
}

# DRIVER FUNCTION

driver.function <-function(){
  irisIndex <- get.partition.iris.data()
  iris.Training.data <- get.iris.training.data(irisIndex)
  iris.Testing.data <- get.iris.test.data(irisIndex)
  
  #IRIS EXECUTIONS
  
  
  #C45 
  print("C45 - IRIS : Confusion Matrix")
  C45irisModel<-c45.irisData.train(iris.Training.data)
  plot(C45irisModel)
  print(table(classification.data.test(C45irisModel, iris.Testing.data), iris.Testing.data[,5]))
  
  #RIPPER
  print("RIPPER - IRIS : Confusion Matrix")
  ripperirisModel <- ripper.irisData.train(iris.Training.data)
  plot(ripperirisModel)
  print(table(classification.data.test(ripperirisModel, iris.Testing.data), iris.Testing.data[,5]))
  
  #OBLIQUE
  print("OBLIQUE - IRIS : Confusion Matrix")
  obliqueirisModel <- oblique.irisData.train(iris.Training.data)
  obliqueirisModel
  print(table(oblique.tree.test(obliqueirisModel, iris.Testing.data), iris.Testing.data[,5]))
  
  #NAIVE-BAYES
  print("NAIVE-BAYES - IRIS : Confusion Matrix")
  naiveBayesModel <- naive.bayes.irisData.train(iris.Training.data)
  # plot(naiveBayesModel)
  print(table(classification.data.test(naiveBayesModel, iris.Testing.data), iris.Testing.data[, 5]))
  windows(pairs(iris.Training.data[1:4], main = "Iris TrainingData", pch = 21, bg = c("red", "green3", "blue") [unclass(iris.Training.data$Species)]))
  
  #KNN Model
  print("KNN-MODEL - IRIS : Confusion Matrix")
  train.con <- train.kknn(Species~., data = iris.Training.data, kmax = 50, kernel = c("triangular"))
  windows(plot(train.con))
  knnModel <- k.nearest.neighbots.irisData.train(iris.Training.data, iris.Testing.data)
  print(table(classification.data.test(knnModel, iris.Testing.data), iris.Testing.data[,5]))
  
  #COUNTRIES DATA MANIPULATION 
  countries <- read.countries.csv.file(lifeExpectancyData)
  countries.TrainIndex <- get.partition.countries.data(countries)
  countries.test.data <- get.countries.test.data(countries.TrainIndex, countries)
  countries.train.data <- get.countries.training.data(countries.TrainIndex, countries)
  
  #C45
  print("C45 - Countries : Confusion Matrix")
  c45.lifespan.model <- c45.countriesData.train(countries.train.data)
  windows(plot(c45.lifespan.model))
  c45.lifespan.answer <- classification.data.test(c45.lifespan.model, countries.test.data)
  c45.lifespan.confusion.matrix <- table(c45.lifespan.answer, countries.test.data$Continent)  
  print(c45.lifespan.confusion.matrix)
  
  #RIPPER
  print("Ripper - Countries : Confusion Matrix")
  ripper.countries.model <- ripper.countriesData.train(countries.train.data)
  ripper.lifespan.answer <- classification.data.test(ripper.countries.model, countries.test.data)
  ripper.lifespan.confusion.matrix <- table(ripper.lifespan.answer, countries.test.data$Continent)  
  print(ripper.lifespan.confusion.matrix)
  windows(plot(ripper.countries.model))
  
  #OBLIQUE
  print("Oblique Tree Classifier - Countries : Confusion Matrix")
  oblique.countries.model <- oblique.countriesData.train(countries.train.data)
  oblique.lifespan.answer <- oblique.tree.test(oblique.countries.model, countries.test.data[,3:8])
  oblique.lifespan.confusion.matrix <- table(oblique.lifespan.answer, countries.test.data$Continent)
  plot(oblique.lifespan.confusion.matrix)
  print(oblique.lifespan.confusion.matrix)
  windows(plot(oblique.countries.model))
  
  #NAIVEBAYES
  print("Naive Bayes Classifier - Countries : Confusion Matrix")
  naive.bayes.countries.model <- naive.bayes.countriesData.train(countries.train.data)
  naive.bayes.lifespan.answer <- classification.data.test(naive.bayes.countries.model, countries.test.data)
  naive.bayes.lifespan.confusion.matrix <- table(naive.bayes.lifespan.answer, countries.test.data$Continent)  
  print(naive.bayes.lifespan.confusion.matrix)
  # windows(plot(naive.bayes.countries.model))
  windows(pairs(countries.train.data[3:7], main = "Countries Data", pch = 21, bg = c("red", "green3", "blue") [unclass(countries.train.data[,8])]))
  
  #KNN
  print("k-nearest Neighbor - Countries : Confusion Matrix")
  knn.countries.model <- k.nearest.neighbots.countriesData.train(countries.train.data, countries.test.data)
  knn.lifespan.answer <- classification.data.test(knn.countries.model)
  knn.lifespan.confusion.matrix <- table(knn.lifespan.answer, countries.test.data$Continent)
  print(knn.lifespan.confusion.matrix)
  windows(plot(knn.countries.model))
}