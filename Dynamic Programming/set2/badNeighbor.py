
def LIS(s):
    #sequence[i] stores longest increasing sequence for s[i]
    sum=[0]*len(s)
    size=len(s)
    if size==1:
        return s[0]
    if size==2:
        return max(s[0],s[1])

    sum[0]=s[0]
    sum[1]=s[1]
    sum[2]=s[2]
    maxDonation=0
    for i in range(3,size):
        for j in range(0,i-1):
            if sum[j]+s[i]>sum[i]:
                sum[i]=sum[j]+s[i]

    for i in range(0,len(sum)):
         maxDonation=max(sum[i],maxDonation)
    return maxDonation

s=[10, 3, 2, 5, 7, 8 ]

print("Longest Sequence is:- ",LIS(s))