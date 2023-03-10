package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {

    class spamProbabilityComparator  implements Comparator<TestFile>{

        public int compare(TestFile file1, TestFile file2){

            if (file1.getSpamProbability() == file2.getSpamProbability()){
                return 0;
            }
            else if (file1.getSpamProbability() > file2.getSpamProbability()){
                return 1;
            }
            else{
                return -1;
            }
        }
    }

    public List<TestFile> trainAndTest(File mainDirectory) throws FileNotFoundException {
//        TODO: main method of loading the directories and files, training and testing the model

        //Access training file ham
        File ham = new File(mainDirectory, "train/ham");

        //Access training file ham2
        File ham2 = new File(mainDirectory, "train/ham2");

        //Access
        File spam = new File(mainDirectory, "train/spam");

        //Frequency Map Initializations
        Map<String,Integer> trainHamFreq = new TreeMap<String,Integer>();
        Map<String,Integer> trainSpamFreq = new TreeMap<String,Integer>();

        //Analyze frequency of ham files containing a word
        int ham_length = countInstance(trainHamFreq, ham);
        int ham2_length = countInstance(trainHamFreq, ham2);

        //Sum the number of ham files in each directory
        int ham_size = ham_length + ham2_length;

        //Analyze frequency of spam files containing a word
        int spam_size = countInstance(trainSpamFreq, spam);


        for (String key: trainHamFreq.keySet()){

            if (!trainSpamFreq.containsKey(key)){
                trainSpamFreq.put(key, 0);
            }
        }

        for (String key: trainSpamFreq.keySet()){

            if (!trainHamFreq.containsKey(key)){
                trainHamFreq.put(key, 0);
            }
        }

        Map<String, Double> probMap = createProbabilityMap(trainHamFreq, trainSpamFreq, ham_size, spam_size);

        File spam_test = new File(mainDirectory, "test/spam");
        File ham_test = new File(mainDirectory, "test/ham");

        ArrayList<TestFile> testList = new ArrayList<>();

        TestTestFileList(spam_test, "spam", probMap, testList);
        TestTestFileList(ham_test, "ham", probMap, testList);

        Collections.sort(testList, new spamProbabilityComparator());

        return testList;
    }

    //Modifies a wordMap to create entries <Word, frequency> where frequency is the number of files containing
    //that word
    private int countInstance(Map<String, Integer> wordMap, File file) throws FileNotFoundException {

        //Create list of email files in the directory
        File[] emails = file.listFiles();

        //Get length of file to return
        int emailsNumb = emails.length;

        //For each email file in the directory
        for (File email: emails){

            //Create map that tracks the frequency of words in each email
            Map<String, Integer> tempMap = new TreeMap<>();

            if (email.exists()){
                Scanner scanner = new Scanner(email);

                while(scanner.hasNext()){

                    //Convert all words to lower case
                    String word = (scanner.next()).toLowerCase();

                    if (isWord(word)){

                        //Add word to temp map if it doesn't exist already
                        if (!tempMap.containsKey(word)){
                            tempMap.put(word, 1);
                        }
                        else{ //Increment the temporary count if exists already
                            int prevCountTemp = tempMap.get(word);
                            tempMap.put(word, prevCountTemp+1);
                        }

                        //Add word to word map if it doesn't exist already
                        if (!wordMap.containsKey(word)){
                            wordMap.put(word, 1);
                        }
                        else{
                            //If the word exists already, increment the count only if the temp count of
                            //the word is 1
                            if (tempMap.get(word) == 1){
                                int prevCountWord = wordMap.get(word);
                                wordMap.put(word, prevCountWord+1);
                            }
                        }
                    }

                }

            }


        }
        return emailsNumb;

    }

    private Map<String, Double> createProbabilityMap(Map<String, Integer> ham, Map<String, Integer> spam, int ham_size, int spam_size){

        Map<String, Double> probMap = new TreeMap<>();

        for (String key: spam.keySet()){

            double probWiS = ((double) spam.get(key))/(spam_size);

            double probWiH = ((double) ham.get(key))/(ham_size);

            double probSWi = (probWiS)/(probWiS + probWiH);

            probMap.put(key, probSWi);
        }

        return probMap;
    }

    private void TestTestFileList(File dir, String actualClass, Map<String, Double> probMap, ArrayList<TestFile> fileList) throws FileNotFoundException {

        File[] testFiles = dir.listFiles();

        for (File testFile: testFiles){

            if (testFile.exists()){

                Scanner scanner = new Scanner(testFile);

                double nu = 0;

                while(scanner.hasNext()){

                    //Convert all words to lower case
                    String word = (scanner.next()).toLowerCase();

                    if (isWord(word)){

                        if (probMap.containsKey(word)){
                            //System.out.println(probMap.get(word));


                            if ((probMap.get(word) > 0) && probMap.get(word) < 1){
                                nu += Math.log(1- probMap.get(word)) - Math.log(probMap.get(word));
                            }

                        }


                    }
                }

                double probSF = 1/(1 + Math.pow(Math.E,nu));

                TestFile testFileObject = new TestFile(testFile.getName(),probSF, actualClass);

                fileList.add(testFileObject);

            }


        }


    }

    private Boolean isWord(String word){
        if (word == null){
            return false;
        }

        String pattern = "^[a-zA-Z]*$";
        if(word.matches(pattern)){
            return true;
        }

        return false;

    }
}

