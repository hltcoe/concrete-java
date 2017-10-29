package edu.jhu.hlt.concrete.ingesters.base;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

public class IngesterOpts {
  @ParametersDelegate
  public IngesterParameterDelegate delegate = new IngesterParameterDelegate();

  @Parameter(description = "path/to/folder1 /path/to/file2 ...")
  public List<String> paths = new ArrayList<>();

  /**
   * Finds all (regular) files under any file or directory added to the path.
   */
  public List<Path> findFilesInPaths() {
    List<Path> output = new ArrayList<>();
    try {
      for (String pstr : paths) {
        Path p = Paths.get(pstr);
        if (Files.isRegularFile(p)) {
          output.add(p);
        } else {
          Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
              if (Files.isRegularFile(path))
                output.add(path);
              return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
              return FileVisitResult.CONTINUE;
            }
          });
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return output;
  }
}
