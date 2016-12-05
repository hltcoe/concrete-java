#!/usr/bin/env bash

DIR=`dirname $0`
JAR=$(find $DIR/target/ -name '*.jar')
java -cp .:$JAR edu.jhu.hlt.concrete.services.search.SearchTool "$@"
