package edu.jhu.hlt.concrete.ingesters.base;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
public class IngesterParameterDelegateTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(IngesterParameterDelegateTest.class);

  @Rule
  public TemporaryFolder tf = new TemporaryFolder();

  @Test
  public void missing() throws IOException {
    Path missing = Paths.get("/tmp/11111111111111").toAbsolutePath();
    assertFalse("file should not exist", Files.exists(missing));
    LOGGER.info("Testing missing path: {}", missing.toString());
    assertFalse("file should not exist", IngesterParameterDelegate.prepare(missing));
  }

  @Test
  public void file() throws IOException {
    Path filePath = tf.newFile("foo").toPath().toAbsolutePath();
    assertTrue("file should exist", Files.exists(filePath));
    LOGGER.debug("Abs path: {}", filePath.toAbsolutePath().toString());
    assertTrue("should find a file that exists", IngesterParameterDelegate.prepare(filePath));
  }

  @Test
  public void nonFile() throws IOException {
    Path filePath = tf.newFolder("qux").toPath();
    LOGGER.debug("Abs path: {}", filePath.toAbsolutePath().toString());
    assertTrue(Files.exists(filePath));
    assertTrue(Files.isDirectory(filePath));
    assertTrue(IngesterParameterDelegate.prepare(filePath));
  }
}
