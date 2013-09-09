# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
import getopt, sys

def containsOption(option, options):
	for entry in options:
		if entry[0].replace('=', '') == option:
			return True
	return False
	
def usage(args, command_line, options):
	help_option = ('help', 'Print this help message.', False)
	
	if not containsOption('help', options):
		options_with_help.append(help_option)

		
	return_string = command_line % (args[0])
	return_string += '\nOptions:\n'
	for entry in options:
		arg = entry[0]
		help = entry[1]
		required = False
		if len(entry) > 2:
			required = entry[2]
		return_string += '\t--%s (%s)' % (arg, help)
		if required:
			return_string += ' (Required)'
		return_string += '\n'
	
	return return_string
	
def parseCommandLine(options_with_help, command_line=''):
	options = []
	required_options = []
	help_option = ('help', 'Print this help message.', False)
	
	if not containsOption('help', options):
		options_with_help.append(help_option)
		
	for entry in options_with_help:
		option = entry[0]
		help = entry[1]
		required = False
		if len(entry) > 2:
			required = entry[2]
		
		options.append(option)
		required_options.append((option, required))
		
		
	optlist, remainder = getopt.getopt(sys.argv[1:], '', options)
	
	parameter_hash = {}
	# Put the optlist into a hash.
	for parameter, value in optlist:
		if (parameter.startswith('--')):
			parameter = parameter[2:]
		if (parameter.endswith('=')):
			parameter = parameter[:-1]
		parameter_hash[parameter] = value
	
	
	missing_option = False
	for option, required in required_options:
		if required:
			option = option.replace('=', '')
			if option not in parameter_hash:
				missing_option = True
				print 'Missing required option: %s' % option

	if missing_option or 'help' in parameter_hash:
		print usage(sys.argv, command_line, options_with_help)
		sys.exit()
		
	if missing_option:
		sys.exit()
		
		
	return parameter_hash, remainder


'''
Based on: http://cse-mjmcl.cse.bris.ac.uk/blog/2007/02/14/1171465494443.html

This method ensures that the output String has only
valid XML unicode characters as specified by the
XML 1.0 standard. For reference, please see
<a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
 standard</a>. This method will return an empty
String if the input is null or empty.
'''
def stripNonValidXMLCharacters(text):
	new_text = [] # Used to hold the new text.

	if text == None or text == '':
		return text
	
	for char in text:
		current = ord(char)
		if ((current == 0x9) or
			(current == 0xA) or
			(current == 0xD) or
			((current >= 0x20) and (current <= 0xD7FF)) or
			((current >= 0xE000) and (current <= 0xFFFD)) or
			((current >= 0x10000) and (current <= 0x10FFFF))):
			new_text.append(char)
	
	new_string = ''.join(new_text)

	return new_string