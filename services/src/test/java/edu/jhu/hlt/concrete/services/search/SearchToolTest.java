package edu.jhu.hlt.concrete.services.search;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.thrift.transport.TTransportException;
import org.junit.Test;

import edu.jhu.hlt.concrete.services.search.SearchTool;
import edu.jhu.hlt.concrete.services.search.SearchTool.Output;

public class SearchToolTest {

    @Test
    public void testGetQuestions() throws TTransportException {
        String q1 = "?:What is blue? ?:What is green?";
        Output output = SearchTool.getQuestions(q1);
        assertEquals("What is blue?", output.items.get(0));
        assertEquals("What is green?", output.items.get(1));
        assertEquals("", output.query);

        String q2 = "colors ?:\"What is red?\" ?:What is green?";
        output = SearchTool.getQuestions(q2);
        assertEquals("What is red?", output.items.get(0));
        assertEquals("What is green?", output.items.get(1));
        assertEquals("colors", output.query);

        String q3 = "colors ?:\"What is red\" ?:\"What is green?\" test";
        output = SearchTool.getQuestions(q3);
        assertEquals("What is red", output.items.get(0));
        assertEquals("What is green?", output.items.get(1));
        assertEquals("colors   test", output.query);
    }

    @Test
    public void testGetTerms() throws TTransportException {
        String q1 = "red blue green";
        List<String> terms = SearchTool.getTerms(q1);
        assertEquals("red", terms.get(0));
        assertEquals("blue", terms.get(1));
        assertEquals("green", terms.get(2));

        String q2 = "yellow \"red blue\" green";
        terms = SearchTool.getTerms(q2);
        assertEquals("yellow", terms.get(0));
        assertEquals("red blue", terms.get(1));
        assertEquals("green", terms.get(2));

        String q3 = "电脑坏了 他的大脑仍然很活跃";
        terms = SearchTool.getTerms(q3);
        assertEquals("电脑坏了", terms.get(0));
        assertEquals("他的大脑仍然很活跃", terms.get(1));

        String q4 = "\"" + "السلام عليكم" + "\"" +  "بالتوفيق";
        terms = SearchTool.getTerms(q4);
        assertEquals(2, terms.size());
        assertEquals("السلام عليكم", terms.get(0));
        assertEquals("بالتوفيق", terms.get(1));
    }
}
