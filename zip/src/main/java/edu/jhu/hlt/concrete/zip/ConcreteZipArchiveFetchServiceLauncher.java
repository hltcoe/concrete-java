package edu.jhu.hlt.concrete.zip;

import org.apache.thrift.TException;

import java.nio.file.Paths;

/**
 * Launches a CommunicationFetchService that fetches communications from a Concrete zip archive.
 * @author Tongfei Chen
 * @since 4.12.0
 */
public class ConcreteZipArchiveFetchServiceLauncher {

    public static void main(String[] args) throws java.io.IOException, TException, InterruptedException {

        String zipPath = args[0];
        int port = Integer.parseInt(args[1]);

        ConcreteZipArchiveFetchService fs = new ConcreteZipArchiveFetchService(Paths.get(zipPath));
        ConcreteZipArchiveFetchServiceThread fst = new ConcreteZipArchiveFetchServiceThread(fs, port);

        Thread thread = new Thread(fst);
        thread.start();
        thread.join();

    }

}
