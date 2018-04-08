#!/usr/bin/env sh

DIR=$(pwd)
JAR=$(find $DIR/target/ -name '*uberjar*.jar')
java -cp .:$JAR edu.jhu.hlt.concrete.services.summarization.SummarizationTool "$@"
