/*
 * Copyright 2012-2015 Johns Hopkins University HLTCOE. All rights reserved.
 * See LICENSE in the project root directory.
 */

package edu.jhu.hlt.ingesters.simple;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.TextSpan;
import edu.jhu.hlt.concrete.communications.CommunicationFactory;
import edu.jhu.hlt.concrete.ingesters.base.FileIngester;
import edu.jhu.hlt.concrete.ingesters.base.IngestException;
import edu.jhu.hlt.concrete.ingesters.base.util.ExistingNonDirectoryFile;
import edu.jhu.hlt.concrete.ingesters.base.util.NotFileException;
import edu.jhu.hlt.concrete.util.ConcreteException;

/**
 * Implementation of {@link FileIngester} whose {@link FileIngester#fromCharacterBasedFile(Path, Charset)}
 * implementation converts the entire contents of a
 * character-based file to a {@link Communication} object.
 * <ul>
 *  <li>
 *   The file name is used as the ID of the Communication.
 *  </li>
 *  <li>
 *   The Communication will contain one {@link Section} with one {@link TextSpan}.
 *  </li>
 * </ul>
 */
public class CompleteFileIngester implements FileIngester {

  private final Charset cs;

  /**
   * Expect UTF-8 encoded documents.
   */
  public CompleteFileIngester() {
    this.cs = StandardCharsets.UTF_8;
  }

  /* (non-Javadoc)
   * @see edu.jhu.hlt.concrete.ingesters.base.FileIngester#fromCharacterBasedFile(java.nio.file.Path, java.nio.charset.Charset)
   */
  @Override
  public Communication fromCharacterBasedFile(Path path, Charset charset) throws IngestException {
    try {
      ExistingNonDirectoryFile f = new ExistingNonDirectoryFile(path);
      try(InputStream is = Files.newInputStream(f.getPath());
          BufferedInputStream bis = new BufferedInputStream(is);
          Scanner sc = new Scanner(bis, this.cs.toString());) {
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine())
          sb.append(sc.nextLine());
        String content = sb.toString();
        return CommunicationFactory.create(f.getName(), content, "Other");
      } catch (IOException e) {
        throw new IngestException("Caught exception reading in document.", e);
      } catch (ConcreteException e) {
        throw new IngestException(e);
      }
    } catch (NoSuchFileException | NotFileException e) {
      throw new IngestException("Path did not exist or was a directory.", e);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
