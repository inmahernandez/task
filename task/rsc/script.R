#load libraries
library("klaR")
library("caret")
library("Rserve")

#load dataset
leaf <- read.csv(file="leaf2.csv",head=TRUE,sep=",")

#Use the provided dataset to create training & testing samples
train.ind = sample(1:nrow(leaf), ceiling(nrow(leaf)*2/3), replace=FALSE)
leafTrain = leaf[train.ind,]
leafTest = leaf[-train.ind,]

#required functions
trainingFunction<-function(){ 
xTrain = leafTrain[,-1]
yTrain = as.factor(leafTrain$Class)
model <- train(xTrain,yTrain,'nb')
return (model)
}

testingFunction<-function(testSet){
xTest = testSet[,-1]
yTest = as.factor(testSet$Class)
predict(model$finalModel,xTest)
result <- predict(model$finalModel,xTest)$class
return (result)
}

#calls to both functions to test their functionalities
model <- trainingFunction()
result <- testingFunction(leafTest)
result