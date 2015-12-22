Goal:Text Classification
*******************************************************************
GROUP:
 - ASHISH KALBHOR (NUID: 001630133)
 - YOGIRAJ AWATI  (NUID: 001663431)
*******************************************************************
Prerequisite :
1) Install jdk1.7.0_45 
2) Eclipse Helios
****************************************************
How to run code : 
1) Import the project YogirajAshishTextClassification in eclipse

2) Run nbtrain.java (make sure folder = "textcat" is present inside project workspace)
--Give input to nbtrain through command line argument: 

	> nbtrain <training-directory> <model-file>
	
	Sample Input: nbtrain "C:\Users\Ashish\Documents\IR\Assignment 6\YogirajAshishTextClassification\textcat\train" "model.txt"
	
3) Run nbtest.java
--Give following input through command line :

	> nbtest <model-file> <test-directory> <predictions-file>
	
	-- Sample for test data, input: nbtest "model.txt" "C:\Users\Ashish\Documents\IR\Assignment 6\YogirajAshishTextClassification\textcat\test" "TestPrediction.txt"
	-- Sample for dev data, input : nbtest "model.txt" "C:\Users\Ashish\Documents\IR\Assignment 6\YogirajAshishTextClassification\textcat\dev" "DevPrediction.txt"
	
(Result is written to filename with 3rd parameter)
****************************************************
Time for execution of the program : 3 seconds
****************************************************
Description:
1) Running nbtrain.java iterates over textcat to generate probabilities for the words present in the collection
2) Model.txt file is generated. It contains p_prior and n_prior as the first line. 
	And word , log(positive_probability), log(negative_probability) for the remainder of the text
3) Model.txt is input to nbtest.java. Running this file classifies the test data and analyzes dev data. 

************************************************************************
Deliverables:
1) Source Code
2) model.txt (Contains the model generated after running the nbtrain file)
3) Prediction file for Dev directory: "DevPrediction.txt"
4) Prediction file for Test directory: "TestPrediction.txt"
5) A list of the 20 terms with the highest (log) ratio of positive to negative weight: "Top20P-by-N"
6) A list of the 20 terms with the highest (log) ratio of negative to positive weight: "Top20N-by-P"
7) Console output of both the programs.
*************************************************************************
ANALYSIS FOR DEV DATA

For folder : neg
Positive Review : 20 Negative Review : 80 Total Files : 100
So 80% of the negative reviews were correctly classified


For folder : pos
Positive Review : 75 Negative Review : 25 Total Files : 100
So 75% of th epositive reviews were correctly classified

*************************************************************************
NOTE:
1) space (" ") is removed from the map which stores unique words from train data
2) When running program for test and dev data, PLEASE GIVE DIFFERENT FILENAME else RESULT WILL BE OVERWRITTEN in the same file