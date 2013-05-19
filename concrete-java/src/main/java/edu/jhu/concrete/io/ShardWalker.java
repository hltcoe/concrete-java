package edu.jhu.concrete.io;

import com.google.protobuf.Message;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An abstract class for processing each entry in a shard
 * 
 * @author Delip Rao
 */
public abstract class ShardWalker {
    protected ProtocolBufferReader reader = null;
    protected ProtocolBufferWriter writer = null;

    private int messagesProcessed = 0;
    private int debugInterval = Integer.MAX_VALUE;

    public ShardWalker(InputStream in, Class<?> messageClass, OutputStream out) throws Exception {
        assert (in != null);
        assert (messageClass != null);

        reader = new ProtocolBufferReader(in, messageClass);
        if (out != null)
            writer = new ProtocolBufferWriter(out);
    }

    public ShardWalker(String in, Class<?> messageClass, String out) throws Exception {
        assert (in != null);
        assert (messageClass != null);

        reader = new ProtocolBufferReader(in, messageClass);
        if (out != null)
            writer = new ProtocolBufferWriter(out);
    }

    public final void setDebugInterval(int debugInterval) {
        this.debugInterval = debugInterval;
    }

    public void displayDebugInfo() {
        System.err.println("Processed messages " + messagesProcessed);
    }

    public final void walk() throws Exception {
        Message msg = null;
        while ((msg = reader.next()) != null) {
            msg = process(msg);
            messagesProcessed++;
            outputMessage(msg);
            if (messagesProcessed % debugInterval == 0)
                displayDebugInfo();
            if (continueWalk() == false)
                break;
        }
        if (writer != null)
            writer.close();
        reader.close();
        if (debugInterval != Integer.MAX_VALUE)
            displayDebugInfo();
    }

    public final void outputMessage(Message m) throws Exception {
        if (writer != null && m != null)
            writer.write(m);
    }

    /**
     * Override this method to make your walker abort a walk early
     * 
     * @see DocumentSubsetSelector for an example
     * 
     * @return
     */
    public boolean continueWalk() {
        return true;
    }

    /**
     * Returns the total number of bytes read by the underlying reader. If this
     * is called within process, it includes the bytes used to read the current
     * message.
     * 
     * @return
     */
    public int getTotalBytesRead() {
        return this.reader.getTotalBytesRead();
    }

    /**
     * Do something with the message and return a message
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public abstract Message process(Message message) throws Exception;

}
