/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.serialization;

import static org.junit.Assert.assertEquals;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.jhu.hlt.acute.AutoCloseableIterator;
import edu.jhu.hlt.acute.archivers.Archivable;
import edu.jhu.hlt.acute.archivers.tar.TarArchiver;
import edu.jhu.hlt.concrete.UUID;
import edu.jhu.hlt.concrete.util.ConcreteException;
import edu.jhu.hlt.concrete.uuid.UUIDFactory;

public class BoundedThriftAPITest {

  Path outPath;

  @Rule
  public TemporaryFolder tf = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    outPath = tf.getRoot().toPath();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void boundedSerialization() throws ConcreteException {
    UUID uuid = new UUID();
    // whatever
    uuid.setUuidString("asdf");
    BoundedThriftSerializer<UUID> ser = new BoundedThriftSerializer<>(UUID.class);
    byte[] bytes = ser.toBytes(uuid);

    UUID fromBytes = ser.fromBytes(bytes);
    assertEquals(uuid.getUuidString(), fromBytes.getUuidString());
  }

  private class UUIDArchivable implements Archivable {

    private final UUID uuid;
    private final ThriftSerializer<UUID> ser = new ThreadSafeThriftSerializer<>();

    public UUIDArchivable (UUID uuid) {
      this.uuid = uuid;
    }

    @Override
    public String getFileName() {
      return this.uuid.getUuidString() + ".uuid";
    }

    @Override
    public byte[] getBytes() {
      try {
        return this.ser.toBytes(this.uuid);
      } catch (ConcreteException e) {
        // unlikely to throw.
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Test
  public void boundedTarSerialization() throws Exception {
    final Path outFile = outPath.resolve("test.tar");
    List<UUID> uuidList = new ArrayList<>(11);
    for (int i = 0; i < 10; i++)
      uuidList.add(UUIDFactory.newUUID());

    try (OutputStream os = Files.newOutputStream(outFile);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        TarArchiver archiver = new TarArchiver(bos);) {
      for (UUID uuid : uuidList)
        archiver.addEntry(new UUIDArchivable(uuid));
    }

    BoundedThriftSerializer<UUID> ser = new BoundedThriftSerializer<>(UUID.class);
    try(AutoCloseableIterator<UUID> iter = ser.fromTar(outFile);) {
      while (iter.hasNext())
        iter.next();
    }
  }
}
