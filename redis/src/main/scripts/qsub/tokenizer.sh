#!/usr/bin/env sh
#$ -j y                        # join stderr to stdout
#$ -V                          # job has the same environment variables as the submission shell
#$ -l h_rt=499:00:00            # runtime limit
#$ -l mem_free=8G             # expected amount of mem
#$ -l h_vmem=8G             # expected amount of mem
#$ -l num_proc=8
#$ -o /export/common/max/job-logs/concrete/redis    # log here
#$ -S /home/hltcoe/mthomas/local/bin/zsh
#$ -b y                         # run command line vs batch (?)

JARP=$1
java \
    -XX:+UseConcMarkSweepGC \
    -XX:ParallelCMSThreads=4 \
    -XX:ReservedCodeCacheSize=250M \
    -XX:+TieredCompilation \
    -XX:-UseGCOverheadLimit \
    -XX:+CMSClassUnloadingEnabled \
    -Xmx3G \
    -Dconfig.file=/export/common/max/conf/concrete-redis.conf \
    -Dlog4j.configurationFile=/export/common/max/conf/qsub-socket.xml \
    -cp $JARP \
    edu.jhu.hlt.concrete.redis.PullPushTwitterTokenizer \
# edu.jhu.hlt.scion.concrete.ingest.TTwitterRedisIngester \
# --redis-port 33033 \
# --redis-host r8n09.cm.cluster \
# --key 'coe-twitter:list-queue'

if [[ $? -ne 0 ]]; then
    echo "Tokenizer job failed."
else
    echo "Tokenizer job succeeded."
fi
