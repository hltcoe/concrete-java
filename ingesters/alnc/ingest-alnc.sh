#!/usr/bin/env sh
ALNC_PATH=$1
OUTPUT_DIR=$2
if [[ -d "target" ]]; then
    find $ALNC_PATH -name "*.bz2" -type f | xargs -s 2000000 \
                                                java -XX:+UseG1GC -Xmx10G \
                                                -cp target/*.jar \
                                                edu.jhu.hlt.concrete.ingesters.alnc.ALNCIngesterRunner \
                                                --output-path "$OUTPUT_DIR" \
                                                $F
    echo "Finished."
else
    echo "Project is not build. Run:

    mvn clean compile assembly:single

from this directory first."
fi
