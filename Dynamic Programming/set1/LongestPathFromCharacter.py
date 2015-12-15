from nltk.internals import a


#tool matrices to recur for adjacent cells.
x = {0, 1, 1, -1, 1, 0, -1, -1}
y = {1, 0, 1, 1, -1, -1, 0, -1};
R=C=3
#dp[i][j] Stores length of longest consecutive path
#starting at arr[i][j].
dp=[R][C]
dp= [-1][R * C]

#check whether mat[i][j] is a valid cell or not.
def isValid(i,j):
    if i<0 or j<0 or i> R or j>C :
        return #f
    else :
        return #t

# Check whether current character is adjacent to previous
# character (character processed in parent call) or not.
def isAdjacent (prev,curr):
    return ((curr-prev)==1)

#i, j are the indices of the current cell and prev is the
#character processed in the parent call.. also mat[i][j]
# is our current character.
def getLenUntil (a,i,j,prev):
    #If this cell is not valid or current character is not
    #adjacent to previous one (e.g. d is not adjacent to b )
    #or if this cell is already included in the path than return 0.
    if(not isValid(i,j) or not isAdjacent(prev,a[i][j])):
        return 0
    #If this subproblem is already solved , return the answer
    if dp[i][j] != -1:
        return dp[i][j]
    ans =0
    #recur for paths with differnt adjacent cells and store
    #the length of longest path.
    for k in range (0,8):
                    ans=max(ans , (1 + getLenUntil(arr,i+x[k],j+y[k],arr[i][j])))

    dp[i][j]=ans
    return dp[i][j]


#Returns length of the longest path with all characters consecutive
#to each other.  This function first initializes dp array that
#is used to store results of subproblems, then it calls
#recursive DFS based function getLenUtil() to find max length path
def lis (arr,point):
    n=len(arr)
    lis=[1]* n
    i,j=0
    ans =0
    for i in range(0,n):
        for j in range (0,i):
            if arr[i][j]==point :
                for k in range (0,8):
                    ans=max(ans , (1 + getLenUntil(arr,i+x[k],j+y[k],point)))


    return ans

arr=[R][C]
arr[R][C] =      [ ['a','c', 'd'],
                   ['h', 'b', 'e'],
                   ['i', 'g', 'f']]
startPoint = 'e'
print ("Length of LIS is", lis(arr,startPoint))