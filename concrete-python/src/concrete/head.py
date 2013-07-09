# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
import sys
from concrete.proto_io import ProtocolBufferFileReader, ProtocolBufferFileWriter
from concrete.utils import parseCommandLine, usage

'''
A simple utility for copying the first N protocol buffers in a shard.
'''

class ProtocolBufferHead:
	def main(self):
		# Specify options.
		options = [
					('n=', 'The number of protocol buffers to save.', True),
					]
		# Start main method here.
		command_line = '%s --n=number_to_print input_shard output_shard'
		options_hash, remainder = parseCommandLine(options, command_line=command_line)
		
		if (len(remainder) != 2):
			print usage(sys.argv, command_line, options)
			sys.exit()
			
		input_shard = remainder[0]
		output_shard = remainder[1]
		
		
		reader = ProtocolBufferFileReader(None, filename=input_shard, return_byte_string_only=True)
		writer = ProtocolBufferFileWriter(filename=output_shard, messages_are_byte_strings=True)
		num_messages_to_print = int(options_hash['n'])
		
		num_messages = 0
		for message in reader:
			num_messages += 1
			if num_messages > num_messages_to_print:
				break
			writer.write(message)
		
		writer.close()
		reader.close()
	
	
if __name__ == '__main__':
	ProtocolBufferHead().main()
