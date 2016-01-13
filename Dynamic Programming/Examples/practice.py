__author__ = 'sarit'
a = 20
def chk():
    b = 10
    return b
    raise Exception(answer + "doesnot match the expected answer")

if __name__ == '__main__':
    try:
        ans = chk()
        print('Answer: %s' % ans)
        assert ans == a
        print('SUCCESS!')
    except Exception as err:
     print ("ERROR :", str(err))
