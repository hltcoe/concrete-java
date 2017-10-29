#!/usr/bin/env sh
DATA_PATH=$1
OUTPUT_DIR=$2

echo "Input: $DATA_PATH"
echo "Output: $OUTPUT_DIR"

if [[ -d "target" ]]; then
    find $DATA_PATH -name "*[ENG|SPA|CMN]_NW*.xml" -type f | xargs -s 3000000 \
                                                  java -XX:+UseG1GC -Xmx10G \
                                                  -cp target/*.jar \
                                                  edu.jhu.hlt.concrete.ingesters.webposts.TACKBP2017WebPostIngester \
                                                  --output-path "$OUTPUT_DIR" \
                                                  $F
else
    echo "Project is not built. Run:

    mvn clean compile assembly:single

from this directory first."
fi
