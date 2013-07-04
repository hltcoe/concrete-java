package edu.jhu.hlt.concrete.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import edu.jhu.hlt.concrete.Concrete.Communication;
import edu.jhu.hlt.concrete.Concrete.Communication.Builder;
import edu.jhu.hlt.concrete.Concrete.EmailAddress;
import edu.jhu.hlt.concrete.Concrete.EmailCommunicationInfo;
import edu.jhu.hlt.concrete.Concrete.KeyValues;
import edu.jhu.hlt.concrete.Concrete.Section;
import edu.jhu.hlt.concrete.Concrete.Section.Kind;
import edu.jhu.hlt.concrete.Concrete.SectionSegmentation;
import edu.jhu.hlt.concrete.Concrete.TextSpan;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;



/**
 * The purpose of this utility is to provide a json wrapper class
 * and utility functions
 * for the protocol buffer Communication class.
 * 
 * Set test(String[] args) for an example of deserializing and serializing
 * Concrete Communication objects to and form the JsonCommunication wrapper class.
 * 
 */
public class JsonUtil {

	public static class JsonCommunication {
		/*
		 * A JSON wrapper for concrete communication.
		 * 
		 */

		/*
		 * Subclasses
		 */
		private class Body{
			//Body in the intuitive sense as a sequence
			//of paragraphs
			private List<Paragraph> paragraphs;
			
			public Body(List<Paragraph> paras){
				this.paragraphs = paras;
			}
			public void setAllParagraphs(List<Paragraph> paras){
				this.paragraphs = paras;
			}
			public void addAllParagraphs(List<Paragraph> paras){
				for(Paragraph para: paras){
					this.paragraphs.add(para);
				}
			}
			public void addParagraph(Paragraph para){
				this.paragraphs.add(para);
			}
		}
		
		private class Paragraph{
			//Paragraph in the intuitive sense as a sequence
			//of sentences
			//private List<Sentence> sentences;
			//Paragraph is the rough equivalent of Section in Concrete
			private String paraRawText;
			private String kind;
			
			public Paragraph(String t, String k){
				this.paraRawText = t;
				this.kind = k;
			}
			
			public Paragraph(String substring, Kind kind2) {
				this.paraRawText = substring;
				this.kind = kind2.name();
			}

			public void setKind(String kind){
				this.kind = kind;
			}
			
			public String getKind(){return kind;}
			
			/*
			public void setAllSentences(List<Sentence> sents){
				this.sentences = sents;
			}
			public void addAllSentences(List<Sentence> sents){
				for(Sentence sent: sents){
					this.sentences.add(sent);
				}
			}
			public void addSentence(Sentence sent){
				this.sentences.add(sent);
			}
			*/
		}
		
		/*
		private class Sentence{
			//Sentence as the atomic unit of the JsonCommunication
			//Not currently supported
			//Class
			private String text = "";
			public void setText(String text){
				this.text = text;
			}
		}*/		
		
		private class JsonKeyValues{
			public JsonKeyValues(String key, List<String> valuesList) {
				this.key = key;
				this.values = valuesList;
			}
			private String key;
			private List<String> values;
		}
		/*
		 * Generic meta-information for communication
		 */
		private double startTime;
		private String author;
		private String title;
		
		/*
		 * Email Headers
		 */
		private String messageId;
		private String senderEmail;
		private List<String> recipientsTo;
		private List<String> recipientsCc;
		private List<String> recipientsBcc;
		
		/*
		 * Content
		 */
		private String rawText;//The raw byte->String of the email
		private String bodyText;//The byte->String->Mime message->getText(Mime message)
		private List<Body> bodyChain = new ArrayList<Body>();//A list of the email messages contained in emailBodyText	
		private List<JsonKeyValues> metadata = new ArrayList<JsonKeyValues>();

		
		public List<String> getAcceptedKeys(){
			List<String> acceptedKeys = new ArrayList <String>();
			acceptedKeys.add("rawText");
			acceptedKeys.add("bodyText");
			acceptedKeys.add("bodyChain");
			return acceptedKeys;
		}
		
		
		public List<JsonKeyValues> getMetadata() {
			return metadata;
		}

		public void addJKVMetadata(JsonKeyValues jkv){
			this.metadata.add(jkv);
		}

		public void addMetadata(KeyValues kvs) {
			this.metadata.add(new JsonKeyValues(kvs.getKey(),kvs.getValuesList()));
		}


		/*
		 * Manipulation methods
		 */
		public JsonCommunication(){
			this.rawText = "";
		}
				
				
		public JsonCommunication(String startTime, String rawText) throws ParseException{
			this.rawText = rawText;
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime);
			this.startTime = date.getTime();
		}
		
		/**
		 * Convert a SectionSegmentation to a Body object
		 * @param seg
		 * @param rawText
		 * @return the Body object that represents the rawText
		 */
		private Body segmentToBody(SectionSegmentation seg,String rawText) {
			List<Section> sections = seg.getSectionList();
			List<Paragraph> paras = new ArrayList<Paragraph>();			
			for(Section sec: sections){
				TextSpan ts = sec.getTextSpan();
				paras.add(new Paragraph(rawText.substring(ts.getStart(),ts.getEnd()),sec.getKind()));
			}
			return new Body(paras);
		}
		
		/*
		private Body listToBody(List<String> list){
			List<Paragraph> paras = new ArrayList<Paragraph>();
			for (String s: list){
				paras.add(new Paragraph(s));
			}
			return new Body(paras);
		}*/
		
	
		/*
		 * Accessor methods
		 * 
		 */
		
		public double getStartTime() {
			return startTime;
		}

		public void setStartTime(double startTime) {
			this.startTime = startTime;
		}
		
		private void setAuthor(EmailAddress senderAddress) {
			this.author = senderAddress.getAddress();			
		}

		private void setTitle(String messageId2) {
			this.title = messageId2;			
		}
		
		public String getMessageId() {
			return messageId;
		}
		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}
		public String getSenderEmail() {
			return senderEmail;
		}
		public void setSenderEmail(String senderEmail) {
			this.senderEmail = senderEmail;
		}
		public List<String> getRecipientsTo() {
			return recipientsTo;
		}
		public void setRecipientsTo(List<String> recipientsTo) {
			this.recipientsTo = recipientsTo;
		}
		public List<String> getRecipientsCc() {
			return recipientsCc;
		}
		public void setRecipientsCc(List<String> recipientsCc) {
			this.recipientsCc = recipientsCc;
		}
		public List<String> getRecipientsBcc() {
			return recipientsBcc;
		}
		public void setRecipientsBcc(List<String> recipientsBcc) {
			this.recipientsBcc = recipientsBcc;
		}
		public String getRawText() {
			return rawText;
		}
		public void setRawText(String rawText) {
			this.rawText = rawText;
		}
		public String getBodyText() {
			return bodyText;
		}
		public void setBodyText(String emailBodyText) {
			this.bodyText = emailBodyText;
		}
		public List<Body> getBodyChain() {
			return bodyChain;
		}
		public void setBodyChain(List<Body> emailChain) {
			this.bodyChain = emailChain;
		}
		public void addBodyToChain(SectionSegmentation seg, String rawText){
			Body body = segmentToBody(seg,rawText);
			this.bodyChain.add(body);
		}
		public JsonObject toJsonObject(){
			Gson gson = new Gson();
			return gson.toJsonTree(this).getAsJsonObject();
		}
		public JsonElement toJsonElement(){
			Gson gson = new Gson();
			return gson.toJsonTree(this).getAsJsonObject();
		}
	}
	/*
	 * Handler Methods
	 */
	
	/**
	 * Given a Communication object return the JsonCommunication jsonObject
	 * @input commIn Given a Communiation object, convert 
	 * 					specific data members and return the json object
	 * 
	 * @return the json object
	 */
	public static JsonObject toJson(Communication commIn) {
		JsonCommunication jcomm = toJsonCommunication(commIn);
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject jo = (JsonObject)parser.parse(gson.toJson(jcomm));
		return jo;
	}
	
	/**
	 * Given a json string representation of a JsonCommunication object
	 * return the JsonObject equivalent
	 * 
	 *@param json a string representation of a JsonCommunication Object
	 *
	 *@return a JsonObject of the JsonCommunication
	 */
	public static JsonObject toJsonObjectFromJsonString(String json) {
		Gson gson = new Gson();
		JsonCommunication jc = gson.fromJson(json,JsonCommunication.class);
		return jc.toJsonObject();
	}
	
	/**
	 * Given a communication, return the json string representation
	 * 
	 * @param commIn the Communication input
	 * 
	 * @return the json String representation of the JsonCommunication
	 */
	public static String toJsonString(Communication commIn){
		Gson gson = new Gson();
		JsonCommunication jcomm = toJsonCommunication(commIn);
		return gson.toJson(jcomm);
	}
	
	/**
	 * If the Json string is already in perfect JsonCommunication form,
	 * return the JsonCommunication. If it is just a json string, not yet
	 * in JsonCommunication form, use toJsonCommunicationFromUnknown.
	 * 
	 *@param json a string representation of a JsonCommunication Object
	 *
	 *@return : a JsonCommunication given the json string
	 */
	public static JsonCommunication toJsonCommunicationFromWellFormed(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json,JsonCommunication.class);		
	}
	
	/**
	 * Given a Communication object, convert the object to a JsonCommunication 
	 * @param commIn the Concrete Communication object
	 * 
	 * @return the JsonCommunication object
	 */
	public static JsonCommunication toJsonCommunication(Communication commIn){
		JsonCommunication jcomm = new JsonCommunication();		
		
		//Get data members
		String rawText = commIn.getText();
		List<SectionSegmentation> segs = commIn.getSectionSegmentationList();
		SectionSegmentation headers = segs.get(0);//Header text is written as first secseg
		EmailCommunicationInfo info = commIn.getEmailInfo();
		
		List<KeyValues> metadata = commIn.getMetadataList();
		for(KeyValues kvs : metadata){
			jcomm.addMetadata(kvs);
		}
		
		//Set data members
		jcomm.setRawText(rawText);		
		jcomm.setAuthor(info.getSenderAddress());
		jcomm.setTitle(info.getMessageId());
		for(SectionSegmentation seg : segs.subList(1,segs.size())){
			jcomm.addBodyToChain(seg,rawText);
		}
		return jcomm;		
	}
	
	/**
	 * This needs to be changed such that we look at all the entries of the json
	 * object, find the ones that aren't included and add them as 
	 * 
	 * @param json
	 * @return
	 */
	public static JsonCommunication toJsonCommunicationFromUnknown(String json){
		Gson gson = new Gson();
		JsonParser jp = new JsonParser();
		JsonObject jo = (JsonObject)jp.parse(json);
		/*If we knew this was a well formed JsonCommunication:*/		
		JsonCommunication jcomm = gson.fromJson(jo, JsonCommunication.class);
		jcomm.getStartTime();
		return jcomm;
		
		
		//But we don't know that it's well formed, so we iterate through the fields
		/*
		Set<Entry<String, JsonElement>> es = jo.entrySet();
		String key; List<String> values;
		List<String> acceptedKeys = jcomm.getAcceptedKeys();
		
		for(Entry e: es){
			key = (String)e.getKey();
			values = (List<String>)e.getValue();
			if (acceptedKeys.contains(key)){
				if(key.contentEquals("rawText")){
					jcomm.setRawText(values.get(0));
				}
				else if(key.contentEquals("bodyText")){
					jcomm.setBodyText(values.get(0));
				}
				else if(key.contentEquals("bodyChain")){
					jcomm.setBodyChain(values);
				}
			
			}
			
		}
		*/


	}
	
	/**
	 * Get a list of communications as JsonObjects from
	 * a file of zipped communications.
	 * 
	 * @input filename the filename of the gzipped communications
	 * 
	 * @return the list of JsonObjects that represents the communications
	 */
	public List<JsonObject> getJsonCommunicationsFromGzip(String filename) throws FileNotFoundException, IOException{
		InputStream in = new GZIPInputStream(new FileInputStream(filename));
		List<JsonObject> jcomms = new ArrayList<JsonObject>();
		Communication commIn;
		
		while((commIn = Communication.parseDelimitedFrom(in)) != null){
			jcomms.add(toJson(commIn));
		}			
		in.close();
		return jcomms;
	}
	
	/**
	 * Get a list of strings that are the Json representation of the
	 * communication objects in the file filename.
	 * 
	 * @param filename the filename of the gzipped zerialized Concrete Communication objects
	 * 
	 * @return the String list of the json JsonCommunication objects
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<String> getJsonStringsFromGzip(String filename) throws FileNotFoundException, IOException{
		InputStream in = new GZIPInputStream(new FileInputStream(filename));
		List<String> jcomms = new ArrayList<String>();
		Communication commIn;		
		while((commIn = Communication.parseDelimitedFrom(in)) != null){
			jcomms.add(toJsonString(commIn));
		}			
		in.close();
		return jcomms;
	}
	
	/**
	 * Get a list of communications as JsonObjects from
	 * a file of zipped communications.
	 * 
	 * @param filename the filename of the gzipped communications
	 * 
	 * @return jcomms the list of JsonObjects that represents the communications
	 */
	public List<JsonObject> getJsonCommunications(String filename) throws FileNotFoundException, IOException{
		InputStream in = new GZIPInputStream(new FileInputStream(filename));
		List<JsonObject> jcomms = new ArrayList<JsonObject>();
		Communication commIn;
		
		while((commIn = Communication.parseDelimitedFrom(in)) != null){
			jcomms.add(toJson(commIn));
		}			
		in.close();
		return jcomms;
	}
	
	/**
	 * Get a list of strings that are the Json representation of the
	 * communication objects in the file filename.
	 * 
	 * @param filename the filename of the serialized Concrete communication objects
	 * @return a String List of the Json JsonCommunication strings
	 *  
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<String> getJsonStrings(String filename) throws FileNotFoundException, IOException{
		InputStream in = new FileInputStream(filename);
		List<String> jcomms = new ArrayList<String>();
		Communication commIn;		
		while((commIn = Communication.parseDelimitedFrom(in)) != null){
			jcomms.add(toJsonString(commIn));
		}			
		in.close();
		return jcomms;
	}
	
	/*
	 * 
	 */
	/**
	 * Convert the JsonCommunication object to a protobuf Communication object
	 * 
	 * @param jcomm the JsonCommunication object
	 * @return the Communication object
	 */
	public Communication toCommunication(JsonCommunication jcomm){
		Communication cb = new ProtoFactory().generateMockCommunication();
		
		Communication comm = cb.toBuilder()
				.setText(jcomm.getRawText())
				.setStartTime(jcomm.getStartTime())
				.build();
		return comm;		
	}
	
	/**
	 * Given a string representation of a JsonCommunication
	 * convert the communication to the jcomm and return the
	 * protobuf communication object.
	 * 
	 * @param json A string reprsentation of a JsonCommunication
	 * 
	 * @return the Communication object
	 */
	public Communication toCommunication(String json){
		JsonCommunication jcomm = toJsonCommunicationFromWellFormed(json);
		return toCommunication(jcomm);
	}
	
	/**
	 * Save the concrete object to a file
	 * @param jcomm
	 * @param outFilename
	 * @throws IOException
	 */
	public void saveConcrete(JsonCommunication jcomm, String outFilename) throws IOException{
		File outputFile = new File(outFilename);
		Communication comm = toCommunication(jcomm);
		if(!outputFile.exists()) {
		    outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
		}
		
		FileOutputStream output = new FileOutputStream(outputFile,false);
		comm.writeTo(output);
		output.close();
	}

	
	public static void test(String[] args) {
		try {
			//To get FROM a communication file TO jsonObject
			JsonUtil ju = new JsonUtil();		
			List<JsonObject> jcomms = ju.getJsonCommunicationsFromGzip(args[0]);

			//To get FROM a communication file TO json string
			List<String> jcommstrings = ju.getJsonStringsFromGzip(args[0]);
			
			//To get TO a communication object FROM a json object
			Communication comm;
			Gson gson = new Gson();			
			for(JsonObject jcomm : jcomms){
				JsonCommunication jc = gson.fromJson(jcomm, JsonCommunication.class);
				comm = ju.toCommunication(jc);
			}
			
			//To get TO a communication object FROM a json string
			for(String jcomm : jcommstrings){
				JsonCommunication jc = toJsonCommunicationFromWellFormed(jcomm);
				comm = ju.toCommunication(jc);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			//To get FROM a communication file TO jsonObject
			JsonUtil ju = new JsonUtil();		
			List<JsonObject> jcomms = ju.getJsonCommunicationsFromGzip(args[0]);

			//To get FROM a communication file TO json string
			List<String> jcommstrings = ju.getJsonStringsFromGzip(args[0]);
			
			//To get TO a communication object FROM a json object
			Communication comm;
			Gson gson = new Gson();			
			for(JsonObject jcomm : jcomms){
				JsonCommunication jc = gson.fromJson(jcomm, JsonCommunication.class);

				comm = ju.toCommunication(jc);
			}
			
			//To get TO a communication object FROM a json string
			for(String jcomm : jcommstrings){
				JsonCommunication jc = toJsonCommunicationFromWellFormed(jcomm);
				JsonCommunication jcs = toJsonCommunicationFromUnknown(jcomm);
				comm = ju.toCommunication(jc);
				System.out.println(comm.getStartTime());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
/*
 *Extended JSON support, not implemented, not functional yet
 
public static void merge(Entry entry, Builder builder){
//Detect if entry is array or not
Object value = entry.getValue();
Object key = (String)entry.getKey();
Gson gson = new Gson();
JsonElement je = gson.toJsonTree(value);
if(je.isJsonArray()){
	mergeArray(je,builder,gson);
}
else{
	je.getAsString();
}
//System.out.println("js:"+js+" je:"+je);
}

public static void mergeArray(JsonElement je, Builder builder, Gson gson){
JsonArray ja = je.getAsJsonArray();
for(JsonElement j: ja){
	if (j.isJsonArray()){mergeArray(j,builder,gson);}
	else{
		
	}
}		
}

public static void merge(JsonObject jobj, ExtensionRegistry ext, Communication.Builder builder){
String name = jobj.getAsString();
FieldDescriptor field;
Descriptor type = builder.getDescriptorForType();
field = type.findFieldByName(name);

ExtensionRegistry.ExtensionInfo extension = ext.findExtensionByName(name);
if (extension != null){
	if(extension.descriptor.getContainingType() != type){
		System.out.println("Extension "+name+"does not extend message type "+type.getFullName());
	}
	field = extension.descriptor;
}

handleValue(jobj,ext, builder,field,extension);

}

public static void handleValue(JsonObject jobj, ExtensionRegistry ext, Communication.Builder builder,
	FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension){
Object value;
if(field.getJavaType() == FieldDescriptor.JavaType.MESSAGE){
	value = handleObject(jobj,ext, builder, field, extension);
}
}

public static Object handleObject(JsonElement jel, ExtensionRegistry ext, Communication.Builder builder,
	FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension){
return null;
}
*/