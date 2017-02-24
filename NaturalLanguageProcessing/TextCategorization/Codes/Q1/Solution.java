import java.io.*;
import java.util.*;


/**
 * @Author - Yogiraj Awati
 */
public class Solution {

    private static final int V = 37; // 26 Alphabets + 1 space+ 0-9 digits
    private static final double LAMBDA = 0.1; //Lambda constant for smoothing
    private static final String TEST_FILE = "sample.txt";
    private static final String MARKOV_FILE_PATH = "markov.txt";

    private static Map<String, Integer> bigramCount = new HashMap<>();
    private static Map<String, Integer> trigramCount = new HashMap<>();
    private static Map<String,Double> markovModel = new HashMap<>(); //second order markov model

    public static void main(String[] args) throws InterruptedException {


        List<String> lines = readFile(TEST_FILE);
        extractNgrams(lines,bigramCount,2);
        extractNgrams(lines,trigramCount,3);
        generateModel(bigramCount,trigramCount,markovModel);
        writeModelToFile(markovModel);

        //calculate cross entropy
        String input="";
         input = "he somehow made this analogy sound exciting instead of hopeless";
        calculateCrossEntropy(input,markovModel);

         input = "no living humans had skeletal features remotely like these";
        calculateCrossEntropy(input,markovModel);

        input = "frequent internet and social media users do not have higher stress levels";
        calculateCrossEntropy(input,markovModel);

        input = "the sand the two women were sweeping into their dustpans was transferred into plastic bags";
        calculateCrossEntropy(input,markovModel);


    }

    private static void writeModelToFile(Map<String, Double> model) {
        try (FileWriter fileWriter = new FileWriter(MARKOV_FILE_PATH);
             BufferedWriter writer = new BufferedWriter(fileWriter)) {
            Set<String> terms = model.keySet();
            for (String term : terms) {
                double probability = model.get(term);
                writer.write(term + " : "+"{" + probability + "}\n");
            }

        } catch (IOException e) {

        }
    }

    private static void calculateCrossEntropy(String input, Map<String, Double> markovModel) {
         Map<String,Integer> trigrams = new HashMap<>();
         extractNgrams(Arrays.asList(input),trigrams,3); //get trigrams

        double cumulativeSum =0.0;

        for(String trigram : trigrams.keySet()){
            double probabilityFromModel= getProbabilityFromCorpus(trigram);
            cumulativeSum +=  (Math.log(probabilityFromModel)/Math.log(2));
        }

        Double entropyForLine = -1 * cumulativeSum / (input.length() - 2); //using hint
        System.out.println("line : "+input+" #### Cross Entropy: "+entropyForLine);
    }

    private static double getProbabilityFromCorpus(String trigram) {
        String bigram = getBiGramFromTriGram(trigram);
        double probability;

        int bigramFrequency = getValue(bigram,bigramCount);
        int trigramFrequency = getValue(trigram,trigramCount);
        double numerator = (double)trigramFrequency+LAMBDA;
        double denominator = (double)bigramFrequency +(LAMBDA * V);
        probability =  numerator/denominator;

        return  probability;
    }


    private static void generateModel(Map<String, Integer> bigramCount, Map<String, Integer> trigramCount, Map<String, Double> markovModel) {
        trigramCount.forEach((trigram,trigramFrequency)->{
            applyLambdaSmoothing(trigram,trigramFrequency,bigramCount,markovModel);
        });
    }

    /**
     * @param trigram
     * @param trigramFrequency
     * Applies Lambda smoothing for lambda = 0.1
     * @param bigramCount
     * @param markovModel

     */
    private static void applyLambdaSmoothing(String trigram, Integer trigramFrequency, Map<String, Integer> bigramCount, Map<String, Double> markovModel) {
        String biGram = getBiGramFromTriGram(trigram);
        int bigramFrequency = getValue(biGram,bigramCount);
        double numerator = trigramFrequency+LAMBDA;
        double denominator = bigramFrequency +(LAMBDA * V);
        double probability =  numerator/denominator;
        markovModel.put(trigram,probability); // populate the model
    }

    /**
     * @return : if value present return the value else 0
     */
    private static int getValue(String key, Map<String, Integer> map) {
        if(map.containsKey(key))
            return map.get(key);
        return 0;
    }

    /**
     *
     * @param trigram
     * @return : BiGram for the given trigram
     */
    private static String getBiGramFromTriGram(String trigram) {
        return(trigram.equals(null)? null : trigram.substring(0,2)); //return first two characters
    }

    /**
     *  @param lines
     * @param i : the value of gram : 1,2
     *
     */
    private static void extractNgrams(List<String> lines, Map<String, Integer> map, int i) {
        lines.forEach(line->{
            populateNGrams(line,i,map);
        });
    }

    /**
     *  @param line
     * @param n
     * @param map
     */
    private static void populateNGrams(String line, int n, Map<String, Integer> map) {
        String processedLine = removePunctuations(line);
        int lowerBound = 0;
        for(int upperBound=n-1;upperBound<processedLine.length();upperBound+=1){
            String nGram="";
            nGram = processedLine.substring(lowerBound,upperBound+1);
            populateData(nGram,map);
            lowerBound++;
        }
    }

    /**
     *  @param nGram
     */
    private static void populateData(String nGram, Map<String, Integer> currentMap) {
        if(currentMap.containsKey(nGram)){
           int oldValue = currentMap.get(nGram);
           currentMap.put(nGram,oldValue+1);
        }else{
            currentMap.put(nGram,1); //for the first entry in the map
        }
    }

    /**
     *
     * @param s
     * @return
     * Removes punctuation from a line. Keeps only alphabets,digits,and trimmed spaces
     */
    public static String removePunctuations(String line) {
        return line.replaceAll("(\\w+)\\p{Punct}(\\s|$)", "$1$2").toLowerCase();
    }


    /**
     *
     * @param FILENAME
     * @return
     * Reads data from file
     */
    private static List<String> readFile(String FILENAME) {
        BufferedReader br = null;
        FileReader fr = null;
        List<String> lines = new ArrayList<>();
        try {

            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);
            String currentLine;
            br = new BufferedReader(new FileReader(FILENAME));
            while ((currentLine = br.readLine()) != null) {
                lines.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return lines;
    }

}
