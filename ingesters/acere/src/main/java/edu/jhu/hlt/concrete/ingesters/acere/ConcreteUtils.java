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

}
