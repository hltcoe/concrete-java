# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
'''
Randomize the order of protocol buffers in a shard.

Arguments:
- input_shard- a shard of protocol buffers to randomzie
- output_shard- a new shard of ranomzied protocol buffers
'''

import sys, codecs, random
from concrete.utils import parseCommandLine, usage
from concrete.proto_io import ProtocolBufferFileReader, ProtocolBufferFileWriter

class RandomizeShard:
	def run(self):
	# Specify options.
		options = [
					]
		# Start main method here.
	
		command_line = '%s input_shard output_shard'
		options_hash, remainder = parseCommandLine(options, command_line=command_line)

		sys.stdout = codecs.getwriter('utf8')(sys.stdout)
		
		if (len(remainder) != 2):
			print usage(sys.argv, command_line, options)
			sys.exit()
			
		input_file = remainder[0]
		output_file = remainder[1]
		
		self.randomize(input_file, output_file)

	def randomize(self, input_file, output_file):
		reader = ProtocolBufferFileReader(None, filename=input_file, return_byte_string_only=True)
		
		
		buffer = []
		for message in reader:
			buffer.append(message)
		
		reader.close()
		
		random.shuffle(buffer)
		writer = ProtocolBufferFileWriter(filename=output_file, messages_are_byte_strings=True)
		for message in buffer:
			writer.write(message)
				
		writer.close()
		
	
if __name__ == '__main__':
	RandomizeShard().run()