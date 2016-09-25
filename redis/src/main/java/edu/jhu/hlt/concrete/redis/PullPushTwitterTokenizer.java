package edu.jhu.hlt.concrete.redis;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.tift.Tokenizer;
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class PullPushTwitterTokenizer implements AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(PullPushTwitterTokenizer.class);

  private static final CommunicationSerializer cs;
  static {
    cs = new CompactCommunicationSerializer();
  }

  private final int sleepTime;
  private final int pushLimit;
  private final byte[] pullKey;
  private final byte[] pushKey;
  private final boolean isPullContainerSet;

  private final JedisPool pullJp;
  private final JedisPool pushJp;

  private final Jedis pull;
  private final Jedis push;

  public PullPushTwitterTokenizer() throws URISyntaxException {
    ConcreteRedisConfig cfg = new ConcreteRedisConfig();
    this.sleepTime = cfg.getSleepTime();
    ConcreteRedisPullConfig pullCfg = cfg.getPullConfig();
    this.pullJp = pullCfg.getJedisPool();
    this.pullKey = pullCfg.getKey().getBytes();
    this.isPullContainerSet = pullCfg.getContainer().equalsIgnoreCase("set");

    ConcreteRedisPushConfig pushCfg = cfg.getPushConfig();
    this.pushJp = pushCfg.getJedisPool();
    this.pushKey = pushCfg.getKey().getBytes();
    this.pushLimit = pushCfg.getLimit();

    this.push = this.pushJp.getResource();
    this.pull = this.pullJp.getResource();
  }

  public long pullTokenizePush() throws InterruptedException, ConcreteException {
    byte[] pulled = this.isPullContainerSet ? pull.spop(this.pullKey) : pull.lpop(this.pullKey);
    if (pulled != null) {
      Communication c = cs.fromBytes(pulled);
      Tokenizer.TWITTER.addSectionSentenceTokenizationInPlace(c);
      while (push.llen(this.pushKey) > this.pushLimit)
        Thread.sleep(sleepTime);

      return push.lpush(this.pushKey, cs.toBytes(c));
    } else {
      Thread.sleep(this.sleepTime);
      return push.llen(this.pushKey);
    }
  }

  @Override
  public void close() {
    this.pull.close();
    this.push.close();

    this.pullJp.close();
    this.pushJp.close();

    this.pullJp.destroy();
    this.pushJp.destroy();
  }

  public static void main (String... args) {
    Thread.setDefaultUncaughtExceptionHandler(new LoggedUncaughtExceptionHandler());

    long ctr = 0;
    final long mod = 10 * 1000;
    try (PullPushTwitterTokenizer tt = new PullPushTwitterTokenizer()) {
      while (true) {
        tt.pullTokenizePush();
        ctr++;

        if (ctr % mod == 0)
          LOGGER.info("Processed {} communications.", ctr);
      }

    } catch (URISyntaxException e) {
      LOGGER.error("URI is invalid: {}", e.getMessage());
    } catch (InterruptedException e) {
      LOGGER.error("Interrupted.", e);
    } catch (ConcreteException e) {
      LOGGER.error("Caught ConcreteException.", e);
    }
  }
}
