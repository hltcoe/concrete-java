#!/bin/bash
cd ..
INCLUDE=`ls target/ | grep dependencies`
echo "INCLUDE: ${INCLUDE}"
java -cp .:target/${INCLUDE} edu.jhu.hlt.concrete.kb.TAC09KB2Concrete $1 $2
cd -
