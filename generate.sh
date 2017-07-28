#!/usr/bin/env bash

set -e

CONCRETE_THRIFT_VERSION='0.9.3'

AVAILABLE_THRIFT_VERSION=$(thrift -version 2>/dev/null | grep -F "${CONCRETE_THRIFT_VERSION}" | wc -l | sed -e 's/^ *//' -e 's/ *$//')
if [[ "${AVAILABLE_THRIFT_VERSION}" != '1' ]] ; then
    echo "Thrift must be present and its version must be $CONCRETE_THRIFT_VERSION. Exiting."
    exit 1
fi

PATH_TO_THRIFT_FILES=$1
THRIFT_OUT_DIR='core/src/main/java'
THRIFT_ARGS="-out $THRIFT_OUT_DIR --gen java:private-members,hashcode,generated_annotations=undated"
mkdir -p $THRIFT_OUT_DIR
rm -rf $THRIFT_OUT_DIR/*
for F in $PATH_TO_THRIFT_FILES/*.thrift; do
    thrift ${THRIFT_ARGS} "$F" || $(echo "Failed to generate Java classes based on thrift file $F"; exit 1)
done
