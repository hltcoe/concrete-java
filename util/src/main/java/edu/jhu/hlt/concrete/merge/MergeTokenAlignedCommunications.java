package edu.jhu.hlt.concrete.merge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.DependencyParse;
import edu.jhu.hlt.concrete.Parse;
import edu.jhu.hlt.concrete.Section;
import edu.jhu.hlt.concrete.Sentence;
import edu.jhu.hlt.concrete.SpanLink;
import edu.jhu.hlt.concrete.TokenTagging;
import edu.jhu.hlt.concrete.Tokenization;

/**
 * Given two communications with the same number of sections, sentences, and tokens,
 * take the union of their annotations. In cases where fields cannot be added together
 * (e.g. document id), the first Communications's values take precedent.
 *
 * @author travis
 */
public class MergeTokenAlignedCommunications {
  public static boolean VERBOSE = true;
  
  private Communication left, right;
  private Communication union;
  
  /**
   * The result starts out as a deep copy of left (so it inherits things like
   * left's id and metadata on things which can't be unioned like sections and
   * sentences), and then annotations from right are added.
   */
  public MergeTokenAlignedCommunications(Communication left, Communication right) {
    this.left = left;
    this.right = right;

    this.union = new Communication(left);
    int nsection = left.getSectionListSize();
    if (nsection != right.getSectionListSize())
      throw new IllegalArgumentException("left has " + nsection + " sections but right has " + right.getSectionListSize());
    
    for (int i = 0; i < nsection; i++) {
      Section sl = left.getSectionList().get(i);
      Section sr = right.getSectionList().get(i);
      MergedSection m;
      try {
        m = new MergedSection(sl, sr);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("in section " + i, e);
      }
      union.getSectionList().set(i, m.getUnion());
    }
  }
  
  public Communication getUnion() {
    return union;
  }
  
  public static class MergedSection {
    private Section left, right;
    private Section union;
    
    public MergedSection(Section left, Section right) {
      this.left = left;
      this.right = right;
      
      this.union = new Section(left);
      
      int nsentence = left.getSentenceListSize();
      if (nsentence != right.getSentenceListSize())
        throw new IllegalArgumentException("left section has " + nsentence + " sentences but right has " + right.getSentenceListSize());
      
      for (int i = 0; i < nsentence; i++) {
        Sentence sl = left.getSentenceList().get(i);
        Sentence sr = right.getSentenceList().get(i);
        MergedSentence m;
        try {
          m = new MergedSentence(sl, sr);
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("in sentence " + i, e);
        }
        union.getSentenceList().set(i, m.getUnion());
      }
    }
    
    public Section getUnion() {
      return union;
    }
  }

  public static class MergedSentence {
    private Sentence left, right;
    private Sentence union;

    public MergedSentence(Sentence left, Sentence right) {
      this.left = left;
      this.right = right;
      this.union = new Sentence(left);
      
      int n = left.getTokenization().getTokenList().getTokenListSize();
      int nn = right.getTokenization().getTokenList().getTokenListSize();
      if (n != nn)
        throw new IllegalArgumentException("left sentence has " + n + " tokens but right has " + nn);
      
      Tokenization tu = union.getTokenization();
      Tokenization tr = right.getTokenization();

      if (tr.isSetDependencyParseList())
        for (DependencyParse x : tr.getDependencyParseList())
          tu.addToDependencyParseList(x);

      if (tr.isSetParseList())
        for (Parse x : tr.getParseList())
          tu.addToParseList(x);

      if (tr.isSetSpanLinkList())
        for (SpanLink x : tr.getSpanLinkList())
          tu.addToSpanLinkList(x);

      if (tr.isSetTokenTaggingList())
        for (TokenTagging x : tr.getTokenTaggingList())
          tu.addToTokenTaggingList(x);
    }
    
    public Sentence getUnion() {
      return union;
    }
  }
  
  public static void writeCommunication(Communication c, String filename) {
    if (VERBOSE)
      System.out.println("writing communication id=" + c.getId() + " to " + filename);
    File f = new File(filename);
    try (BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(f))) {
      c.write(new TCompactProtocol(new TIOStreamTransport(b)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Communication readCommunication(String filename) {
    if (VERBOSE)
      System.out.println("reading a communication from " + filename);
    File f = new File(filename);
    if (!f.isFile())
      throw new RuntimeException("not a file: " + f.getPath());
    Communication c = new Communication();
    try (BufferedInputStream b = new BufferedInputStream(new FileInputStream(f))) {
      c.read(new TCompactProtocol(new TIOStreamTransport(b)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return c;
  }
  
  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("please provide:");
      System.out.println("1) an input Communication file (left)");
      System.out.println("2) an input Communication file (right)");
      System.out.println("3) an output Communication file");
      System.exit(1);
    }
    Communication left = readCommunication(args[0]);
    Communication right = readCommunication(args[1]);
    try {
      MergeTokenAlignedCommunications m = new MergeTokenAlignedCommunications(left, right);
      writeCommunication(m.getUnion(), args[2]);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("left=" + args[0] + " right=" + args[1], e);
    }
  }
}
