package edu.jhu.hlt.concrete.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.Message;

/**
 * A generic protocol buffer writer
 * 
 * @author Delip Rao
 * 
 */
public class ProtocolBufferWriter {
    OutputStream outputStream = null;

    public ProtocolBufferWriter(OutputStream out) {
        init(out);
    }

    public ProtocolBufferWriter(String outputShard) throws IOException {
        if (outputShard.endsWith(".gz"))
            init(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(outputShard))));
        else
            init(new BufferedOutputStream(new FileOutputStream(outputShard)));
    }

    private void init(OutputStream out) {
        outputStream = out;
    }

    public void write(Message message) throws IOException {
        byte[] messageBytes = message.toByteString().toByteArray();
        int size = messageBytes.length;
		final int LONG_SIZE = 8;
        ByteBuffer buffer = ByteBuffer.allocate(LONG_SIZE);
        byte[] messageBytesSize = buffer.putLong(size).array();
        outputStream.write(messageBytesSize);
        outputStream.write(messageBytes);
    }

    public void close() throws IOException {
        outputStream.close();
    }
}
