##############RIPPER###############
install.packages("party")
install.packages("zoo")
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


set.seed(9468)
data("iris")

print("Preparing the LifeExpectency Dataset")
lifeexpectency = read.csv("life_expectancy.csv")


print("Partition Iris dataset")
iris_train_Index <- createDataPartition(iris$Species, p=0.80, list=FALSE)
iris_train_data <- iris[iris_train_Index,]
iris_test_data <- iris[-iris_train_Index,]

print("Partition LifeExpectency dataset")
lifeexpectency_train_index <- createDataPartition(lifeexpectency$Continent, p = .8, list = FALSE,times = 1)
lifeexpectency_train_data <- lifeexpectency[lifeexpectency_train_index,]
lifeexpectency_test_data <- lifeexpectency[-lifeexpectency_train_index,]

#################################### RIPPER###########
#testInd <- iris_test_data[ ,!colnames(iris_test_data) %in% "Species"]
#testDep <- as.factor(iris_test_data[, names(iris_test_data) == "Species"]) 

train_data <- iris_train_data[,1:4]
train_classes <- iris_train_data[,5]
train_Jrip_fit_model <- train (train_data, train_classes, method = "JRip", tuneLength = 17)
plot(train_Jrip_fit_model)
predictions <- predict(train_Jrip_fit_model, iris_test_data)
print (table (predictions, iris_test_data$Species))

####################################### C4.5###########

iris_c45_fit_model <- J48(Species~., data = iris.train.data)
plot (iris_c45_fit_model)
summary(iris.fit)
iris_c45_predictions <- predict(iris_c45_fit_model, iris_test_data)
print(table(iris_c45_predictions, iris_test_data$Species))


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
  cnames = colnames(predictions)[answers[]]
  return (cnames)
}
  
print(table(iris_oblique_tree_predictions(iris_oblique_tree_model, iris_test_data), iris_test_data$Species))



########################################## NAIVE BIAS########

#iris_naivebias_model<-NaiveBayes(Species~., data=iris_train_data)
#iris_naivebias_model
iris_naivebias_model<-naiveBayes(iris.train.data[,1:4], iris.train.data[,5])
iris_naivebias_model
iris_nb_predictions <- predict(iris_naivebias_model, iris_test_data[,1:4])
iris_nb_predictions
plot(table(iris_nb_predictions, iris_test_data$Species))
pairs(iris_train_data, main = "Iris Data", pch = 21, bg = c("red", "green3", "blue") [unclass(iris_train_data$Species)])


################KNN##########

library(class)
library(kknn)
train_con <- train.kknn(Species~., data = iris_train_data, kmax = 50, kernel = c("triangular"))
plot(train_con)
iris_knn <- knn(train=subset(iris, select = -Species), test=subset(iris, select = -Species), cl=iris$Species, k = 3, prob=TRUE)
iris_knn_model<-kknn(train = iris_train_data, test = iris_test_data, formula = formula(Species~.), k = 7, distance = 1)
iris_knn_predictions <-predict(iris_knn_model, iris_test_data[,1:4])
print(table(iris_knn_predictions, iris_test_data[,5]))


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
plot(lifeexpectency_Jrip_fit_model, main="lifeexpectecy training model")
lifeexpectency_ripper_predictions<-predict(lifeexpectency_Jrip_fit_model, lifeexpectency_test_data)
print (table (lifeexpectency_ripper_predictions, lifeexpectency_test_data_classes))


##############C4.5#########
data(lifeexpectency)
lifeexpectency_fit <- J48(Continent~., data=lifeexpectency)
summary(lifeexpectency_fit)
lifeexpectency_predictions <- predict(lifeexpectency_fit, lifeexpectency[,1:7])
table(lifeexpectency_predictions, lifeexpectency_predictions[,8])

###############Oblique Tree#########


############Naive Bias##########



######################KNN############

life_expectency_model <- NaiveBayes(Continent~., data=lifeexpectency_train_data)
life_expectation_predictions <- predict(life_expectency_model, lifeexdata_test[,1:3])
confusionMatrix(life_expectation_predictions$class, lifeexdata_test$Continent)
plot(life_expectation_predictions$class, lifeexdata_test$Continent)
pairs(iris_train_data, main = "Iris Data", pch = 21, bg = c("red", "green3", "blue") [unclass(iris_train_data$Species)])


