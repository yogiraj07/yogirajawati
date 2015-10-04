Instructions to run program :

1) The program is written in Python 3.4.2. File Name :- Yogiraj_WebCrawler.py

2) External Library Used:- BeautifulSoup 4.4.0
   Link to download:http://www.crummy.com/software/BeautifulSoup/bs4/download/

3) Also install library "requests". Following commands can be used to install above two          packages : 
   a) open cmd
   b) type : python -m pip install "required library name"
      for eg : python -m pip install requests
               python -m pip install beautifulsoup4

4) Following imports are being made:

   import requests
   import time
   from urllib import parse
   from bs4 import BeautifulSoup

5) Proportion for keyword "concordance":
      Total Valid Urls crawled :- 66425
      Relevant URLS in which keyphrase is present :- 415
      Proportion = 415/66425= 0.00624

6) Implementation Note:a) For Focussed crawling, the crawler crawls upto limit of thousand                                once the crawler gets 1000 urls in which the keyphrase is present.
                       b) Focussed crawl took approx 12 hours to produce above result

7) Valid Inputs :
Focussed Crawling:- webcrawler("http://en.wikipedia.org/wiki/Hugh_of_Saint-Cher","concordance")
Unfocussed Crawling:- webcrawler("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher")

8) Deliverables :
   a) Yogiraj_WebCrawler.py
   b) YogirajAwati_FocussedOutput.txt  -> Contains focussed crawl output for "concordance"
   c) YogirajAwati_UnFocussedOutput.txt ->Contains unfocussed crawl output 

9) Approach for the program :
   a) BFS is used for the crawling
   b) Seed url is taken
   c) Then all its valid children are populated
   d) First child is taken and all its children are populated in the list
   e) Second child of the same depth is taken and all its children are populated in the list
   f) In similar fashion all urls are crawled until depth=5 or 1000 relevant urls are reached



