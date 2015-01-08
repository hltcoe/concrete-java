package concrete.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TSimpleJSONProtocol;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.serialization.ThreadSafeCompactCommunicationSerializer;

/**
 * Computes the space on disk taken up by each of the types in a Concrete thrift
 * object.
 * 
 * @author travis
 */
public class SpaceAudit {
  
  private final ObjectMapper om = new ObjectMapper();

	/** Stores info about a particular item being accumulated */
	static class Info {
		private String path;
		private int instances = 0;
		private int bytes = 0;
		public Info(String path) {
			this.path = path;
		}
		public void update(int gzipBytes) {
			this.instances++;
			this.bytes += gzipBytes;
		}
		public String toString() {
			return path + " has " + instances + " instances and " + bytes + " bytes";
		}
	}
	
	// Sorts by bytes, decreasing
	private Comparator<Info> sortOrder = new Comparator<Info>() {
		@Override
		public int compare(Info o1, Info o2) {
			return o2.bytes - o1.bytes;
		}
	};
	private Map<String, Info> aggregates;
	
	public SpaceAudit() {
		aggregates = new HashMap<>();
	}

	/** Show the top k items */
	public void show(int k) {
		int totalBytes = 0;
		List<Info> items = new ArrayList<>();
		for (Info i : aggregates.values()) {
			totalBytes += i.bytes;
			items.add(i);
		}
		Collections.sort(items, sortOrder);
		int n = Math.min(k, items.size());
		System.out.println("total bytes: " + totalBytes);
		System.out.println("bytes		instances	%		cumulative%		path");
		int running = 0;
		for (int i = 0; i < n; i++) {
			Info inf = items.get(i);
			running += inf.bytes;
			System.out.printf("% 7d\t\t% 7d\t\t%5.2f\t\t%5.2f\t\t%s\n",
					inf.bytes,
					inf.instances,
					(100d * inf.bytes) / totalBytes,
					(100d * running) / totalBytes,
					inf.path);
		}
	}

	/** Recursively estimate size of the items under this node 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException */
	public void count(TBase<?, ?> node) throws TException, JsonParseException, JsonMappingException, IOException {
		TSerializer serializer = new TSerializer(new TSimpleJSONProtocol.Factory());
		String json = serializer.toString(node);
		Map<String, Object> j = om.readValue(json, Map.class);
		// JSONObject j = new JSONObject(json);
		count("", j);
	}

	/** Recursively estimate size of the items under this node */
	public void count(String path, Object maybe) {
		if (maybe == null)
			return;
		if (maybe instanceof String) {
			getInfo(path).update(((String) maybe).length());
		} else if (maybe instanceof Number) {
			getInfo(path).update(4);
		} else if (maybe instanceof List) {
			List<Object> a = ((ArrayList<Object>) maybe);
			for (int i = 0; i < a.size(); i++)
				count(path, a.get(i));
		} else if (maybe instanceof Map) {
			Map<String, Object> jo = (LinkedHashMap<String, Object>) maybe;
			Set<String> keys = jo.keySet();
			for (String k : keys)
				count(path + "/" + k, jo.get(k));
		}
	}

	private Info getInfo(String key) {
		Info i = aggregates.get(key);
		if (i == null) {
			i = new Info(key);
			aggregates.put(key, i);
		}
		return i;
	}
	
	/*	TODO support gzip estimates of true size
	private int size(JSONObject j) {
		try {
			String s = j.toString();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(new GZIPOutputStream(os));
			osw.write(s);
			osw.flush();
			return os.size();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	*/

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("provide some Concrete files");
			return;
		}
		ThreadSafeCompactCommunicationSerializer ser = new ThreadSafeCompactCommunicationSerializer();
		SpaceAudit all = new SpaceAudit();
		for (String concreteFile : args) {
			System.out.println(concreteFile);
			Communication c = ser.fromPathString(concreteFile);
			SpaceAudit audit = new SpaceAudit();
			audit.count(c);
			all.count(c);
			audit.show(5);
			System.out.println();
		}
		System.out.println("all " + args.length + " files");
		all.show(15);
	}
}
