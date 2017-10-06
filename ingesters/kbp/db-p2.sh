#!/usr/bin/env bash
DIR=$(pwd)
JAR=$(find $DIR/target/ -name 'concrete*uber*.jar')
# echo "Found jar: $JAR"
java -cp $JAR \
     edu.jhu.hlt.concrete.ingesters.kbp2017.concrete.DBCreationPhaseTwo \
     "$@"
