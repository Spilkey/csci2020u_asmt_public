package sample;

// WordCounter.java class from in lecture

import java.io.*;
import java.util.*;

public class WordCounter {
    private Map<String, Integer> wordCounts;

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
            scanner.useDelimiter("\\s"); // "[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
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

    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }
}
