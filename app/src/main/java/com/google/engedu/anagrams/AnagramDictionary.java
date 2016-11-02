package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 3;
    private static final int DEFAULT_WORD_LENGTH = 4;
    private static final int MAX_WORD_LENGTH = 10;
    private Random random = new Random(System.currentTimeMillis());
    private int wordLength = DEFAULT_WORD_LENGTH;
    private ArrayList<String> wordList = new ArrayList<String>();
    private HashSet<String> wordSet = new HashSet<String>();
    private HashMap<String,ArrayList<String>> lettersToWords = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();

    public AnagramDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            String sortedWord = sortLetters(word);
            if(lettersToWords.containsKey(sortedWord)){
                lettersToWords.get(sortedWord).add(word);
            } else {
                ArrayList<String> newAL = new ArrayList<String>();
                newAL.add(word);
                lettersToWords.put(sortedWord, newAL);
            }
        }

        Iterator it = lettersToWords.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            ArrayList<String> anagrams = (ArrayList)pair.getValue();
            if((anagrams.size() - 1) >= MIN_NUM_ANAGRAMS){
                if(sizeToWords.containsKey(anagrams.get(0).length())){
                    for(String i : anagrams){
                        sizeToWords.get(anagrams.get(0).length()).add(i);
                    }
                } else {
                    ArrayList<String> newList = new ArrayList<>();
                    for(String i : anagrams) {
                        newList.add(i);
                    }
                    sizeToWords.put(anagrams.get(0).length(), newList);
                }
            }
        }


    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public ArrayList<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String sortedTarget = sortLetters(targetWord);
        for(int i = 0; i < wordList.size(); i++){
            String dictWord = wordList.get(i);
            if(dictWord.length() == targetWord.length()){
                if(sortedTarget.equals(sortLetters(dictWord))){
                    result.add(dictWord);
                }
            }
        }
        return result;
    }

    public String sortLetters(String s){
        char [] sortedArray = s.toCharArray();
        Arrays.sort(sortedArray);
        return new String(sortedArray);
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(char i = 'a'; i <= 'z'; i++){
            String wordPlusOne = word + i;
            String sortedWPO = sortLetters(wordPlusOne);
            if(lettersToWords.containsKey(sortedWPO)){
                ArrayList<String> iAnagrams = lettersToWords.get(sortedWPO);
                for(String s : iAnagrams){
                    result.add(s);
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        if(sizeToWords.containsKey(wordLength)) {
            ArrayList<String> starterList = sizeToWords.get(wordLength);
            int randIndex = random.nextInt(starterList.size());

            for(int i = 0; i < starterList.size(); i++) {
                String word = starterList.get(randIndex);
                String sortedWord = sortLetters(word);
                ArrayList<String> arrayList = lettersToWords.get(sortedWord);
                if (arrayList.size() >= MIN_NUM_ANAGRAMS) {
                    wordLength++;
                    return word;
                }
                randIndex++;
                if (randIndex == starterList.size()) {
                    randIndex = 0;
                }
            }
        }
        wordLength = DEFAULT_WORD_LENGTH;
        return "stop";
    }
}
