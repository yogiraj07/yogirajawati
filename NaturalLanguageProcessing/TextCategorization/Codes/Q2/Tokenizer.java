import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author : Yogiraj Awati
 * Generates Vocabulary File
 */
public class Tokenizer {
    private static final String INPUT_PATH = "D:\\NLP_Spring2K17\\HW1\\shakespeare";
    private static final String OUTPUT_PATH = "vocabulary.txt";
    static HashMap<String, HashMap<String, Integer>> vocabulary = new HashMap<>();
    static int wordCount;
    private  static final int TERM_FREQUENCY_THRESHOLD = 5;
    private  static final int DOCUMENT_FREQUENCY_THRESHOLD = 2;
    public static final String SEPERATOR= ":";


    public static void main(String[] args) {
        loadDataFromFile(INPUT_PATH);
        cleanVocab();
        writeVocabularyToFile(OUTPUT_PATH);
        System.out.println(vocabulary.size());
        System.out.println("Total Words found: "+wordCount);
    }

    private static void writeVocabularyToFile(String outputPath) {
        try (FileWriter fileWriter = new FileWriter(outputPath);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            Set<String> terms = vocabulary.keySet();
            for (String term : terms) {
                wordCount++;
                StringBuffer records = new StringBuffer();
                for (String doc : vocabulary.get(term).keySet()) {
                    records.append("(" + doc + "," + vocabulary.get(term).get(doc) + ")");
                }
                writer.write(term + SEPERATOR+"{" + records.toString() + "}\n");
            }

        } catch (IOException e) {

        }
    }

    private static void loadDataFromFile(String inputPath) {
        File inputFolder = new File(inputPath);

        for (final File file : inputFolder.listFiles()) {
            if (file.isDirectory()) {
                System.out.println("Inside Subdirectory " + file);
                loadDataFromFile(file.getPath());
            } else {
                try (FileReader fileReader = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fileReader)) {
                    String fileName = file.getName();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processLine(fileName, line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void processLine(String fileName, String line) {
        String[] words = line.split("\\s+"); // removing consecutive whitespaces if any
        for (String word : words) {
            //Reference : http://stackoverflow.com/questions/23332146/remove-punctuation-preserve-letters-and-white-space-java-regex
            word = word.replaceAll("(\\w+)\\p{Punct}(\\s|$)", "$1$2").toLowerCase(); //removes punctuation
            if (word.isEmpty() || word.equals(" "))
                continue;
            if (vocabulary.get(word) != null) {
                HashMap<String, Integer> record = vocabulary.get(word);
                populateRecord(fileName, record);
                vocabulary.put(word, record);
            } else {
                HashMap<String, Integer> record = new HashMap<>();
                record.put(fileName, 1);
                vocabulary.put(word, record);
            }

        }
    }

    /**
     * @param fileName
     * @param record   - Populates record for the given filename
     */
    private static void populateRecord(String fileName, HashMap<String, Integer> record) {
        if (record.get(fileName) != null) {
            int oldValue = record.get(fileName);
            record.put(fileName, oldValue + 1);
        } else {
            record.put(fileName, 1);
        }
    }

    /**
     * 1. Remove terms with Term Frequency < 5
     * 2. Remove terms with Document Frequency < 2
     */
    public static void cleanVocab(){

        Iterator<Map.Entry<String,HashMap<String, Integer>>> iterator = vocabulary.entrySet().iterator();
        while (iterator.hasNext()) { //iterate all data
            Map.Entry<String,HashMap<String, Integer>> entry = iterator.next();
            int termFrequency = 0;
            for(String fileName : entry.getValue().keySet()){
                termFrequency += entry.getValue().get(fileName);
            }
            int documentFrequency=entry.getValue().keySet().size();
            if( documentFrequency< DOCUMENT_FREQUENCY_THRESHOLD || termFrequency < TERM_FREQUENCY_THRESHOLD){
                iterator.remove();
            }
        }
    }
}
