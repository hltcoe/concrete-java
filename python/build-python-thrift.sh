#!/bin/bash

#
# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
#

#
# This script will compile the concrete-thrift Python code and install
# the files in the 'concrete' directory.
#
# Usage: build-python-thrift.sh </absolute/path/to/thrift/files>
#

if [ $# != 1 ]
then
    echo "Usage: build-python-thrift.sh </absolute/path/to/thrift/files>"
    exit 1
fi

for P in `find $1 -name '*.thrift'`
do
    # Create a 'concrete' directory in the current directory,
    # for consistency with Python packaging defaults
    thrift --gen py:new_style,utf8strings --out . $P
done

# Delete extraneous file generated by thrift
rm __init__.py