# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.

'''
Count the number of proto buffers in a file.
'''

import sys

from concrete.utils import parseCommandLine, usage
from concrete.proto_io import countProtosInFile

class CountProtoBufs:
	def countFiles(self, files, verbose=False):
		total_records = 0
		for file in files:
			num_records = countProtosInFile(file)
			if verbose:
				print '%s\t%d' % (file, num_records)
			total_records += num_records
			
		if len(files) > 1 and verbose:
			print 'Total: %d' % (total_records)
		return total_records
		
	def run(self):
		# Specify options.
		options = [
					]
		# Start main method here.
	
		options_hash, remainder = parseCommandLine(options)

		if (len(remainder) < 1):
			command_line = '%s input_shard*'
			print usage(sys.argv, command_line, options)
			sys.exit()
	
		self.countFiles(remainder, verbose=True)

if __name__ == '__main__':
	counter = CountProtoBufs()	
	counter.run()
