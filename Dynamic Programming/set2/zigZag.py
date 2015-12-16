'''
Problem Statement

A sequence of numbers is called a zig-zag sequence if the differences between successive numbers strictly alternate between positive
and negative. The first difference (if one exists) may be either positive or negative. A sequence with fewer than two elements is
 trivially a zig-zag sequence.

For example, 1,7,4,9,2,5 is a zig-zag sequence because the differences (6,-3,5,-7,3) are alternately positive and negative.
In contrast, 1,4,7,2,5 and 1,7,4,5,5 are not zig-zag sequences, the first because its first two differences are positive and the
second because its last difference is zero.

Given a sequence of integers, sequence, return the length of the longest subsequence of sequence that is a zig-zag sequence.
A subsequence is obtained by deleting some number of elements (possibly zero) from the original sequence, leaving the remaining
elements in their original order.


Definition

Class:	ZigZag
Method:	longestZigZag
Parameters:	int[]
Returns:	int
Method signature:	int longestZigZag(int[] sequence)
(be sure your method is public)


Constraints
-	sequence contains between 1 and 50 elements, inclusive.
-	Each element of sequence is between 1 and 1000, inclusive.

Examples
0){ 1, 7, 4, 9, 2, 5 }
Returns: 6
The entire sequence is a zig-zag sequence.
1){ 1, 17, 5, 10, 13, 15, 10, 5, 16, 8 }
Returns: 7
There are several subsequences that achieve this length. One is 1,17,10,13,10,16,8.
2){ 44 }
Returns: 1
3){ 1, 2, 3, 4, 5, 6, 7, 8, 9 }
Returns: 2
4){ 70, 55, 13, 2, 99, 2, 80, 80, 80, 80, 100, 19, 7, 5, 5, 5, 1000, 32, 32 }
Returns: 8
5){ 374, 40, 854, 203, 203, 156, 362, 279, 812, 955,
600, 947, 978, 46, 100, 953, 670, 862, 568, 188,
67, 669, 810, 704, 52, 861, 49, 640, 370, 908,
477, 245, 413, 109, 659, 401, 483, 308, 609, 120,
249, 22, 176, 279, 23, 22, 617, 462, 459, 244 }
Returns: 36


'''

# IMPLEMENTATION
######################################################################################################################
#Returns true if the product of current diff and previous diff is negative which implies its alternating sequence
def isValidAlternateDifference(prev,curr):
    if curr*prev<0:
        return True
    return False
def longestZigZag(s):
    #sequence[i] stores longest increasing sequence for s[i]
    size=len(s)
    sequence=[1]*len(s)
    #validDifference[i] sotres difference between s[i]- last element of the longest sequence before s[i]
    validDifference=[-1]*size

    #used to adjust if second element in the sequence is smaller than first
    if size>1 and s[1]<s[0]:
        validDifference[0]=1

    for i in range(0,size):
        for j in range(0,i):
            if isValidAlternateDifference(validDifference[j],s[i]-s[j]) and sequence[j]+1>sequence[i]:
                  validDifference[i]=s[i]-s[j]
                  sequence[i]=sequence[j]+1
    return sequence[size-1]

s=[1, 17, 5, 10, 13, 15, 10, 5, 16, 8 ]

print("Longest Sequence is:- ",longestZigZag(s))