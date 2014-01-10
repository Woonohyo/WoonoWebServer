# Echo client program
import socket
import time
import sys

HOST = sys.argv[1]
PORT = int(sys.argv[2])

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))
if len(sys.argv) < 5:
    s.sendall('GET ' + sys.argv[3] + '\r\n\r\n')
else:
    s.sendall('GET ' + sys.argv[3] + ' ' + sys.argv[4] + '\r\n\r\n')
resp = ''
while 1:
    data = s.recv(8192)
    resp += data
    if not data:
        break
s.close()
print resp
