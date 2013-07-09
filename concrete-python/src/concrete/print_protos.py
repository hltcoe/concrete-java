# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.

import sys
from concrete.proto_io import ProtocolBufferFileReader, resolveProtoObjectFromString

'''
A simple utility for printing protocol buffers.
Note that the resulting printing is subject to Python's writing to the command line
and the protocol buffers rendering of the object. This means that printed contents
may not match the file exactly. For example, escapes (\) are insterted before such
characters, such as ".
'''
def main():
	proto_name = sys.argv[1]
	filename = sys.argv[2]
	
	
	object = resolveProtoObjectFromString(proto_name)
	
	reader = ProtocolBufferFileReader(object, filename=filename)
	
	for message in reader:
	   print message



if __name__ == '__main__':
	main()
