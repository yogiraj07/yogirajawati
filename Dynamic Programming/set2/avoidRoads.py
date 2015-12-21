'''

In the city, roads are arranged in a grid pattern. Each point on the grid represents a corner where two blocks meet.
The points are connected by line segments which represent the various street blocks. Using the cartesian coordinate system,
we can assign a pair of integers to each corner as shown below.

You are standing at the corner with coordinates 0,0. Your destination is at corner width,height. You will return the number of distinct paths that lead to your destination. Each path must use exactly width+height blocks. In addition, the city has declared certain street blocks untraversable. These blocks may not be a part of any path. You will be given a String[] bad describing which blocks are bad. If (quotes for clarity) "a b c d" is an element of bad, it means the block from corner a,b to corner c,d is untraversable. For example, let's say
width  = 6
length = 6
bad = {"0 0 0 1","6 6 5 6"}

Definition

Class:	AvoidRoads
Method:	numWays
Parameters:	int, int, String[]
Returns:	long
Method signature:	long numWays(int width, int height, String[] bad)
(be sure your method is public)


Constraints
-	width will be between 1 and 100 inclusive.
-	height will be between 1 and 100 inclusive.
-	bad will contain between 0 and 50 elements inclusive.
-	Each element of bad will contain between 7 and 14 characters inclusive.
-	Each element of the bad will be in the format "a b c d" where,
a,b,c,d are integers with no extra leading zeros,
a and c are between 0 and width inclusive,
b and d are between 0 and height inclusive,
and a,b is one block away from c,d.
-	The return value will be between 0 and 2^63-1 inclusive.

Examples
0)

6
6
{"0 0 0 1","6 6 5 6"}
Returns: 252
Example from above.
1)

1
1
{}
Returns: 2
Four blocks aranged in a square. Only 2 paths allowed.
2)

35
31
{}
Returns: 6406484391866534976
Big number.
3)

2
2
{"0 0 1 0", "1 2 2 2", "1 1 2 1"}
Returns: 0


'''

#Implementation


def allowed(i, j, x, y, banned_set):
    s = "{} {} {} {}"
    options = [
        s.format(i, j, x, y),
        s.format(x, y, i, j)
     ]

    for opt in options:
        if opt in banned_set:
            return False
    return True

def calc(lst, i, j, banned_set):
    result = 0
    #i-1>=0 is used for work around to (0,0) point and calculate points >(0,0)
    if i-1 >= 0 and allowed(i, j, i-1, j, banned_set):
            result += lst[i-1][j]
    if j-1 >= 0 and allowed(i, j, i, j-1, banned_set):
            result += lst[i][j-1]

    return result

def avoid_roads(n, m, banned_set):
    n += 1
    m += 1

    matrix = [[0 for x in range(m)]for x  in range(n)]
    #initialising point to 0,0
    matrix[0][0] = 1

    for i in range(n):
        for j in range(m):
            if i == j == 0:
                continue
            matrix[i][j] = calc(matrix, i, j, banned_set)
    return matrix[n-1][m-1]


w=2
h=2
block=["0 0 1 0", "1 2 2 2", "1 1 2 1"]
print ("Number of paths :- ",avoid_roads(w,h,block))
if __name__ == '__main__':

    assert 252 == avoid_roads(6, 6, ['0 0 0 1', '6 6 5 6'])
    assert 2 == avoid_roads(1, 1, {})
    assert 6406484391866534976 == avoid_roads(35, 31, {})
    assert 252 == avoid_roads(6, 6, ["0 0 0 1","6 6 5 6"])