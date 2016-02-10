#!/usr/bin/env sh
### An easy, single-input way to ingest all ACERE
### data to Communications.
### Takes 1 argument: path to LDC2006T06
### Outputs a .tar.gz with all ACERE communications,
### named 'acere-comms.tar.gz'
if [ $# -eq 1 ]; then
    # Create directory for symlinks.
    mkdir links-dir

    # Run prepare.sh
    sh prepare.sh $1 "links-dir"

    # Build the jar.
    mvn clean compile assembly:single

    # Run the program.
    java \
        -cp target/*.jar \
        edu.jhu.hlt.concrete.ingesters.acere.AceApf2Concrete \
        links-dir \
        acere-comms

    # Compress the communications.
    tar czf acere-comms.tar.gz acere-comms

    # Remove output dirs.
    rm -rf links-dir acere-comms

else
    echo "This program takes 1 argument: path to the LDC2006T06 directory."; exit 1;
fi
