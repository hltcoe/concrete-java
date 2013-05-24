package edu.jhu.hlt.concrete.kb;

import java.util.*;
import java.io.*;
import java.util.regex.*;
 
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/*

# Required* / Desired Concrete Fields
#   Vertex
#    *uuid
#     kind* (PERSON, ORGANIZATION, GPE, UNKNOWN)
#     name* [StringAttribute: uuid, AttributeMetadata metadata, value]
#   Communication
#    *guid
#    *uuid
#     text
#     (language_id)
#     title
#     kind (WIKIPEDIA)
#    *knowledge_graph 
#   KnowledgeGraph (EMPTY)
#    *uuid
#    *vertex

# Mappings:
#	TACKB			Concrete
#	=====			========
#	wiki_text		Communication->text
#	entity			Vertex
#	entity->wiki_title	Communication->title ??
#	entity->type		Vertex->kind
#	entity->id		hashed or mapped to Vertex->uuid
#	entity->id		  Communication->guid (TAC09KB_E00392)
#	entity->name		Vertex->name (possibly normalized)
#	fact:fullname		Vertex->name
#	fact:???		RawKeyValuePair
#	fact:name		RawKeyValuePair->key
#	fact->{TEXT}		RawKeyValuePair->value
*/

public class TAC09KB2Concrete {

    static String concrete_protobuf_filename = "/home/hltcoe/jmayfield/code/Concrete/src/main/proto/concrete.proto";
    //    static String kb_dir = "/export/common/data/corpora/LDC/LDC2009E58/data";
    static String kb_dir = "/home/hltcoe/jmayfield/code/Slinky/samplekb";
    static Pattern pattern = Pattern.compile("\\s+");

    String current_id = null;
    String current_link = null;
    String fact_name = null;

    public class KBHandler extends DefaultHandler {

	StringBuilder text_buf = new StringBuilder(10000);
	String text_type = null;

	void collect_text(String type) {
	    if (text_type != null)
		System.err.println("ERROR: Nested calls to collect_text");
	    text_type = type;
	    text_buf.setLength(0);
	}

	String retrieve_text(String type) {
	    String result = "";
	    if (text_type == null)
		System.err.println("ERROR: Attempt to collect text for " + type + " that wasn't collected");
	    else if (!text_type.equals(type))
		System.err.println("ERROR: Text collected for " + text_type + " but retrieved for " + type);
	    else result = new String(text_buf);
	    text_buf.setLength(0);
	    text_type = null;
	    return(result);
	}


	String normalize(String string) {
	    return(pattern.matcher(string).replaceAll(" "));
	}

	void report(String key, String value) {
	    if (current_id == null) {
		System.err.println("Attempt to report with no current ID");
	    }
	    else {
		System.out.println(current_id + "\t" + key + "\t" + normalize(value));
	    }
	}

	public void startDocument() throws SAXException {
	}

	public void startElement(String namespaceURI,
				 String localName,
				 String qualifiedName, 
				 Attributes attributes)
	    throws SAXException {
	    if (qualifiedName.equals("entity")) {
		current_id = attributes.getValue("id");
		report("type", attributes.getValue("type"));
		report("wiki_title", attributes.getValue("wiki_title"));
		report("wiki_name", attributes.getValue("name"));
	    }
	    else if (qualifiedName.equals("fact")) {
		collect_text("fact");
		fact_name = attributes.getValue("name");
	    }
	    else if (qualifiedName.equals("link")) {
		current_link = attributes.getValue("entity_id");
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	}

	public void endElement(String uri, String localName, String qualifiedName) {
	    if (qualifiedName.equals("entity")) {
		current_id = null;
	    }
	    else if (qualifiedName.equals("fact")) {
		report("fact:" + fact_name, retrieve_text("fact"));
		if (current_link != null)
		    report("link:" + fact_name, current_link);
	    }
	    else if (qualifiedName.equals("link")) {
		current_link = null;
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	    else if (qualifiedName.equals("")) {
	    }
	}

	public void endDocument() throws SAXException {
	}

	public void characters(char[] chars, int start, int length) {
	    if (text_type != null)
		text_buf.append(chars, start, length);
	}
    }

    public TAC09KB2Concrete() {
    }

    void runmain(String[] argv) {
	if (argv.length == 1) {
	    try {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		InputStream xmlInput = new FileInputStream(argv[0]);
		SAXParser saxParser = factory.newSAXParser();
		KBHandler kbhandler = new KBHandler();
		saxParser.parse(xmlInput, kbhandler);
	    }
	    catch (Exception exception) {
		System.err.println(exception);
	    }
	}
	else {
	    System.out.println("Usage: java TAC09KB2Concrete <tac09-wp-xmlfile>");
	}
    }

    static public void main(String[] argv) {
	TAC09KB2Concrete transducer = new TAC09KB2Concrete();
	transducer.runmain(argv);
    }

}
