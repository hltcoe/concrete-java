package edu.jhu.hlt.concrete.zip;

import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

class ConcreteZipArchiveFetchServiceThread implements AutoCloseable, Runnable {

    private final TNonblockingServerTransport serverTransport;
    private final TServer server;
    private final TNonblockingServer.Args serverArgs;

    public ConcreteZipArchiveFetchServiceThread(FetchCommunicationService.Iface impl, int port) throws TException {
        serverTransport = new TNonblockingServerSocket(port);
        serverArgs = new TNonblockingServer.Args(serverTransport);
        serverArgs.protocolFactory(new TCompactProtocol.Factory());
        serverArgs.transportFactory(new TFramedTransport.Factory(Integer.MAX_VALUE));
        serverArgs.processorFactory(new TProcessorFactory(new FetchCommunicationService.Processor<>(impl)));
        serverArgs.maxReadBufferBytes = Long.MAX_VALUE;
        server = new TNonblockingServer(serverArgs);
    }

    public void run() {
        server.serve();
    }

    public void close() {
        server.stop();
        serverTransport.close();
    }

}

