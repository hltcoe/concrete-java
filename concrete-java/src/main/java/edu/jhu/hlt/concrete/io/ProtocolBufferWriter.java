/**
 * Copyright 2012-2013 Johns Hopkins University HLTCOE. All rights reserved.
 * This software is released under the 2-clause BSD license.
 * See LICENSE in the project root directory.
 */
package edu.jhu.hlt.concrete.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.Message;

/**
 * A generic protocol buffer writer
 * 
 * @author Delip Rao and Mark Dredze
 * 
 */
public class ProtocolBufferWriter {
    OutputStream outputStream = null;

    public ProtocolBufferWriter(OutputStream out) {
        this.outputStream = out;
    }

    public ProtocolBufferWriter(String outputShard) throws IOException {
        if (outputShard.endsWith(".gz"))
            this.outputStream = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outputShard)));
        else
            this.outputStream = new BufferedOutputStream(new FileOutputStream(outputShard));
    }
    
    public ProtocolBufferWriter(Path path) throws IOException {
        this(path.toString());
    }

    public void write(Message message) throws IOException {
        byte[] messageBytes = message.toByteString().toByteArray();
        int size = messageBytes.length;
	final int INT_SIZE = 4;
        ByteBuffer buffer = ByteBuffer.allocate(INT_SIZE);
        byte[] messageBytesSize = buffer.putInt(size).array();
        outputStream.write(messageBytesSize);
        outputStream.write(messageBytes);
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
