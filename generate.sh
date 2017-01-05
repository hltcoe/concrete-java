#!/usr/bin/env bash
PATH_TO_THRIFT_FILES=$1
THRIFT_OUT_DIR='core/src/main/java'
THRIFT_ARGS="-out $THRIFT_OUT_DIR --gen java:private-members,hashcode"
mkdir -p $THRIFT_OUT_DIR
rm -rf $THRIFT_OUT_DIR/*
for F in $PATH_TO_THRIFT_FILES/*.thrift; do
    thrift ${THRIFT_ARGS} "$F" || $(echo "Failed to generate Java classes based on thrift file $F"; exit 1)
done
