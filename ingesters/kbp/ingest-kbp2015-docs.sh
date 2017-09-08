#!/bin/bash
set -eu
DATA_PATH=$1  # e.g. /export/common/data/corpora/LDC/LDC2015E77
OUTPUT_DIR=$2
if [[ -d "../bolt/target" ]]; then

  MPDF=$DATA_PATH/data/mpdf
  mkdir -p $OUTPUT_DIR/mpdf
  java -ea -XX:+UseG1GC -Xmx4G -cp '../bolt/target/*' \
    edu.jhu.hlt.concrete.ingesters.bolt.BoltIngesterRunner \
    --output-path $OUTPUT_DIR/mpdf \
    $MPDF

  echo "TODO: figure out how to ingest $DATA_PATH/data/nw data, GigawordIngesterRunner crashes..."

else
  echo "Project is not build. Run:

    (cd ../bolt && mvn clean compile assembly:single)

from this directory first."
fi

