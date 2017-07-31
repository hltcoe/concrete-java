package edu.jhu.hlt.concrete.zip;

import org.apache.thrift.TException;

import java.nio.file.Paths;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * Launches a CommunicationFetchService that fetches communications from a Concrete zip archive.
 * @author Tongfei Chen
 * @since 4.12.0
 */
public class ConcreteZipArchiveFetchServiceLauncher {

    private static class Opts {
	@Parameter(description = "Zip file path", required = true)
	String zipPath;

	@Parameter(description = "Fetch service port", required = true)
	int port;

	@Parameter(help = true, names = { "--help", "-h" },
		   description = "Print the help message and exit.")
	boolean help = false;
    }

    public static void main(String[] args) throws java.io.IOException, TException, InterruptedException {
	Opts opts = new Opts();
	JCommander jc = new JCommander(opts);
	try {
	    jc.parse(args);
	} catch (ParameterException e) {
	    jc.usage();
	    System.exit(-1);
	}
	if (opts.help) {
	    jc.usage();
	    return;
	}

        ConcreteZipArchiveFetchService fs = new ConcreteZipArchiveFetchService(Paths.get(opts.zipPath));
        ConcreteZipArchiveFetchServiceThread fst = new ConcreteZipArchiveFetchServiceThread(fs, opts.port);

        Thread thread = new Thread(fst);
        thread.start();
        thread.join();
    }
}
