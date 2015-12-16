
R=3
C=3
def minCost(cost,m,n):
 #Holds cost for at each point
 termCost=[[0 for x in range(C)] for x in range(R)]
 #set first row
 for i in range(1,n+1):
     termCost[0][i]=termCost[0][i-1]+cost[0][i]
 #set first coloumn
 for j in range(1,m+1):
     termCost[j][0]=termCost[j-1][0]+cost[j][0]
 #calculate remaining cost
 for i in range (1,m+1):
     for j in range(1,n+1):
         # current cost + minimum cost from previous node
         termCost[i][j]=cost[i][j]+min(termCost[i-1][j],termCost[i][j-1],termCost[i-1][j-1])
 return termCost[m][n]

#Driver program to test above functions
cost = [[1, 2, 3],
        [4, 8, 2],
        [1, 5, 3]]
print(minCost(cost, 2, 2))