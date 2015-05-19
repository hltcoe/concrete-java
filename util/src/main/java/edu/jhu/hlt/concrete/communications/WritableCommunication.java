/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.communications;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.CommunicationSerializer;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Small wrapper around {@link Communication} that allows trivial writing to a {@link Path}
 * for saving to disk.
 * <br>
 * <br>
 * As of version 11, this is intended as a drop-in replacement for now-deprecated {@link SuperCommunication#writeToFile(Path, boolean)}
 * and similar, which will be removed in a later release.
 */
public class WritableCommunication {

  private final Communication comm;
  private final CommunicationSerializer ser;

  /**
   *
   */
  public WritableCommunication(final Communication orig) {
    this.comm = new Communication(orig);
    this.ser = new CompactCommunicationSerializer();
  }

  /**
   * Take in a {@link Path} to an output file, and whether or not to delete the file at that path if it already exists, and output a byte array that represents
   * a serialized {@link Communication} object.
   *
   * @param path
   *          - a {@link Path} to the destination of the serialized {@link Communication}.
   * @param deleteExisting
   *          - whether to delete the file at path, if it exists.
   * @throws ConcreteException
   *           if there are {@link IOException}s or {@link TException}s.
   */
  public void writeToFile(Path path, boolean deleteExisting) throws ConcreteException {
    try {
      if (deleteExisting)
        Files.deleteIfExists(path);
      else if (Files.exists(path))
        throw new ConcreteException("File exists at: " + path.toString() + ". Delete it, or " + "call this method with the second parameter set to 'true'.");

      byte[] bytez = this.ser.toBytes(this.comm);
      try(OutputStream os = Files.newOutputStream(path);
          BufferedOutputStream bout = new BufferedOutputStream(os, 1024 * 8 * 24);) {
        bout.write(bytez);
      }
    } catch (IOException e) {
      throw new ConcreteException(e);
    }
  }

  /**
   * Wrapper around {@link #writeToFile(Path, boolean)} that takes a {@link String} instead of a {@link Path}.
   *
   * @see #writeToFile(Path, boolean)
   *
   * @param pathString
   * @param deleteExisting
   *          - whether to delete the file at path, if it exists.
   * @throws ConcreteException
   *           if there are {@link IOException}s or {@link TException}s.
   */
  public void writeToFile(String pathString, boolean deleteExisting) throws ConcreteException {
    this.writeToFile(Paths.get(pathString), deleteExisting);
  }

}
