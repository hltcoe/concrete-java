# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
import sys
from concrete.proto_io import ProtocolBufferFileReader, ProtocolBufferFileWriter
from concrete.utils import parseCommandLine, usage
from concrete.proto_io import countProtosInFile

'''
A simple utility for copying the last N protocol buffers in a shard.
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
		
		num_in_file = countProtosInFile(input_shard)
		
		reader = ProtocolBufferFileReader(None, filename=input_shard, return_byte_string_only=True)
		writer = ProtocolBufferFileWriter(filename=output_shard, messages_are_byte_strings=True)
		num_messages_to_print = int(options_hash['n'])
		
		first_message_to_print = num_in_file - num_messages_to_print
		if first_message_to_print < 0:
			first_message_to_print = 0
		
		num_messages = 0
		for message in reader:
			if num_messages >= first_message_to_print:
				writer.write(message)
			num_messages += 1
		
		writer.close()
		reader.close()
	
	
if __name__ == '__main__':
	ProtocolBufferHead().main()
