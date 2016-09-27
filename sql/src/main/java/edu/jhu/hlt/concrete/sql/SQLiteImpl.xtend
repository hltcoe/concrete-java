package edu.jhu.hlt.concrete.sql

import edu.jhu.hlt.concrete.Communication
import edu.jhu.hlt.concrete.serialization.TarGzCompactCommunicationSerializer
import edu.jhu.hlt.concrete.util.ConcreteException
import edu.jhu.hlt.utilt.ex.LoggedUncaughtExceptionHandler
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Optional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static java.lang.Thread.*

class SQLiteImpl implements AutoCloseable {
	
	static final val Logger LOGGER = LoggerFactory.getLogger(typeof(SQLiteImpl))
	
	static final TarGzCompactCommunicationSerializer cs = new TarGzCompactCommunicationSerializer

	final Path p
	final Connection conn
	final PreparedStatement ps
	
	new (String pathStr) {
		this(Paths.get(pathStr))
	}
	
	new(Path p) {
		this.p = p
		val exists = Files.exists(p)
		if (!exists) {
			LOGGER.info("Creating db file at: {}", p.toAbsolutePath.toString)
			Files.createFile(p)
		}
			
		Class::forName("org.sqlite.JDBC")
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + p.toAbsolutePath.toString)
		if (Files.size(p) == 0) {
			LOGGER.info("Init db file at: {}", p.toAbsolutePath.toString)
			this.init
		}
		
		this.conn.autoCommit = false
		this.ps = this.conn.prepareStatement("INSERT INTO concrete VALUES (?, ?, ?)")
	}
	
	def init() {
		val stmt = this.conn.createStatement
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS concrete (id STRING PRIMARY KEY, type STRING, concrete_bytes)")
		stmt.close
	}
	
	def drop() {
		val stmt = this.conn.createStatement
		stmt.executeUpdate("DROP TABLE IF EXISTS concrete")
		stmt.close
	}
	
	override close() throws SQLException {
		this.ps.close
		this.conn.commit
		this.conn.close
	}

	def get(String id) {
		val stmt = this.conn.prepareStatement("SELECT concrete_bytes FROM concrete WHERE ID = ?")
		stmt.setString(1, id)
		val rs = stmt.executeQuery
		
		val toRet = 
			if(rs.next) 
				Optional.of(cs.fromBytes(rs.getBytes("concrete_bytes"))) 
			else
				Optional.empty

		rs.close
		stmt.close
		return toRet
	}
	
	def add(Communication c) {
		this.ps.setString(1, c.getId)
		this.ps.setString(2, c.getType)
		this.ps.setBytes(3, cs.toBytes(c))
		
		this.ps.addBatch
	}
	
	def execute() {
		this.ps.executeBatch
	}
	
	def ingest(Path p) throws ConcreteException {
		val is = Files.newInputStream(p)
		val bis = new BufferedInputStream(is)
		val iter = cs.fromTarGz(bis)
		iter.forEach[ comm |
			this.add(comm)
		]
		
		bis.close
		is.close
	}
	
	def static void main (String[] args) {
		Thread.defaultUncaughtExceptionHandler = new LoggedUncaughtExceptionHandler
		val db = Paths.get(args.get(0))
		val input = Paths.get(args.get(1))
		val impl = new SQLiteImpl(db)
		impl.ingest(input)
		impl.execute
		impl.close
	}
}
