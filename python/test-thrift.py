#!/usr/bin/env python
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol

import sys
sys.path.append('gen-py')

from concrete.communication import *
from concrete.communication.ttypes import *

transportOut = TTransport.TMemoryBuffer()
protocolOut = TBinaryProtocol.TBinaryProtocol(transportOut)

foo = Communication()
foo.text = u'\u00c3'
foo.write(protocolOut)

bytez = transportOut.getvalue()

transportIn = TTransport.TMemoryBuffer(bytez)
protocolIn = TBinaryProtocol.TBinaryProtocol(transportIn)
newComm = Communication()
newComm.read(protocolIn)

print newComm.text
