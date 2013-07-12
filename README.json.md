JsonUtil
========

Overview
--------
The JsonUtil class provides utility functions for converting Concrete objects
to and from Json. The functional class of JsonUtil is JsonCommunication.
JsonCommunication holds the converted data members from the Concrete objects.
For example in Concrete, Communications have SectionSegmentations and 
SectionSegmentations have Sections and Sections have TextSpans.

The equivalent of SectionSegmentation in JsonCommunication is Body. The
equivalent of Section in JsonCommunication is BodySection. JsonCommunication 
does not have TextSpans because it stores the text explicitly in the json
string.

Functions
---------
Given a Concrete Communication file in Gzip format you can:
*getJsonCommunicationsFromGzip
*getJsonStringsFromGzip

Given a Json string you can:
*toJsonCommunicationFromWellFormed
*toJsonCommunicationFromUnknown

Given a JsonCommunication object you can:
*toConcreteEmail

To Json from Concrete
---------------------
getJsonStringsFromGzip(filename):
	Given a gzipped concrete file you can get a list of strings that are each
	the Json representation of the Concrete Communication.

To Concrete from Json
---------------------
saveConcreteFromJson(jsonString,fileOut):
	Given a json string in the JsonCommunication format, save the Concrete
	equivalent to fileOut where fileOut is a string of the filename you
	wish to save to.

Commandline Usage
================
JsonUtil handles concrete objects and converts them to and from json.
Specify the functions you would like to perform.
If going to Concrete, include a json filename and desired concrete
filename for output.
If going from Concrete, include a concrete filename alone.

Commandline arguments
---------------------
-fc,--from-concrete Given a concrete file, print the json
representation.
-h,--help dislay this help and exit
-jp,--just-passages Given a concrete file, return just the sections
marked as PASSAGE.(not yet implemented)
-js,--just-sentences Given a concrete file, return just the sentences
in json format.(not yet implemented)
-tc,--to-concrete Given a json file (A file that contains a json
string on the first line), save the concrete
object equivalent.
-uuid,--uuid Specify the exact uuid of the Concrete object.(not
yet implemented)
-v,--validate If to-concrete then check the key/value pairs.


