import java.io.*;
import java.util.*;

/**
 * @author: Yogiraj Awati
 * Implements Naive Bayesian Classifier
 */
public class NBClassifier {
    private static final String INPUT_PATH = "D:\\NLP_Spring2K17\\HW1\\shakespeare";
    private static final String VOCAB_PATH = "vocabulary.txt";
    private static final String OUTPUT_PATH = "prediction.txt";
    private static final String COMEDY_NAME = "comedies";
    private static final String TRAGEDY_NAME = "tragedies";

    static List<String> allDocuments = new ArrayList<>(); //contains all the file names
    static Set<String> tragedySet = new HashSet<>(); // contains tragedy files
    static Set<String> comedySet = new HashSet<>(); //contains comedy files

    static Map<String, FeatureData> likelyHoodMap = new HashMap<>();
    static Map<String, FeatureData> NB_Model = new HashMap<>();

    static final double LAMBDA = 0.1;
    static long VOCABULARY_SIZE;

    private static int cumulativeTragedyFrequency;
    private static int cumulativeComedyFrequency;


    public static void main(String[] args) {
        loadData(INPUT_PATH); // populates sets

        //####### Question 2
        try (FileWriter writer = new FileWriter(OUTPUT_PATH);
             BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

            // Leave-One Out Cross Validation process
            for (int documentNumber = 0; documentNumber < allDocuments.size(); documentNumber++) {

                //reset data structures for this iteration
                likelyHoodMap = new HashMap<>();
                NB_Model = new HashMap<>();
                cumulativeTragedyFrequency = 0;
                cumulativeComedyFrequency = 0;

                HashSet<String> trainingData = getTrainingDocuments(documentNumber); //Retreives list of Documents except current documentNumber
                calculatePriorAndLikeliHood(VOCAB_PATH, trainingData);
                generateNBModel();

                String testDocumentName = allDocuments.get(documentNumber);
                String predictedClass = applyNBOnTest(VOCAB_PATH, testDocumentName);
                String actualClass = getActualClassForDoc(testDocumentName);
                //write output to file
                bufferedWriter.write(testDocumentName + " --- " + "Actual: " + actualClass + "  Predicted: " + predictedClass + "\n");
            }

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.getCause());
        }


        //####### Question 3 - Training all plays

        trainAllPlays(VOCAB_PATH);

        Map<String, Double> wordLikelihoodLogMap = new HashMap<>();

        for (String word : NB_Model.keySet()) {
            double comedyProbabilityValue = NB_Model.get(word).comedyProbability;
            double tragedyProbabilityValue = NB_Model.get(word).tragedyProbability;
            double ratio = comedyProbabilityValue / tragedyProbabilityValue;
            double logValue = Math.log(ratio) / Math.log(2);
            wordLikelihoodLogMap.put(word, logValue);
        }

        writeDataToFile(wordLikelihoodLogMap);
        sortData(wordLikelihoodLogMap);

    }

    //Author: Ashish Kalbhor
    private static void sortData(Map<String, Double> wordLikelihoodLogMap) {
        TreeMap<String,Double> sortedComedyData = SortByValue(wordLikelihoodLogMap, 1);
        TreeMap<String,Double> sortedTragedyData = SortByValue(wordLikelihoodLogMap, -1);

        System.out.println("Top 20 most 'comic' features: ");
        Iterator<String> comedyIterator = sortedComedyData.keySet().iterator();
        int counter = 0;
        while(comedyIterator.hasNext() && counter < 20)
        {
            System.out.println(comedyIterator.next());
            counter++;
        }
        counter = 0;
        System.out.println("Top 20 most 'tragic' features: ");
        Iterator<String> tragedyIterator = sortedTragedyData.keySet().iterator();
        while(tragedyIterator.hasNext() && counter < 20)
        {
            System.out.println(tragedyIterator.next());
            counter++;
        }


    }

    /**
     * @Author: Ashish Kalbhor
     * Returns a Sorted TreeMap of the given HashMap.
     */
    public static TreeMap<String, Double> SortByValue (Map<String, Double> map, int sign)
    {
        ValueComparator vc =  new ValueComparator(map, sign);
        TreeMap<String,Double> sortedMap = new TreeMap<>(vc);
        sortedMap.putAll(map);
        return sortedMap;
    }


    private static void writeDataToFile(Map<String, Double> wordLikelihoodLogMap) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("LikeLihood.txt"));
            for (String word : wordLikelihoodLogMap.keySet()) {
                writer.write(word + " ----- " + wordLikelihoodLogMap.get(word) + "\n");
            }
            writer.close();
        } catch (IOException e) {

        }
    }

    private static void trainAllPlays(String vocabularyPath) {
        likelyHoodMap = new HashMap<>();
        NB_Model = new HashMap<>();
        HashSet<String> trainingData = new HashSet<>();
        for (String documentName : allDocuments) {
            trainingData.add(documentName);
        }
        calculatePriorAndLikeliHood(vocabularyPath, trainingData);
        generateNBModel(); //generate model for all data
    }

    /**
     * @param testDocumentName
     * @return : Actual class for the given document name
     */
    private static String getActualClassForDoc(String testDocumentName) {
        return ((tragedySet.contains(testDocumentName)) ? "tragedy" : "comedy");
    }

    /**
     * @param vocabPath
     * @param testDocumentName
     * @return Whether testDocumentName is comedy or trajedy based on Model prediction
     */
    private static String applyNBOnTest(String vocabPath, String testDocumentName) {
        double comedyValue = 0.0;
        double tragedyValue = 0.0;

        VOCABULARY_SIZE = 0;
        try (FileReader fileReader = new FileReader(vocabPath);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while (null != (line = reader.readLine())) {
                VOCABULARY_SIZE++;
                String[] data = line.split(Tokenizer.SEPERATOR);
                String word = data[0];
                String records = data[1];
                // Pick the term from vocabulary only if present in given test doc.
                if (records.contains(testDocumentName)) {
                    comedyValue += Math.log(NB_Model.get(word).comedyProbability);
                    tragedyValue += Math.log(NB_Model.get(word).tragedyProbability);
                }
            }
            double likelihoodRatio = (comedyValue / tragedyValue);
            System.out.println("Document Name : " + testDocumentName + " Likelihood Ratio = " + likelihoodRatio);
            return (comedyValue > tragedyValue ? "comedy" : "tragedy");

        } catch (IOException e) {
            System.out.println(e.getCause());
        }
        return null;
    }

    /**
     * Creates NB Model from train data
     */
    private static void generateNBModel() {
        for (String word : likelyHoodMap.keySet()) {

            //Calculate prior for the word

            //calculate likelihoods
            int comedyFrequency = likelyHoodMap.get(word).comedyFrequency;
            int tragedyFrequency = likelyHoodMap.get(word).tragedyFrequency;

            //calculate both prob
            double comedyProbability = likelyHoodMap.get(word).comedyProbability;
            double tragedyProbability = likelyHoodMap.get(word).tragedyProbability;

            FeatureData feature = new FeatureData(comedyFrequency, tragedyFrequency);
            feature.setComedyProbability(comedyProbability);
            feature.setTragedyProbability(tragedyProbability);
            //populate model
            NB_Model.put(word, feature);
        }
    }


    public static void calculatePriorAndLikeliHood(String vocabularyPath, Set<String> trainingData) {
        calculateLikeliHood(vocabularyPath, trainingData);
        // Currently the table holds frequencies, lets iterate again to calculate probabilities.

    }

    private static void calculateLikeliHood(String vocabularyPath, Set<String> trainingData) {
        // System.out.println("Calculation Likelihood....");
        try (FileReader fileReader = new FileReader(vocabularyPath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(Tokenizer.SEPERATOR);

                // Line format : "xyz : {(document1,34)(document2,67)}"
                String word = data[0]; //get word
                String record = data[1]; //{(document1,34)(document2,67)}

                // First load the frequencies for each term.
                FeatureData features = calculateFrequenciesPerWord(record, trainingData);

                // Keep cumulatively adding the comedy and tragedy frequencies
                cumulativeComedyFrequency += features.comedyFrequency;
                cumulativeTragedyFrequency += features.tragedyFrequency;

                // Update the likelihood table with frequencies
                likelyHoodMap.put(word, features);
            }

        } catch (IOException e) {

        }
        //Calculate probability
        for (String word : likelyHoodMap.keySet()) {
            likelyHoodMap.get(word).calculateProbabilities(cumulativeComedyFrequency, cumulativeTragedyFrequency);
        }
    }


    /**
     * @param record
     * @param trainingData
     * @return : FeatureData object which calculates comedy and tragedy frequency for the given word
     */
    private static FeatureData calculateFrequenciesPerWord(String record, Set<String> trainingData) {
        int comedyFreq = 0;
        int tragedyFreq = 0;

        record = record.substring(1, record.length() - 2); //trim {}
        String[] tuples = record.split("\\)");

        for (String tuple : tuples) {
            String documentName = tuple.split(",")[0].substring(1); //get document name
            String frequency = tuple.split(",")[1]; //get frequency in that document

            if (trainingData.contains(documentName)) { //calculate for only those present in training data
                if (comedySet.contains(documentName)) {
                    comedyFreq += Integer.parseInt(frequency);
                } else {
                    tragedyFreq += Integer.parseInt(frequency);
                }
            }
        }
        return (new FeatureData(comedyFreq, tragedyFreq));
    }

    private static HashSet<String> getTrainingDocuments(int documentNumber) {
        int count = 0;
        HashSet<String> trainingData = new HashSet<>();
        //System.out.println("Populating Training data");
        for (String document : allDocuments) {
            if (count != documentNumber) {
                trainingData.add(document);
            }
            count++;
        }
        return trainingData;
    }


    private static void loadData(String inputPath) {
        File inputFolder = new File(inputPath);

        for (final File file : inputFolder.listFiles()) {
            if (file.isDirectory()) {
                loadData(file.getPath());
            } else {
                if (file.getAbsolutePath().contains(COMEDY_NAME)) {
                    comedySet.add(file.getName());
                } else if (file.getAbsolutePath().contains(TRAGEDY_NAME)) {
                    tragedySet.add(file.getName());
                }
                allDocuments.add(file.getName());
            }
        }
    }


}

class FeatureData {
    int comedyFrequency;
    int tragedyFrequency;
    double comedyProbability;
    double tragedyProbability;

    public FeatureData(int comedyFrequency, int tragedyFrequency) {
        this.comedyFrequency = comedyFrequency;
        this.tragedyFrequency = tragedyFrequency;
    }

    public FeatureData() {
        comedyFrequency = 0;
        tragedyFrequency = 0;
        comedyProbability = 0.0;
        tragedyProbability = 0.0;
    }

    public void setComedyProbability(double comedyProbability) {
        this.comedyProbability = comedyProbability;
    }

    public void setTragedyProbability(double tragedyProbability) {
        this.tragedyProbability = tragedyProbability;
    }

    void calculateProbabilities(int totalComedyFrequency, int totalTragedyFrequency) {

        long V = NBClassifier.VOCABULARY_SIZE;

        this.comedyProbability = ((double) this.comedyFrequency + NBClassifier.LAMBDA) / ((double) totalComedyFrequency + (NBClassifier.LAMBDA * V));
        this.tragedyProbability = ((double) this.tragedyFrequency + NBClassifier.LAMBDA) / ((double) totalTragedyFrequency + (NBClassifier.LAMBDA * V));
    }

    @Override
    public String toString() {
        return "Feature{" +
                "comedyFrequency=" + comedyFrequency +
                ", tragedyFrequency=" + tragedyFrequency +
                ", comedyProbability=" + comedyProbability +
                ", tragedyProbability=" + tragedyProbability +
                '}';
    }
}


/**
 * Author: Ashish Kalbhor
 */
class ValueComparator implements Comparator<String> {
    Map<String, Double> map;
    int sign;

    public ValueComparator(Map<String, Double> base, int sign) {
        this.map = base;
        this.sign = sign;
    }

    public int compare(String a, String b) {
        if (sign == 1) {
            if (map.get(a) >= map.get(b)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (map.get(a) >= map.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
