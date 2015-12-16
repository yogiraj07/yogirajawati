
def LIS(s):
    #sequence[i] stores longest increasing sequence for s[i]
    sequence=[1]*len(s)
    size=len(s)
    for i in range(0,size):
        for j in range(0,i):
            if s[j]<s[i] and sequence[j]+1>sequence[i]:
                  sequence[i]=sequence[j]+1
    return sequence[size-1]

s=[5, 3, 4, 8, 6, 7]

print("Longest Sequence is:- ",LIS(s))