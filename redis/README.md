concrete-redis
===

This project has two main purposes:

1. Expose an API for consuming redis push-pull operations
   in java.
2. Expose a main method for running a Twitter Tokenizer over
   data hosted in one redis instance, adding it to another.

## Build

``` shell
mvn clean compile assembly:single
```

## Example code

### An an executable program

#### Running TwitterTokenizer over redis list

**You need to edit the qsub script before running this**

Use [this script](src/main/scripts/qsub/tokenizer.sh). Edit it
with a path to your configuration and your log4j file. A default can be found
here on the grid:

``` shell
### redis conf
/export/common/max/conf/concrete-redis.conf

### log4j conf
/export/common/max/conf/log4j2-warn.json
```

This launches a qsub job that pulls tweets (as `Communication` files)
from the 'pull' redis, runs the TwitterTokenizer over them, then
pushes them to the 'push' redis.

You can launch many of these 'mappers'; they are atomic. For example,
to obtain higher throughput, you could run the above 5 times for 5
mappers.

You can also use the class
[here](src/main/java/edu/jhu/hlt/concrete/redis/PullPushTwitterTokenizer.java)
as a standalone main method to achieve the same effect.

### API

#### Configuration

The [config file](src/main/resources/reference.conf) should be edited
before running. You can edit it in place before building, or add
`application.conf` to the classpath; it will override
it. [See this](https://github.com/typesafehub/config) for more info.

The fields should be pretty self-explanatory, but think of pull as the
incoming data, and push as the outgoing data. Container can be either
'list' or 'set'. The limit is how many entries can be in a particular
key at once. The sleep interval is how often this is polled.

#### Classes

The `ConcreteRedisPushConfig` and `ConcreteRedisPullConfig` classes
can both provide a `JedisPool` instance. From that, one can obtain a
`Jedis` instance, and so
forth. [Jedis API](https://github.com/xetorthio/jedis)
