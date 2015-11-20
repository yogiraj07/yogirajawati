import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class HW4 {
    private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
    //sortedWordFrequency : Stores word : Frequency , sorted in descending order of the frequency
    private static LinkedHashMap<String, Long> sortedWordFrequency =new LinkedHashMap<String, Long>(); 
    static int count =0; // use to count the queries inputed through the console
    private static int totalFrequency; //stores total frequency of the unique words in the collection
    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<File>();

    public static void main(String[] args) throws IOException {
	System.out
		.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");

	String indexLocation = null;  //stores index location
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String s = br.readLine();

	HW4 indexer = null;
	try {
	    indexLocation = s;
	    indexer = new HW4(s);
	} catch (Exception ex) {
	    System.out.println("Cannot create index..." + ex.getMessage());
	    System.exit(-1);
	}

	// ===================================================
	// read input from user until he enters q for quit
	// ===================================================
	while (!s.equalsIgnoreCase("q")) {
	    try {
		System.out
			.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
		System.out
			.println("[Acceptable file types: .xml, .html, .html, .txt]");
		s = br.readLine();
		if (s.equalsIgnoreCase("q")) {
		    break;
		}

		// try to add files into the index
		indexer.indexFileOrDirectory(s);
	    } catch (Exception e) {
		System.out.println("Error indexing " + s + " : "
			+ e.getMessage());
	    }
	}

	// ===================================================
	// after adding, we always have to call the
	// closeIndex, otherwise the index is not created
	// ===================================================
	indexer.closeIndex();

	// =========================================================
	// Now search
	// =========================================================
	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
		indexLocation))); //reader points to index location
	
	//logic to get unique words and their frequency
	
	Fields f = MultiFields.getFields(reader);
	Terms term = f.terms("contents");
	TermsEnum j = term.iterator(null);
	HashMap<String, Long> wordFrequency = new HashMap<String, Long>(); //stores unique words and their frequency
	wordFrequency = populateWordFrequency(reader, j, count, wordFrequency);
	sortedWordFrequency=sortWordFrequency(wordFrequency);
    writeIndexToFile(sortedWordFrequency); //writes index to file
    ZipfGraph.drawZipfCurve(sortedWordFrequency,totalFrequency); // draws Zipf curve on sortedWordFrequency
	
	IndexSearcher searcher = new IndexSearcher(reader);
	s = "";
	while (!s.equalsIgnoreCase("q")) {
	    try {
	    TopScoreDocCollector collector = TopScoreDocCollector.create(10000, true); //points to collection of top 100 ranked docs
		System.out.println("Enter the search query (q=quit):");
		s = br.readLine(); //contains the query
		if (s.equalsIgnoreCase("q")) {
		    break;
		}

		Query q = new QueryParser(Version.LUCENE_47, "contents",
				sAnalyzer).parse(s); //query is parsed
		searcher.search(q, collector); //parsed query is searched in collector
		ScoreDoc[] hits = collector.topDocs().scoreDocs; // contains doc and score for all terms in the query

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
//		for (int i = 0; i < hits.length; ++i) {
//		    int docId = hits[i].doc;
//		    Document d = searcher.doc(docId);
//		    System.out.println((i + 1) + ". " + d.get("path")
//			    + " score=" + hits[i].score);
//		}
		writeSearchResultToFile(hits,searcher,s,++count); //writes search result to file
		// 5. term stats --> watch out for which "version" of the term
		// must be checked here instead!
		String queryData[] =s.split(" ");
		int termFreq = 0;
		int docCount=0;
		for (int i = 0; i < queryData.length; i++) {  //for each term in the query
		 Term termInstance = new Term("contents", queryData[i]); // s is the entered query
		 termFreq += reader.totalTermFreq(termInstance); // term frequency
		 docCount += reader.docFreq(termInstance); // document frequency
		 //termInstance=null;
		}
		System.out.println(s + " Term Frequency " + termFreq
				+ " - Document Frequency " + docCount);
		termFreq=0; docCount=0;
	    } catch (Exception e) {
	    	e.printStackTrace();
		break;
	    }

	}

    }

	private static void writeSearchResultToFile(ScoreDoc[] hits,
			IndexSearcher searcher, String s, int count) {
		try {
			File f = new File("query"+count+".txt");
			if (!f.exists()) // if file is not created then create a new file
			{
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Output for Query : "+s+"\n");
			for (int i = 0; i < hits.length && i<100; ++i) { // write top 100 results
				int docId = hits[i].doc;
				Document d;
				String context;
				d = searcher.doc(docId);
				context = (i + 1) + ". " + d.get("path") + " score="
						+ hits[i].score+"\n";
				bw.write(context);

			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	private static void writeIndexToFile( //writes index to file
			LinkedHashMap<String, Long> sortedWordFrequency) {
		try {
			File f = new File("PopulatedIndex.txt");
 			if (!f.exists()) // if file is not created then create a new file
 			{
 				f.createNewFile();
 			}
 			FileWriter fw = new FileWriter(f.getAbsoluteFile());
 			BufferedWriter bw = new BufferedWriter(fw);
 			String context;
 		    Iterator<String> it = sortedWordFrequency.keySet().iterator();
 			String word;
 			long freq;
 			while(it.hasNext())
 			{  
 				word=it.next();
 				freq=sortedWordFrequency.get(word);
 				context=word+" : "+freq+"\n";
 				bw.write(context);
 				context="";
 			}
 			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param reader
	 * @param j
	 * @param count
	 * @param wordFrequency
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, Long> populateWordFrequency(IndexReader reader, TermsEnum j,
			int count, HashMap<String, Long> wordFrequency) throws IOException {
		BytesRef byteReference;
		while((byteReference= j.next()) != null)
		{
			String singleTerm=new String(byteReference.bytes, byteReference.offset, byteReference.length);
			Term termObject = new Term("contents",singleTerm); // create single term object
			Long termFrequency = reader.totalTermFreq(termObject);
			totalFrequency += termFrequency;
			wordFrequency.put(singleTerm, termFrequency);
			count++;
		}
		// logic to remove html and pre tags in the given data collection set
		long compare=wordFrequency.get("pre")-wordFrequency.get("html");
		wordFrequency.remove("pre");
		wordFrequency.remove("html");
		if(compare==0)
		{
		    return wordFrequency;
		}
		else if(compare>0)
		{
			wordFrequency.put("pre", compare);
		}
		else
		{
			wordFrequency.put("html", Math.abs(compare));
		}
		return wordFrequency;
	}

	private static 	LinkedHashMap<String, Long>  sortWordFrequency(
			HashMap<String, Long> map) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, Long> sortedMap=new LinkedHashMap<String, Long>();
		try {
			Set<Entry<String, Long>> set = map.entrySet();
			List<Entry<String, Long>> list = new ArrayList<Entry<String, Long>>(
					set);
			
			Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {

				@Override
				public int compare(Entry<String, Long> o1,
						Entry<String, Long> o2) {
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});
			for (Entry<String, Long> entry : list) { //populate sorted map
					sortedMap.put(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sortedMap;
	}

	/**
     * Constructor
     * 
     * @param indexDir
     *            the name of the folder in which the index should be created
     * @throws java.io.IOException
     *             when exception creating index.
     */
    HW4(String indexDir) throws IOException {

	FSDirectory dir = FSDirectory.open(new File(indexDir));

	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
			sAnalyzer);

	writer = new IndexWriter(dir, config);
    }

    /**
     * Indexes a file or directory
     * 
     * @param fileName
     *            the name of a text file or a folder we wish to add to the
     *            index
     * @throws java.io.IOException
     *             when exception
     */
    public void indexFileOrDirectory(String fileName) throws IOException {
	// ===================================================
	// gets the list of files in a folder (if user has submitted
	// the name of a folder) or gets a single file name (is user
	// has submitted only the file name)
	// ===================================================
	addFiles(new File(fileName));

	int originalNumDocs = writer.numDocs();
	for (File f : queue) {
	    FileReader fr = null;
	    try {
		Document doc = new Document();

		// ===================================================
		// add contents of file
		// ===================================================
		fr = new FileReader(f);
		doc.add(new TextField("contents", fr));
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		doc.add(new StringField("filename", f.getName(),
			Field.Store.YES));

		writer.addDocument(doc);
		System.out.println("Added: " + f);
	    } catch (Exception e) {
		System.out.println("Could not add: " + f);
	    } finally {
		fr.close();
	    }
	}

	int newNumDocs = writer.numDocs();
	System.out.println("");
	System.out.println("************************");
	System.out
		.println((newNumDocs - originalNumDocs) + " documents added.");
	System.out.println("************************");

	queue.clear();
    }

    private void addFiles(File file) {

	if (!file.exists()) {
	    System.out.println(file + " does not exist.");
	}
	if (file.isDirectory()) {
	    for (File f : file.listFiles()) {
		addFiles(f);
	    }
	} else {
	    String filename = file.getName().toLowerCase();
	    // ===================================================
	    // Only index text files
	    // ===================================================
	    if (filename.endsWith(".htm") || filename.endsWith(".html")
		    || filename.endsWith(".xml") || filename.endsWith(".txt")) {
		queue.add(file);
	    } else {
		System.out.println("Skipped " + filename);
	    }
	}
    }

    /**
     * Close the index.
     * 
     * @throws java.io.IOException
     *             when exception closing
     */
    public void closeIndex() throws IOException {
	writer.close();
    }
}