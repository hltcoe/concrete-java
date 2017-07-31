package edu.jhu.hlt.concrete.zip;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.access.FetchCommunicationService;
import edu.jhu.hlt.concrete.access.FetchRequest;
import edu.jhu.hlt.concrete.access.FetchResult;
import edu.jhu.hlt.concrete.serialization.CompactCommunicationSerializer;
import edu.jhu.hlt.concrete.services.NotImplementedException;
import edu.jhu.hlt.concrete.services.ServiceInfo;
import edu.jhu.hlt.concrete.services.ServicesException;
import edu.jhu.hlt.concrete.util.ConcreteException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.thrift.TException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes a zip archive containing communications as a FetchCommunicationService.
 * @author Tongfei Chen
 * @since 4.12.0
 */
public class ConcreteZipArchiveFetchService implements FetchCommunicationService.Iface {

    ZipFile zf;
    CompactCommunicationSerializer ser = new CompactCommunicationSerializer();

    public ConcreteZipArchiveFetchService(Path path) throws IOException {
        zf = new ZipFile(path.toAbsolutePath().toString());
    }

    public FetchResult fetch(FetchRequest r) throws TException {
        List<String> ids = r.getCommunicationIds();
        List<Communication> comms = new ArrayList<>();
        for (String id : ids) {
            ZipArchiveEntry zae = zf.getEntry(id + ".concrete");
            if (zae == null) zae = zf.getEntry(id + ".comm");
            if (zae != null) {
                System.out.println(id + " fetched.");
                comms.add(Util.read(zf, zae));
            }
            else {
                System.out.println(id + " not found.");
            }
        }
        return new FetchResult(comms);
    }

    public List<String> getCommunicationIDs(long l, long r) throws NotImplementedException, TException {
        throw new NotImplementedException("Cannot get a slice from a zip archive.");
    }

    public long getCommunicationCount() throws TException {
        return Util.enumerationAsStream(zf.getEntries())
                .filter(e -> e.getName().endsWith(".comm") || e.getName().endsWith(".concrete"))
                .count();
    }

    public ServiceInfo about() throws TException {
        return new ServiceInfo(this.getClass().getName(), "4.12.0");
    }

    public boolean alive() throws TException {
        return true;
    }
}
