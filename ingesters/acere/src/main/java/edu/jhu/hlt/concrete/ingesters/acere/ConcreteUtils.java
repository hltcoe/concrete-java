package edu.jhu.hlt.concrete.ingesters.acere;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.jhu.hlt.concrete.AnnotationMetadata;
import edu.jhu.hlt.concrete.Communication;
import edu.jhu.prim.list.ByteArrayList;

public class ConcreteUtils {

  private static final Logger log = LoggerFactory.getLogger(ConcreteUtils.class);

  public static final long annotationTime = System.currentTimeMillis();

  public static AnnotationMetadata metadata(String toolName) {
    return new AnnotationMetadata().setTool(toolName).setTimestamp(annotationTime);
  }

  /** Writes a communication as an entry in a zip file. */
  public static void writeCommAsZipEntry(Communication comm, ZipOutputStream zip, String name)
      throws IOException, TException {
    zip.putNextEntry(new ZipEntry(name));
    TSerializer ser = new TSerializer(new TCompactProtocol.Factory());
    byte[] bytez = ser.serialize(comm);
    zip.write(bytez);
    zip.closeEntry();
  }

  /** Reads a communication from an entry in a zip file. */
  public static Communication readNextEntryAsComm(ZipInputStream zip) throws IOException, TException {
    ZipEntry entry = zip.getNextEntry();
    log.info("Reading zip entry: " + entry);
    ByteArrayList buf = new ByteArrayList();
    while (zip.available() == 1) {
      byte[] tmp = new byte[1024];
      int length = zip.read(tmp);
      if (length != tmp.length) {
        tmp = Arrays.copyOf(tmp, length);
      }
      buf.add(tmp);
    }
    log.info("Deserializing to Communication");
    TDeserializer deser = new TDeserializer(new TCompactProtocol.Factory());
    Communication comm = new Communication();
    deser.deserialize(comm, buf.toNativeArray());
    return comm;
  }

  public static FileSystem getNewZipFileSystem(Path zipFile) throws IOException {
    if (Files.exists(zipFile)) {
      Files.delete(zipFile);
    }
    URI uri = URI.create("jar:file:" + zipFile.toUri().getPath());
    Map<String, String> env = new HashMap<>();
    env.put("create", "true");
    return FileSystems.newFileSystem(uri, env);
  }

}
