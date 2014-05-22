package main;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class TestReserve {
    public static void main(String[] args) throws RserveException {
       
            	// make a new local connection on default port (6311)
				RConnection c = new RConnection();
				
				//load libraries
				c.voidEval("library(\"klaR\")\n");
				c.voidEval("library(\"caret\")\n");
				
				//load dataset
				c.voidEval("leaf <- read.csv(file=\"leaf2.csv\",head=TRUE,sep=\",\")");
				
				//Use the provided dataset to create training & testing samples
				c.voidEval("train.ind <-sample(1:nrow(leaf), ceiling(nrow(leaf)*2/3), replace=FALSE)");
				c.voidEval("leafTrain <-leaf[train.ind,]");
				c.voidEval("leafTest <- leaf[-train.ind,]");
				
				//Create first function, which trains a classifier based on the training sample
			/*	c.voidEval("trainingFunction<-function(){ \n" +
						"xTrain <- leafTrain[,-1]\n" + 
						"yTrain <- as.factor(leafTrain$Class)\n" +
						"model <- train(xTrain,yTrain,'nb', trControl=trainControl(method='cv',number=10))\n}");
				*/
				
				c.voidEval("testingFunction<-function(testSet){\n "
						+ "xTest = testSet[,-1]\n"
						+ "yTest = as.factor(testSet$Class)\n" +
						"predict(model$finalModel,xTest)\n" + 
						"predict(model$finalModel,xTest)$class}\n");
				
				//try {
					//Call functions to test program
				//	c.eval("trainingFunction()");
					REXP output = c.eval("testingFunction(leafTest)");
					System.out.println("Classifier output:");
					/*for(String s: output)
						System.out.println(s);*/
						
			/*	} catch (REXPMismatchException e) {
					System.err.println("Error parsing the output: " + e.getMessage());
				/*} catch (REngineException e) {
					System.err.println("Error parsing the output: " + e.getMessage());
				}*/
				c.close();
    }
}