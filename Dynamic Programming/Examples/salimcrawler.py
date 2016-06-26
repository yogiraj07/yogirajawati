import time
import requests
from urllib import parse
from bs4 import BeautifulSoup
import urllib
import socket
import bs4
import re
import requests
#import  urllib
import time
from urllib import parse

from bs4 import BeautifulSoup
#import urllib

url='http://cs5700sp16.ccs.neu.edu/'
URL = 'http://cs5700sp16.ccs.neu.edu/accounts/login/?next=/fakebook/'
sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
sock.connect(('cs5700sp16.ccs.neu.edu', 80))
GETrequest = "GET /accounts/login/?next=/fakebook/ HTTP/1.1\r\nHost: cs5700sp16.ccs.neu.edu\r\nConnection: keep-alive\r\n\r\n\r\n"

sock.sendall(bytes(GETrequest, 'UTF-8'))
data=sock.recv(4096)
#print data
session_pattern = re.compile(r'sessionid=([a-z0-9]+);')
sessionid = session_pattern.findall(data)[0]
soup= BeautifulSoup(data,"html.parser")
token = soup.find('input', {'name':'csrfmiddlewaretoken'})['value']

postdata="csrfmiddlewaretoken="+str(token)+"&username=001680388&password=RRBOL8FF&next=%2Ffakebook%2F"

POSTrequest = "POST /accounts/login/ HTTP/1.1\r\nHost: cs5700sp16.ccs.neu.edu\r\nConnection: keep-alive\r\nContent-Length: "+str(len(postdata))+" \
                  \r\nContent-Type: application/x-www-form-urlencoded\r\nCookie: csrftoken="+str(token)+"; sessionid="+str(sessionid)+"\r\nReferer: http://cs5700sp16.ccs.neu.edu/accounts/login/?next=/fakebook/\r\n\r\n"+postdata+"\n"

#print POSTrequest
sock.sendall(POSTrequest)
data1=sock.recv(4096)
#print data1

session_pattern1 = re.compile(r'sessionid=([a-z0-9]+);')
sessionid1 = session_pattern1.findall(data1)[0]
redirect="GET http://cs5700sp16.ccs.neu.edu/fakebook/ HTTP/1.1\r\nHost: cs5700sp16.ccs.neu.edu\r\nCookie: csrftoken="+str(token)+"; sessionid="+str(sessionid1)+"\r\n\r\n"
sock.sendall(redirect)
response2=sock.recv(4096)

def webcrawler(seed_url,*argv):
 key=""
 if(argv):       #if key is entered for the focussed crawling
     key=argv[0]
 urls=[""]       #Keeps track of valid urls to be processed
 done=[""]       #Keeps track of valid urls whose processing is complete
 depth_max=5     #Setting max depth that needs to be crawled
 temp=0
 k=0
 childArray=[0]*2  #Stores children of current and successive depths
 cc=0
 result=[""]       #Maintains list of Urls which contains relevant urls
 urls[0]=seed_url
 depth=1           #Set current depth
 flag=0
 firstEntryinChild=0  #Records total number of children for depth 'd'
 crawledUrls=1        #Represents number of Urls which contain keyphrase for focussed crawl or number of urls crawled for unfocussed type
 validUrlsCrawled=1
 maxLimitToCrawl=1000
 while depth<=depth_max and (crawledUrls <maxLimitToCrawl) and len(urls) >0 :  #Condition represents : if max depth is reached, or URL crawled are 1000 or there are urls to process

    text1=getHtmlObject(urls[0])   #Getting Html object of urls[0] (The array which contains urls to be processed)
    if(text1==""):                 #If we get error in opening urls[0], Go to next url. Used for connection exception
        urls.pop(0)
        time.sleep(10)             #Delay inserted to see if the internet connection is re-established
        continue

    parent=urls.pop(0)             # pop the first Valid url (if focussed, Urls with keyphrase are present) and process it

    if(done[0]== ""):              #put it in done[], since the url processing is done and we are going to process its child
       done[0]=parent
    else:
       done.append(parent)

    if(childArray[0]>1):            #This logic is written to calculate depth.
        childArray[0]=childArray[0]-1
    elif(childArray[0]==1):
        childArray[0]=childArray[1]
        childArray[1]=0
        if(not(childArray[0]==0)):
            firstEntryinChild=childArray[0]
            cc=0
            k=1

    if(parent==seed_url):
          if(key=="" or checkKeyphrase(key,text1)):
              print("URL:- "+seed_url)
              result[0]=parent

    print("Parent:- ")
    print(parent)

    print("Depth:- "+str(depth))
    if(depth==depth_max):   #dont go to its children and exit the program
        print ("Max depth reached!!!")
        break;

    soup = BeautifulSoup(text1,"html.parser")
    for link in soup.findAll('a',href=True):

            href=link.get('href')
            validUrl,href=isValidUrl(href,done,urls,parent)  #Checks if the Url is valid
            if (validUrl) :
                 text1 = getHtmlObject(href)
                 if(text1==""):                    #If we get error in opening href, go to next child of the parent
                        result.append(href)
                        continue
                 validUrlsCrawled=validUrlsCrawled+1 # Maintains track of Urls that are valid according to the criteria
                 if(key=="" or checkKeyphrase(key,text1)):
                     crawledUrls=crawledUrls+1       #Counts number of urls that are the final output (In focussed:- counts urls that have keyphrase)
                     print("URL:- ",href," Relevant Urls Crawled:- ",crawledUrls)
                     flag=1
                     cc+=1    #Counting childs for Parents
                     if(crawledUrls==maxLimitToCrawl):
                          result.append(href)
                          flag=0
                          k=0
                          break
                     urls.append(href)  #Relevant urls populated and processed in next iterations

                     if(result[0]==""):
                        result[0]=href
                     else:
                        result.append(href)  #populating output list

            else:
                continue        #if the url is not valid, go to next child

    print("Relevant Crawled Urls:- ",crawledUrls)
    print("Total valid Urls crawled:- ",validUrlsCrawled)
    if(flag==1 or k==1):  #if the current parent had childs, populate childArray which helps in calulating depth
        flag=0
        k=0
        childArray,cc,depth,firstEntryinChild=populateChildArray(childArray,cc,depth,firstEntryinChild)

 print ("Final Result :- ",result)
 #print (result)
 print("Result Length:- ",len(result))
 print("Total Valid Urls Crawled:- ",validUrlsCrawled)
 print("Proportion :- ",crawledUrls/validUrlsCrawled)

 if (key):
      file=open("YogirajAwati_FocussedOutput.txt","w")  #Writing result to file
 else:
      file=open("YogirajAwati_UnFocussedOutput.txt","w")  #Writing result to file
 for u in result:
      file.write(u)
      file.write("\n")


def getHtmlObject(url):
    try:
        time.sleep(1)  # insert delay of 1 sec  as per politeness policy
        code = requests.get(url)
    except:
        print ("Connection Error in opening url:- ",url)
        return ""
    text1 = code.text  # get text format of html for processing
    return text1

def isValidUrl(href,done,urls,parent):
     if (":" in href or "Main_Page" in href or "#" in href ):
                return False,""
     href=parse.urljoin(parent,href)
     if((("http://en.wikipedia.org/wiki/" in href) or ("https://en.wikipedia.org/wiki/" in href)) and ("http://en.wikipedia.org/wiki/Main_Page" not in href) and href not in done and href not in urls):
         return True,href
     return False,""

def checkKeyphrase(key,text1):

          soup = BeautifulSoup(text1,"html.parser")
          data=soup.get_text()
          if(key.lower() in data.lower()):
              return True
          else:
              return False

def populateChildArray(childArray,cc,depth,firstEntryinChild):
        if(childArray[0]==0):
            childArray[0]=cc
            firstEntryinChild=cc
            cc=0
        else:
            childArray[1]=cc

        if(childArray[0]==firstEntryinChild):
            depth=depth+1
        return childArray,cc,depth,firstEntryinChild




#Inputs :
#webcrawler("http://en.wikipedia.org/wiki/Hugh_of_Saint-Cher","concordance")
webcrawler("http://cs5700sp16.ccs.neu.edu/fakebook/")

