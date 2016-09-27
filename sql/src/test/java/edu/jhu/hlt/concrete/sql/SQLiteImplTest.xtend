package edu.jhu.hlt.concrete.sql

import edu.jhu.hlt.concrete.random.RandomConcreteFactory
import java.nio.file.Files
import java.nio.file.Path
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class SQLiteImplTest {
	
	static final val Logger LOGGER = LoggerFactory.getLogger(typeof(SQLiteImplTest))
	
	SQLiteImpl conn
	Path p
		
	@Rule
	public TemporaryFolder tf = new TemporaryFolder
	
	@Before
	def void setUp() {
		p = tf.newFile.toPath
		this.conn = new SQLiteImpl(p)
	}
	
	@After
	def void tearDown() {
		this.conn.drop
		this.conn.close
		if (Files.exists(p))
			Files.delete(p)
	}
	
	@Test
	def insertAndQuery() {
		val fac = new RandomConcreteFactory()
		val comm = fac.communication
		val id = comm.id
		LOGGER.info("Adding comm: {}", id)
		this.conn.add(comm)
		this.conn.execute
		assertFalse("Junk ID should not be present.", this.conn.get("asdfqq").isPresent)
		val res = this.conn.get(id)
		assertTrue("Real ID should be present.", res.isPresent)
		assertEquals("Comms should be equal.", comm, res.get)
		
		val comm2 = fac.communication
		LOGGER.info("Adding comm: {}", comm2.id)
		this.conn.add(comm2)
		this.conn.execute
		
		assertFalse("Junk ID should not be present.", this.conn.get("asdfqq").isPresent)
		val resOrig = this.conn.get(id)
		assertTrue("Real ID should be present.", resOrig.isPresent)
		assertEquals("Comms should be equal.", comm, resOrig.get)
		
		val newRes = this.conn.get(comm2.id)
		assertTrue("Real ID #2 should be present.", newRes.isPresent)
		assertEquals("Comms 2 and retrieved should be equal.", comm2, newRes.get)
	}	
}