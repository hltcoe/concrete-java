# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
'''
Randomly select a specified number of records from this shard.
'''

import sys, os.path, random

from concrete.utils import parseCommandLine, usage
from concrete.proto_io import ProtocolBufferFileReader, ProtocolBufferFileWriter, countProtosInFile

class RandomlySelectProtos:
	def run(self):
		# Specify options.
		options = [
					('num_records=', 'Number of records to select.', True),
					]
		# Start main method here.
	
		command_line = '%s --num_records=n input_shard output_shard'
		options_hash, remainder = parseCommandLine(options, command_line=command_line)

		if (len(remainder) != 2):
			print usage(sys.argv, command_line, options)
			sys.exit()
	
		num_records = int(options_hash['num_records'])
		
		input_file = remainder[0]
		output_file = remainder[1]
		
		total_records = countProtosInFile(input_file)
		
		print 'Selecting %d records from %d total records.' % (num_records, total_records)
		random.seed()
		# Randomly select some records to use.
		records_to_use = set(random.sample(range(total_records), num_records))
		
		reader = ProtocolBufferFileReader(None, filename=input_file, return_byte_string_only=True)
		writer = ProtocolBufferFileWriter(filename=output_file, messages_are_byte_strings=True)

		for ii, message in enumerate(reader):
			if ii in records_to_use:
				writer.write(message)

		reader.close()
		writer.close()

if __name__ == '__main__':
	RandomlySelectProtos().run()
	
