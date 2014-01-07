#!/bin/bash

#
# Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
# This software is released under the 2-clause BSD license.
# See LICENSE in the project root directory.
#

#
# This script will build a 'gen-py' directory with compiled concrete-thrift python code.
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
    thrift -gen py $P
done
