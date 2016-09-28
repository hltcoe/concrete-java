#!/usr/bin/env sh

#########################################################
### Takes in a path of .tar.gz files, an output file,
### and a jarfile.
### Returns a built SQLite db with all communications.
#########################################################

TARGZDIR=$1
DBFILE=$2
JARFILE=$3
for F in $(find "$TARGZDIR" -type f); do
    echo "Working file: $F"
    java \
        -cp $JARFILE \
        edu.jhu.hlt.concrete.sql.SQLiteImpl \
        $DBFILE \
        $F
done
