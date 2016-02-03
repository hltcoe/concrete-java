/*
 * Copyright 2012-2016 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.streams;

import java.io.IOException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Consume concrete {@link Communication}s from a named pipe.
 */
public class NamedPipeCommunicationConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NamedPipeCommunicationConsumer.class);

  private final TSimpleFileTransport tft;
  private final TCompactProtocol tcp;

  /**
   * @param pathStr a path to a named pipe with {@link Communication}s
   * @throws IOException
   */
  public NamedPipeCommunicationConsumer(final String pathStr) throws IOException {
    try {
      this.tft = new TSimpleFileTransport(pathStr);
      this.tft.open();
      this.tcp = new TCompactProtocol(tft);
    } catch (TTransportException e) {
      throw new IOException(e);
    }
  }

  /**
   *
   * @return true if there are {@link Communication}s left in the pipe or it is
   *         being written to
   */
  public boolean isOpen() {
    return this.tft.peek();
  }

  /**
   *
   * @return the next {@link Communication} from the pipe
   * @throws ConcreteException
   *           if there is an error with the serialization or transport of the
   *           communication
   */
  public Communication next() throws ConcreteException {
    Communication c = new Communication();
    try {
      c.read(this.tcp);
      return c;
    } catch (TException e) {
      throw new ConcreteException(e);
    }
  }

  /**
   * @param args
   * @throws IOException
   * @throws ConcreteException
   */
  public static void main(String[] args) throws IOException, ConcreteException {
    String filePathStr = args[0];
    NamedPipeCommunicationConsumer con = new NamedPipeCommunicationConsumer(filePathStr);
    LOGGER.info("Is it open? {}", con.isOpen());
    while (con.isOpen()) {
      LOGGER.info("Got ID: {}", con.next().getId());
    }
  }
}
