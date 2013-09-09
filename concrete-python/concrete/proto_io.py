# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
import struct, os, gzip

'''
Read and write protocol buffers to a stream object.
Assumes that files are written as an integer specifying the size of the message and then the 
message itself.
'''
class ProtocolBufferFileReader:
	'''
	message_constructor- the full path of the protocol buffer. This is only needed if you wanted parsed messages back.
	filename- the file to read from
	file_object- the file object to read from instead of filename
	return_byte_string_only- if you just need the byte string, this is much faster
	return_no_message- don't even bother reading the message. Just do seeks.
	'''
	def __init__(self, message_constructor, filename=None, file_object=None, return_byte_string_only=False, return_no_message=False):
		if filename:
			if filename.endswith('.gz'):
				# Create a gzip version
				self.file = gzip.open(filename, 'rb')
			else:
				self.file = open(filename, 'rb')
		elif file_object:
			self.file = file_object
		else:
			raise ValueError('Either the filename or file_object argument must be provided.')

		self.message_constructor = message_constructor
		self.return_byte_string_only = return_byte_string_only
		self.return_no_message = return_no_message
	
	def next(self):
		read_byte = self.file.read(4)
		if len(read_byte) == 0:
			raise StopIteration
		size = struct.unpack('>i', read_byte)[0]
	
		if self.return_no_message:
			self.file.seek(size, os.SEEK_CUR)
			return None

		read_bytes = self.file.read(size)
		if self.return_byte_string_only:
			# Only get the bytes, don't create a message.
			message = read_bytes
		else:
			message = self.message_constructor()
			bytes_read = message.MergeFromString(read_bytes)
		
		return message	
	
	def __iter__(self):
		return self
	
	def close(self):
		self.file.close()
	
	def closed(self):
		return self.file.closed
	
	def name(self):
		return self.file.name

class ProtocolBufferFileWriter:
	'''
	The file_object argument must be a byte stream.
	messages_are_byte_strings- write will be passed byte strings instead of message objects.
	'''
	def __init__(self, filename=None, file_object=None, messages_are_byte_strings=False, append=False):
		if filename:
			if append:
				open_type = 'ab'
			else:
				open_type = 'wb'

			if filename.endswith('.gz'):
				self.file = gzip.open(filename, open_type)
			else:
				self.file = open(filename, open_type)
		elif file_object:
			self.file = file_object
		else:
			raise ValueError('Either the filename or file_object argument must be provided.')

		self.messages_are_byte_strings = messages_are_byte_strings

	
	def write(self, message):
		if self.messages_are_byte_strings:
			string_to_write = message
			byte_size = len(string_to_write)
		else:
			string_to_write = message.SerializeToString()
			byte_size = message.ByteSize()
		size = struct.pack('>i', byte_size)

		self.file.write(size)
		self.file.write(string_to_write)

	def flush(self):
		self.file.flush()
		
	def close(self):
		self.file.close()
	
	def closed(self):
		return self.file.closed
	
	def name(self):
		return self.file.name

def resolveProtoObjectFromString(proto_name):
	index = proto_name.rfind('.')
	if index < 0:
		index = 0
		
	module_name = proto_name[0:index]
	object_name = proto_name[index+1:]
	
	module = __import__(module_name, globals(), locals(), ['NoName'], -1)
	object = getattr(module, object_name)
	return object
	

def countProtosInFile(filename):
	num_messages = 0
	reader = ProtocolBufferFileReader(None, filename=filename, return_no_message=True)
	try:
		for message in reader:
			num_messages +=1
	except IOError as msg:
		print 'Error reading from file: %s' % msg
	
	reader.close()
	return num_messages

'''
Given the name of the proto and the name of the enum name, return the value of the enum.
'''
def getEnumValueFromString(proto_name, enum_name):
	#proto_name = 'proto.document_processing.documents_pb2.Document'
	object = resolveProtoObjectFromString(proto_name)
	value = getattr(object, enum_name)
	return value
			
'''
Example code
writer = ProtocolBufferFileWriter('temp.txt')

document = document_pb2.Document()
document.contents = "Doc contents here"
document.id = '2312312314'

writer.write(document)

document = document_pb2.Document()
document.contents = "Second document"
document.id = '23asfasdf4'

writer.write(document)

writer.close()

print 'Reading'
reader = ProtocolBufferFileReader('temp.txt', document_pb2.Document)

for message in reader:
	print message.contents

reader.close()
'''
