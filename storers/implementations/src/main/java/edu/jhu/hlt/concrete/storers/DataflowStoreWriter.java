package edu.jhu.hlt.concrete.storers;

import org.apache.thrift.TException;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.StoreCommunicationService;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.concrete.services.store.StoreServiceWrapper;
import groovyx.gpars.dataflow.DataflowWriteChannel;

/**
 *
 */
public class DataflowStoreWriter implements StoreCommunicationService.Iface {
  private final DataflowWriteChannel<Communication> writeChannel;
  private final String desc;

  private static final String defaultDescription = "DataflowStoreWriter";

  public DataflowStoreWriter(DataflowWriteChannel<Communication> writeChan) {
    this(writeChan, defaultDescription);
  }

  public DataflowStoreWriter(DataflowWriteChannel<Communication> writeChan, String desc) {
    this.writeChannel = writeChan;
    this.desc = desc;
  }

  @Override
  public ServiceInfo about() throws TException {
    ServiceInfo si = new ServiceInfo();
    si.setVersion("latest");
    si.setDescription(this.desc);
    si.setName("DataflowStoreWriter");
    return si;
  }

  @Override
  public boolean alive() throws TException {
    return true;
  }

  @Override
  public void store(Communication arg0) throws ServicesException, TException {
    this.writeChannel.bind(arg0);
  }

  /**
   * @param port the port to listen on
   * @return a {@link StoreServiceWrapper}
   * @throws TException
   */
  public StoreServiceWrapper wrapper(int port) throws TException {
    return new StoreServiceWrapper(this, port);
  }
}
