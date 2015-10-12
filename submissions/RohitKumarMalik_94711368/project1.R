#install.packages("caret")
#install.packages("datasets")
#install.packages("rpart")
#install.packages("klaR")
#install.packages("RWeka")
#install.packages("MASS")
#install.packages("e1071")
#install.packages("ggplot2")
#install.packages("tree")
#install.packages("party")
#install.packages("oblique.tree")
#install.packages("partykit")


library(caret)
library(datasets)
library(rpart)
library(klaR)
library(RWeka)
library(MASS)
library(e1071)
library(ggplot2)
library(tree)
library(party)
library(oblique.tree)
library(partykit)

set_seed<-function() {
  set.seed(9468)  
}

set_seed
data("iris")

print("Preparing the LifeExpectency Dataset")
lifeexpectency = read.csv("life_expectancy.csv", header = T, sep = ",")
head(iris)
head(lifeexpectency)


print("Partition Iris dataset into training dataset and the test dataset")
iris_train_Index <- createDataPartition(iris$Species, p=0.80, list=FALSE)
iris_train_data <- iris[iris_train_Index,]
iris_test_data <- iris[-iris_train_Index,]

print("Partition LifeExpectency dataset into training dataset and the test dataset")
lifeexpectency_train_index <- createDataPartition(lifeexpectency$Continent, p = .8, list = FALSE,times = 1)
lifeexpectency_train_data <- lifeexpectency[lifeexpectency_train_index,]
lifeexpectency_test_data <- lifeexpectency[-lifeexpectency_train_index,]

#################################### RIPPER##########################
#testInd <- iris_test_data[ ,!colnames(iris_test_data) %in% "Species"]
#testDep <- as.factor(iris_test_data[, names(iris_test_data) == "Species"]) 

train_data <- iris_train_data[,1:4]
train_classes <- iris_train_data[,5]
train_Jrip_fit_model <- train (train_data, train_classes, method = "JRip", tuneLength = 17)
plot(main="RIPPER Classification Iris Dataset",train_Jrip_fit_model)
predictions <- predict(train_Jrip_fit_model, iris_test_data)
##Prints the confusion Matrix
print (table (predictions, iris_test_data$Species))
##Prints the Confusion Matrix and overall Statisics
confusionMatrix(predictions, iris_test_data$Species)

####################################### C4.5##########################

iris_c45_fit_model <- J48(Species~., data = iris.train.data)
plot (main="C4.5 Classification Iris Dataset",iris_c45_fit_model)
summary(iris.fit)
iris_c45_predictions <- predict(iris_c45_fit_model, iris_test_data)
print(table(iris_c45_predictions, iris_test_data$Species))
confusionMatrix(iris_c45_predictions, iris_test_data$Species)


####################################### Oblique Tree ###########

iris_oblique_tree_model <- oblique.tree(formula = Species~., data = iris_train_data, split.impurity = "gini", oblique.splits = "on")
iris_oblique_tree_model
iris_oblique_tree_predictions <- function (iris_oblique_tree_model, iris_test_data) {
  predictions <- predict(iris_oblique_tree_model, iris_test_data)
  answers <- c()
  pred_rows = nrow (predictions)
  for (i in 1:pred_rows) {
    maxValue <- which.max(predictions[i,]) 
    answers[i] <- maxValue
  }
  columnnames = colnames(predictions)[answers[]]
  return (columnnames)
}

print(table(iris_oblique_tree_predictions(iris_oblique_tree_model, iris_test_data), iris_test_data$Species))
confusionMatrix(table(iris_oblique_tree_predictions(iris_oblique_tree_model, iris_test_data), iris_test_data$Species))
##How to do a plot in oblique tree
plot(iris_oblique_tree_model)


########################################## NAIVE BIAS########

#iris_naivebias_model<-NaiveBayes(Species~., data=iris_train_data)
#iris_naivebias_model
iris_naivebias_model<-naiveBayes(iris.train.data[,1:4], iris.train.data[,5])
iris_naivebias_model
iris_nb_predictions <- predict(iris_naivebias_model, iris_test_data[,1:4])
iris_nb_predictions
print(table(iris_nb_predictions, iris_test_data$Species))
confusionMatrix(table(iris_nb_predictions, iris_test_data$Species))
pairs(iris_train_data, main = "Iris Data", pch = 21, bg = c("red", "green3", "blue") [unclass(iris_train_data$Species)])


################KNN##########


library(class)
library(kknn)
train_con_iris <- train.kknn(Species~., data = iris_train_data, kmax = 50, kernel = c("triangular"))
plot(main="K Nearest Neighbours Iris",train_con_iris)
iris_knn <- knn(train=subset(iris, select = -Species), test=subset(iris, select = -Species), cl=iris$Species, k = 3, prob=TRUE)
iris_knn_model<-kknn(train = iris_train_data, test = iris_test_data, formula = formula(Species~.), k = 7, distance = 1)
iris_knn_predictions <-predict(iris_knn_model, iris_test_data[,1:4])
print(table(iris_knn_predictions, iris_test_data$Species))
confusionMatrix(table(iris_knn_predictions, iris_test_data$Species))


###################RIPPER

lifeexpectency
lifeexpectency_test_data_nonclasses<-lifeexpectency_test_data[,1:7]
lifeexpectency_test_data_classes<-lifeexpectency_test_data[,8]
lifeexpectency_test_data_nonclasses
lifeexpectency_test_data_classes

lifeexpectency_train_data_nonclasses<-lifeexpectency_train_data[,1:7]
lifeexpectency_train_data_classes<-lifeexpectency_train_data[,8]
lifeexpectency_train_data_nonclasses
lifeexpectency_train_data_classes


lifeexpectency_Jrip_fit_model<-train(lifeexpectency_train_data_nonclasses, lifeexpectency_train_data_classes, method = "JRip", tuneLength = 17)
plot(lifeexpectency_Jrip_fit_model, main="Life Expectency RIPPER Classification")
lifeexpectency_ripper_predictions<-predict(lifeexpectency_Jrip_fit_model, lifeexpectency_test_data)
print (table (lifeexpectency_ripper_predictions, lifeexpectency_test_data_classes))
confusionMatrix(lifeexpectency_ripper_predictions, lifeexpectency_test_data_classes)

##############C4.5#########
lifeexpectency_fit_model <- J48(Continent~., data=lifeexpectency_train_data)
###paryKit is not working in my machine need to test with other machine
plot(lifeexpectency_fit_model)
summary(lifeexpectency_fit_model)
lifeexpectency_c45_predictions<-predict(lifeexpectency_fit_model, lifeexpectency_test_data)
table(lifeexpectency_c45_predictions, lifeexpectency_test_data_classes)
confusionMatrix(lifeexpectency_c45_predictions, lifeexpectency_test_data_classes)

###############Oblique Tree#########
lifeexpectency_train_data<-lifeexpectency_train_data[, 3:8]
life_expectency_obtree_model=oblique.tree(formula = Continent~., data=lifeexpectency_train_data, split.impurity = "gini", oblique.splits = "only")
plot(life_expectency_obtree_model)

lifeexpectency_oblique_tree_predictions_func<-function (model, life_test_data) {
  predictions <- predict(model, life_test_data)
  answers <- c()
  pred_rows = nrow (predictions)
  for (i in 1:pred_rows) {
    maxValue <- which.max(predictions[i,]) 
    answers[i] <- maxValue
  }
  columnnames = colnames(predictions)[answers[]]
  return (columnnames)
}
lifeexpectency_oblique_tree_predictions<-lifeexpectency_oblique_tree_predictions_func(life_expectency_obtree_model, lifeexpectency_test_data[, 3:8])
##Now plot based on the predictions
print(table(lifeexpectency_oblique_tree_predictions, lifeexpectency_test_data_classes))
lifeexpectency_oblique_tree_predictions
confusionMatrix(lifeexpectency_oblique_tree_predictions, lifeexpectency_test_data_classes)


############Naive Bias###############################
  
### Partitioning the data again as I have changed the lifeexpectency_train_data for oblique tree,
### Lets reload it again

print("Partition LifeExpectency dataset into training dataset and the test dataset")
lifeexpectency_train_index<-createDataPartition(lifeexpectency$Continent, p = .8, list = FALSE,times = 1)
lifeexpectency_train_data<-lifeexpectency[lifeexpectency_train_index,]
lifeexpectency_test_data<-lifeexpectency[-lifeexpectency_train_index,]


life_expectency_nb_model<-naiveBayes(lifeexpectency_train_data[, 1:7], lifeexpectency_train_data[, 8])
life_expectency_nb_model
life_expectatency_nbb_predictions<-predict(life_expectency_nb_model, lifeexpectency_test_data)
table(life_expectatency_nbb_predictions, lifeexpectency_test_data$Continent) 
confusionMatrix(life_expectatency_nbb_predictions, lifeexpectency_test_data$Continent)
pairs(lifeexpectency_train_data, main = "Life Expectency based on Test Data", pch = 21, bg = c("red", "green3", "blue") [unclass(lifeexpectency_train_data$Continent)])


######################KNN##########################
library(kknn)
train_con_lifeex <- train.kknn(Continent~., data = lifeexpectency_train_data, kmax = 50, kernel = c("triangular"))
plot(main="K Nearest Neighbours Life Expectency",train_con_lifeex)
lifeexpecency_knn_model<-kknn(train=lifeexpectency_train_data, test=lifeexpectency_test_data, formula = formula(Continent~.), k = 7, distance = 1)
##Check this plot this needs to be corrected
##plot(lifeexpecency_knn_model)
lifeexpectency_knn_predictions <-predict(lifeexpecency_knn_model, lifeexpectency_test_data[,1:7])
print(table(lifeexpectency_knn_predictions, lifeexpectency_test_data[,8]))
confusionMatrix(lifeexpectency_knn_predictions, lifeexpectency_test_data$Continent)
