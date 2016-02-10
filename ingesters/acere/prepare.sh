#!/usr/bin/env sh
# The LDC2006T06 path is the first argument.  The temporary directory
# for the .dtd and symlinks is the second argument.
if [ $# -eq 2 ]; then
    LDC_PATH=$1
    OUT_DIR=$2
    if [ ! -d $OUT_DIR ]; then
        echo "Creating output dir: $OUT_DIR"
        mkdir -p $OUT_DIR
    fi
    if [ -d $LDC_PATH ]; then
        DTD_PATH="$LDC_PATH/dtd/apf.v5.1.1.dtd"
        # Create a link from the .dtd to the out dir.
        ln -s $DTD_PATH $OUT_DIR
        # 'adj' path.
        ADJ_PATH="$LDC_PATH/data/English/*/adj"
        # Create links from the 'adj' dirs to the out dir.
        ln -s $ADJ_PATH/*.sgm $OUT_DIR
        ln -s $ADJ_PATH/*.apf.xml $OUT_DIR
    else
        echo "$LDC_PATH is not a directory."; exit 1;
    fi
else
    echo "This program takes 2 arguments: path to the LDC2006T06
    directory and path to the directory to store symbolic links.";
    exit 1;
fi
