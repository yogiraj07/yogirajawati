def  addToResult(result,t):
    i=0;
    resultSize=len(result)
    while result[i] != 400 and i<resultSize:
        i=i+1
    if i==resultSize :
       return result
    print("r :",result)
    print("t ",t)
    for item in t:
        result[i]=item
        i+=1
    return result;
def sort(t):
    t.sort()
    return t

def getOrdering(h,b,w):
    size=len(h)
  #  temp=[400]*size
    temp=[]
    result=[400]*size
    temp.append(h[0])
    flag=0
    for i in range(1,size):
        if b[i]==w[i-1]:
            temp.append(h[i])
            flag=1
        else :
          #  print("temp: ",temp)
            result=addToResult(result,sort(temp))
           # print("Result : ",result)
            temp.clear()
            temp=[]
            temp.append(h[i])
            flag=0
    if  temp:
        print("temp: ",temp)
        result=addToResult(result,sort(temp))



    return result


#h=[5,4,3,2,1]
#b=[1,1,1,1,1]
#w=[365,365,365,365,365]

#h=[5,4,3,2,1]
#b=[1,5,10,15,20]
#w=[5,10,14,20,25]

h=[1,2,3,4,5,6]
b=[1,3,1,3,1,3]
w=[2,4,2,4,2,4]

print("Ordering is:- ",getOrdering(h,b,w))