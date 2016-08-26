package edu.jhu.hlt.concrete.lucene;

import java.io.IOException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.miscommunication.MiscCommunication;

public interface LuceneCommunicationIndexer extends AutoCloseable {

  public void add(MiscCommunication mc) throws IOException;

  public void commit() throws IOException;

  default public void add(Communication c) throws IOException {
    this.add(MiscCommunication.create(c));
  }

  @Override
  public void close() throws IOException;
}