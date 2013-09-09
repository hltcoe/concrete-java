# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
'''
Take a single file of protocol buffers and divide them into many shards.
This process works for any protocol buffer (supplied on the command line).
Splitting is done by file size, records per file, or total number of files.
- file size- measures the size of each protobuf as its being written.
- records per file- keeps a count of the number of records in each file.
  
  This script will also count the number of protobufs in a file.
'''

import sys, os.path

from concrete.utils import parseCommandLine, usage
from concrete.proto_io import ProtocolBufferFileReader, ProtocolBufferFileWriter, resolveProtoObjectFromString

class ProtobufSharder:
	def createShardFilename(self, output_shard_prefix, shard_number):
		return '%s_%.6d.shard' % (output_shard_prefix, shard_number)
		
	def run(self):
		# Specify options.
		options = [
					('max_shard_size=', 'The maximum size of each shard in bytes.'),
					('max_records_per_shard=', 'The maximum number of records in each shard.'),
					('count_records_only', 'Counts the number of protobufs in the file and exits.'),
					('output_shard_prefix=', 'REQUIRED (unless count_records_only): Creates shards starting with this file prefix.'),
					]
		# Start main method here.
	
		options_hash, remainder = parseCommandLine(options)

		if (len(remainder) != 1):
			command_line = '%s --output_shard_prefix=shard_prefix input_shard'
			print usage(sys.argv, command_line, options)
			sys.exit()
	
		num_options_specified = 0
		
		max_shard_size = None
		max_records_per_shard = None
		count_records_only = None
		if 'max_shard_size' in options_hash:
			max_shard_size = int(options_hash['max_shard_size'])
			num_options_specified += 1
		if 'max_records_per_shard' in options_hash:
			max_records_per_shard = int(options_hash['max_records_per_shard'])
			num_options_specified += 1
			print 'Using %d records per shard.' % (max_records_per_shard)
		if 'count_records_only' in options_hash:
			print 'Only counting records.'
			count_records_only = True
			num_options_specified += 1
		
		if (num_options_specified != 1):
			print 'Only one of the following options must be specified:'
			print '\t max_shard_size, max_records_per_shard, count_records_only'
			sys.exit()
		
		if not count_records_only and 'output_shard_prefix' not in options_hash:
			print 'output_shard_prefix is a required option.'
			sys.exit()
		
		if 'output_shard_prefix' in options_hash:
			output_shard_prefix = options_hash['output_shard_prefix']

		input_file = remainder[0]
		
		reader = ProtocolBufferFileReader(None, filename=input_file, return_byte_string_only=True)
		
		num_messages_written = 0
		bytes_written = 0
		total_num_messages = 0
		num_files_written = 1
		
		if count_records_only:
			writer = None
		else:
			writer = ProtocolBufferFileWriter(filename=self.createShardFilename(output_shard_prefix, num_files_written), messages_are_byte_strings=True)
			num_files_written += 1

		for message in reader:
			num_messages_written += 1
			total_num_messages += 1
			bytes_written += len(message) + 4 # +4 for the message size prefix
			
			if writer:
				writer.write(message)
		
			if (max_shard_size != None and bytes_written >= max_shard_size) or \
			   (max_records_per_shard != None and num_messages_written >= max_records_per_shard):
				bytes_written = 0
				num_messages_written = 0
				writer.close()
				writer = ProtocolBufferFileWriter(filename=self.createShardFilename(output_shard_prefix, num_files_written), messages_are_byte_strings=True)
				num_files_written += 1

		if writer:
			writer.close()

		num_files_written -= 1
		print 'Number of records in shard: %d' % total_num_messages
		if num_files_written != 0:
			print 'Number of files written: %d' % num_files_written

if __name__ == '__main__':
	sharder = ProtobufSharder()	
	sharder.run()
