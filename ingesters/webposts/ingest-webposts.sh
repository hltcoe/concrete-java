#!/usr/bin/env sh
DATA_PATH=$1
OUTPUT_DIR=$2
if [[ -d "target" ]]; then
    find $DATA_PATH -name "[cmn|eng]*.xml" -type f | xargs -s 2000000 \
                                                java -XX:+UseG1GC -Xmx10G \
                                                -cp target/*.jar \
                                                edu.jhu.hlt.concrete.ingesters.webposts.WebPostIngesterRunner \
                                                --output-path "$OUTPUT_DIR" \
                                                $F
else
    echo "Project is not build. Run:

    mvn clean compile assembly:single

from this directory first."
fi
