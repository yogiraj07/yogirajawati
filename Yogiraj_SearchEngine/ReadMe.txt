README : 
Search Engine implemented in JAVA

****************************************************
Prerequisite :
1) install jdk1.7.0_45 
2) Eclipse Helios

****************************************************
How to run code : 
1) Import the project "SearchEngine"in the eclipse
2) First run Indexer.java using program arguments: tccorpus.txt index.out
3) Than run BM25.java by opening cmd and using program arguments

"D:\IR-JAVA\SearchEngine>java -cp "D:\IR-JAVA\SearchEngine\bin" com.searchengine.BM25Algorithm.BM25 index.out queries.txt 100 > result.eval" 

3) Output will be in result.eval file
****************************************************
Time for execution of the program : - 3 seconds
****************************************************
Approach :
1) When Indexer.java is run :
 a) Inverted index is populated on stemmed document file "tccorpus.txt" in index.out file.
 b) The given stemmed file "tccorpus.txt" is scanned and document id and valid words that contains atleast one alphabet are stored in the index data structure. 
 c) the words that contain [0-9] digits are ignored. 
 d) each word in the index consist of a hashmap of doc id and the frequency of the word in that document
 e) The index.out which is the ouput of this program is an input for BM25 program

2) When BM25 program is run:
 a) index data structure is again populated from index.out, the queries.txt file is read
 b) for each query in the queries.txt, BM25 algorithm is implemented
 c) In the algorithm, for each document in the collection, BM25 score for all the terms in the query is found out.
 d) Top 100 results per query is displayed.
 e) If the third parameter to this program is result.eval, the output is written in that file

************************************************************************
Deliverables:
1) Source Code
2) Output
3) ReadMe file

*************************************************************************
Note : Run Indexer program first and then BM25 program
*************************************************************************
BM25 Algorithm : 



Σ    log (ri + 0.5)/(R − ri + 0.5)                  · (k1 + 1)fi   · (k2 + 1)qfi
i∈Q     ----------------------------------             ---------      ---------
         (ni − ri + 0.5)/(N − ni − R + ri + 0.5)        K + fi         k2 + qfi


K = k1((1 − b) + b · dl  )
                    ----
                    avdl

1) k1
This parameter controls how quickly an increase in term frequency results in term-frequency saturation. The default value is 1.2. Lower values result in quicker saturation, and higher values in slower saturation.
2) b
This parameter controls how much effect field-length normalization should have. A value of 0.0 disables normalization completely, and a value of 1.0 normalizes fully. The default is 0.75.


1) This program consider no relevance, so ri and R=0
2) ni = number of documents that contains quey term qi
3) K1= 1.2 , b =0.75   ,K2=100 
4) fi = frequency of the term in the current document
5) qfi = frequency of the term in the query
6) dl = Document Length
7) avdl = Average Document length

