// WordCounter.java class from in lecture

import java.io.*;
import java.util.*;

public class WordCounter {
    private Map<String, Integer> wordCounts;
    private int numOfFiles;
    public WordCounter() {
        wordCounts = new HashMap<>();
    }

    public void processFile(File file) throws IOException {
        LinkedList<String> words = new LinkedList<>();
        System.out.println("Processing " + file.getAbsolutePath() + "...");
        if (file.isDirectory()) {

            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current : contents) {
                processFile(current);
            }
        } else if (file.exists()) {
            // count the words in this file
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word)) {
                    if(!words.contains(word)){
                        countWord(word);
                        words.add(word);
                    }

                }
            }
            words.clear();
        }
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        return word.matches(pattern);
    }

    private void countWord(String word) {
        if (wordCounts.containsKey(word)) {
            int oldCount = wordCounts.get(word);
            wordCounts.put(word, oldCount + 1);
        } else {
            wordCounts.put(word, 1);
        }
    }

    public void showWordCounts(){
        System.out.println(wordCounts);
    }

    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }


    //        outFile.createNewFile();
//        if (outFile.canWrite()) {
//            PrintWriter fileOut = new PrintWriter(outFile);
//
//            Set<String> keys = wordCounts.keySet();
//            Iterator<String> keyIterator = keys.iterator();
//
//            while (keyIterator.hasNext()) {
//                String key = keyIterator.next();
//                int count = wordCounts.get(key);
//
//                if (count >= minCount) {
//                    fileOut.println(key + ": " + count);
//                }
//            }
//
//            fileOut.close();
//        } else {
//            System.err.println("Error:  Cannot write to file: " + outFile.getAbsolutePath());
//        }



  /*public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Usage: java WordCounter <dir> <outfile>");
      System.exit(0);
    }

    WordCounter wordCounter = new WordCounter();
    File dataDir = new File(args[0]);
    File outFile = new File(args[1]);

    try {
      wordCounter.processFile(dataDir);
      wordCounter.outputWordCounts(2, outFile);
    } catch (FileNotFoundException e) {
      System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }*/
}
