#!/usr/bin/env sh
#$ -j y                        # join stderr to stdout
#$ -V                          # job has the same environment variables as the submission shell
#$ -l h_rt=499:00:00            # runtime limit
#$ -l mem_free=16G             # expected amount of mem
#$ -l h_vmem=16G             # expected amount of mem
#$ -l num_proc=8
#$ -o /export/common/max/job-logs/concrete/redis    # log here
#$ -S /home/hltcoe/mthomas/local/bin/zsh
#$ -b y                         # run command line vs batch (?)

JARP=$1
java \
    -XX:+UseConcMarkSweepGC \
    -XX:ParallelCMSThreads=3 \
    -XX:ReservedCodeCacheSize=250M \
    -XX:+TieredCompilation \
    -XX:-UseGCOverheadLimit \
    -XX:+CMSClassUnloadingEnabled \
    -Xmx7G \
    -Dconfig.file=/export/common/max/conf/concrete-redis.conf \
    -Dlog4j.configurationFile=/export/common/max/conf/log4j2-warn.json \
    -cp $JARP \
    edu.jhu.hlt.concrete.redis.PullPushTwitterTokenizer

### for max's recollection
# -Dlog4j.configurationFile=/export/common/max/conf/qsub-socket.xml \

if [[ $? -ne 0 ]]; then
    echo "Tokenizer job failed."
else
    echo "Tokenizer job succeeded."
fi
