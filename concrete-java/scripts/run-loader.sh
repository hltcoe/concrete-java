#!/bin/bash
cd ..
INCLUDE=`ls target/ | grep dependencies`
echo "INCLUDE: ${INCLUDE}"
java -cp .:target/${INCLUDE} edu.jhu.hlt.concrete.kb.LoadConcreteTACKBFiles $1 $2
cd -
