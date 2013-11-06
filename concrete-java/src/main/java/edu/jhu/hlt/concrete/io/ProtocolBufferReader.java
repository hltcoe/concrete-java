/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import com.google.protobuf.Message;

import edu.jhu.hlt.concrete.ConcreteException;

/**
 * A generic protocol buffer reader class
 * 
 * @author Delip Rao, Mark Dredze and John Sullivan
 * 
 */
public class ProtocolBufferReader<M extends Message> implements Closeable, Iterator<M> {
  InputStream inputStream = null;
  Object messageObject = null;
  Class<M> messageClass = null;
  int lastBytesRead = 0;
  int totalBytesRead = 0;
  protected M nextMessage = null;

  /**
   * 
   * @param in
   *          - input stream to read the protocol buffers from (network/disk/memory etc)
   * @param messageClass
   *          - The protocol buffer consists of one or more instances of this class. It must match the parameterized type of the reader.
   * @throws ConcreteException
   */
  public ProtocolBufferReader(InputStream in, Class<M> messageClass) throws ConcreteException {
    try {
      init(in, messageClass);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new ConcreteException(e);
    }
  }

  public ProtocolBufferReader(String in, Class<M> messageClass) throws Exception {
    if (in.endsWith(".gz"))
      init(new BufferedInputStream(new GZIPInputStream(new FileInputStream(in))), messageClass);
    else
      init(new BufferedInputStream(new FileInputStream(in)), messageClass);
  }
  
  protected void init(InputStream in, Class<M> messageClass) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, ConcreteException {
    inputStream = in;
    Method messageClassLoader = messageClass.getMethod("getDefaultInstance", (Class[]) null);
    messageObject = messageClassLoader.invoke(null, (Object[]) null);
    this.messageClass = messageClass;
    this.nextMessage = this.getNextMessage();
  }

  /**
   * 
   * @return the next message in the protocol buffer
   * @throws NoSuchElementException
   */
  public M next() throws NoSuchElementException {
    try {
      M nextMessage = this.nextMessage;
      this.nextMessage = this.getNextMessage();

      return nextMessage;
    } catch (ConcreteException cE) {
      throw new NoSuchElementException(cE.getMessage());
    }
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("You cannot remove with this iterator.");
  }

  public boolean hasNext() {
    boolean hasNextp = this.nextMessage != null;
    // if there are no more messages we can close the file
    if (!hasNextp)
      try {
        this.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    return hasNextp;
  }

  protected M getNextMessage() throws ConcreteException {
    final int INT_SIZE = 4;
    lastBytesRead = 0;
    byte[] messageSizeBytes = new byte[INT_SIZE];
    try {
      if (inputStream.read(messageSizeBytes) < INT_SIZE) {
        return null;
      }
      int messageSize = new BigInteger(messageSizeBytes).intValue();
      assert messageSize >= 0 : "overflow: " + messageSize;
      lastBytesRead = INT_SIZE;
      if (messageSize < 1)
        return null;
      byte[] messageBytes = new byte[messageSize];
      if (inputStream.read(messageBytes) < messageSize)
        return null;
      lastBytesRead += messageSize;

      this.totalBytesRead += lastBytesRead;

      Method newBuilderMethod = messageClass.getMethod("newBuilder", (Class[]) null);
      Message.Builder builder = (Message.Builder) newBuilderMethod.invoke(null, (Object[]) null);

      return (M) builder.mergeFrom(messageBytes).build();
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
      throw new ConcreteException(e);
    }
  }

  /**
   * Internal method. Use it only if you know what you're doing.
   * 
   * @return number of bytes read from the inputstream during last successful next() call.
   */
  public int getLastBytesRead() {
    return lastBytesRead;
  }

  /**
   * Similar to getLastBytesRead(). This returns the total number of bytes read by this reader.
   * 
   * @return
   */
  public int getTotalBytesRead() {
    return this.totalBytesRead;
  }

  /**
   * 
   * @param byteOffset
   *          - The offset from start where to read the message
   * @return message at the specified byte offset if there exists one or null
   * @throws Exception
   */
  public M messageAtOffset(long byteOffset) throws Exception {
    inputStream.skip(byteOffset);
    return next();
  }

  /**
   * Attempt to close the input stream associated with the reader
   * 
   * @throws Exception
   */
  public void close() throws IOException {
    inputStream.close();
  }

}
