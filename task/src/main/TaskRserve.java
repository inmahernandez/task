package main;

import java.util.Iterator;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class TaskRserve {
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
				c.eval("trainingFunction<-function(){ \n" +
						"xTrain = leafTrain[,-1]\n" + 
						"yTrain = as.factor(leafTrain$Class)\n" +
						"model <- train(xTrain, yTrain, 'nb')\n"
						+ "return (model)}\n");
				
				//Create second function, which applies the previously created model to
				//classify further instances
				c.eval("testingFunction<-function(testSet){\n "
						+ "xTest = testSet[,-1]\n"
						+ "yTest = as.factor(testSet$Class)\n" +
						"predict(model$finalModel,xTest)\n" + 
						"result <- predict(model$finalModel,xTest)$class\n"
						+ "return (result)}");
				
					//Call functions to test program
					c.eval("model<-trainingFunction()");
					c.eval("result <- testingFunction(leafTest)");
					try {
						RList output = c.eval("result").asList();
						System.out.println("Classifier output:");
						Iterator it = output.iterator();
						while(it.hasNext())
							System.out.println(it.next().toString());
					} catch (REXPMismatchException e) {
						System.err.println("Error outputting results: " + e.getMessage());
					}
						
				c.close();
    }
}