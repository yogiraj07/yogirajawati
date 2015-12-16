#Given a list of N coins, their values (V1, V2, … , VN), and the total sum S. Find the minimum number of coins the sum
#of which is S (we can use as many coins of one type as we want), or report that it’s not possible to select coins in such
#a way that they sum up to S.

def minCoins(arr,N):
    sum=[1000]*(N+1)
    sum[0]=0
    size=len(arr)
    #calculate coins upto sum N starting from 1
    #sum of 1 , 2 , 3, ,,,,N
    for i in range(1,N+1):
        for j in range(0,size):
            if arr[j]<=i and (sum[i-arr[j]]+1)<sum[i]:
                sum[i]=sum[i-arr[j]]+1
    return sum[N]

s=[1,3,5]
N=11
print("Minimum coins for ",N," is:- ",minCoins(s,N))