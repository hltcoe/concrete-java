package edu.jhu.hlt.concrete.sql

import edu.jhu.hlt.acute.archivers.tar.TarArchiver
import edu.jhu.hlt.concrete.serialization.archiver.ArchivableCommunication
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.SQLException
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static java.lang.Thread.*

import static extension com.google.common.io.CharStreams.*

class CommunicationIDListRetriever implements AutoCloseable {
	static final val Logger LOGGER = LoggerFactory.getLogger(typeof(CommunicationIDListRetriever))
	
	final SQLiteImpl db
	final TarArchiver arch
	
	new (Path db, Path output) {
		this.db = new SQLiteImpl(db)
		if (Files.exists(output))
			throw new IllegalArgumentException("Output path exists. Not overwriting.")
		
		this.arch = new TarArchiver(new GzipCompressorOutputStream(Files.newOutputStream(output)))
	}
	
	override close() throws IOException, SQLException {
		this.db.close
		this.arch.close
	}
	
	def query(Path idFile) {
		val br = Files.newBufferedReader(idFile, StandardCharsets.UTF_8)
		br.readLines.forEach [ line | 
			val comm = this.db.get(line)
			if (comm.isPresent) {
				val act = comm.get
				this.arch.addEntry(new ArchivableCommunication(act))
			} else
			    LOGGER.info("Did not find comm: {}", line)
		]
		br.close
	}
	
	def static void main(String[] args) {
		Thread.defaultUncaughtExceptionHandler = new LoggedUncaughtExceptionHandler
		val db = Paths.get(args.get(0))
		val output = Paths.get(args.get(1))
		val fileWithIDsPerLine = Paths.get(args.get(2))
	
		if (!Files.exists(db)) {
			LOGGER.info("Database does not exist.")
			return
		}
		
		if (!Files.exists(fileWithIDsPerLine)) {
			LOGGER.info("File with IDs does not exist.")
			return
		}
		
		if (Files.exists(output)) {
			LOGGER.info("Output path exists. Not overwriting.")
			return
		}
		
		val retr = new CommunicationIDListRetriever(db, output)
		retr.query(fileWithIDsPerLine)
		retr.close
	}
}