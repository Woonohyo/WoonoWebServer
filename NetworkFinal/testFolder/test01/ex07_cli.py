# Echo client program
import socket
import time

HOST = 'localhost'
PORT = 8000  

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))
s.sendall('GET /\r\n\r\n')
resp = ''
while 1:
    data = s.recv(8192)
    resp += data
    if not data:
        break
    else:
        print len(data), 'bytes read'
for i in xrange(100):
    print i
    time.sleep(1)
s.close()
print 'Received:', resp
#print 'Received', repr(data)
