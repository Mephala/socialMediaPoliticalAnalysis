package com.gokhanozg.ptnla;

import org.ejml.simple.SimpleMatrix;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by mephala on 5/9/17.
 */
public class CalculateWordCoefficients implements Callable<List<Word>> {

    private List<FacebookTrendInterval> trendIntervals;

    public CalculateWordCoefficients(List<FacebookTrendInterval> trendIntervals) {
        this.trendIntervals = trendIntervals;
    }

    @Override
    public List<Word> call() throws Exception {
        try {
            return calculateMostWordCoefficients();
        } catch (Throwable t) {
            System.out.println("Encountered error, re-calibrating random coefficients");
            t.printStackTrace();
            return calculateMostWordCoefficients();
        }

    }

    private List<Word> calculateMostWordCoefficients() {
        List<Word> wordList = createTweetWordsList(trendIntervals);
        int yColumns = trendIntervals.size();
        SimpleMatrix y = new SimpleMatrix(yColumns, 1);
        for (int i = 0; i < trendIntervals.size(); i++) {
            FacebookTrendInterval interval = trendIntervals.get(i);
            y.setRow(i, 0, interval.getPopulationChange().doubleValue());
        }
        SimpleMatrix A = new SimpleMatrix(yColumns, wordList.size());
        for (int i = 0; i < yColumns; i++) {
            A.setRow(i, 0, getPersistentValues(wordList, trendIntervals.get(i)));
        }
        SimpleMatrix w = A.transpose().mult(A).invert().mult(A.transpose().mult(y));
        for (int i = 0; i < wordList.size(); i++) {
            Word word = wordList.get(i);
            word.setCoefficient(w.get(i, 0));
        }
        Collections.sort(wordList, (o1, o2) -> {
            Double persistentCoefficientCombined1 = o1.getCoefficient() * o1.getPersistentValue();
            Double persistentCoefficientCombined2 = o2.getCoefficient() * o2.getPersistentValue();
            return persistentCoefficientCombined2.compareTo(persistentCoefficientCombined1);
        });
        return wordList;
    }

    private double[] getPersistentValues(List<Word> wordList, FacebookTrendInterval facebookTrendInterval) {
        //tokenizing all used words in the tweet.
        Set<String> words = new HashSet<>();
        List<TweetObject> tweets = facebookTrendInterval.getTweets();
        for (TweetObject tweet : tweets) {
            tokenizeWordsAndAddToSet(words, tweet.getText());
        }


        double[] values = new double[wordList.size()];
        for (int i = 0; i < wordList.size(); i++) {
            Word word = wordList.get(i);
            String wordText = word.getWordText();
            if (words.contains(wordText)) {
                values[i] = word.getPersistentValue();
            } else {
                values[i] = 0; // if word doesn't exist in interval wordset, it's coefficient value doesn't matter.
            }
        }
        return values;
    }

    private void tokenizeWordsAndAddToSet(Set<String> words, String tweetString) {
        StringTokenizer tokenizer = new StringTokenizer(tweetString, " ");
        while (tokenizer.hasMoreTokens()) {
            words.add(tokenizer.nextToken());
        }
    }

    private List<Word> createTweetWordsList(List<FacebookTrendInterval> trendIntervals) {
        List<String> tweetStrings = new ArrayList<>();
        for (FacebookTrendInterval trendInterval : trendIntervals) {
            List<TweetObject> tweets = trendInterval.getTweets();
            for (TweetObject tweet : tweets) {
                String tweetString = tweet.getText();
                tweetStrings.add(tweetString);
            }
        }
        Set<String> words = new HashSet<>();
        for (String tweetString : tweetStrings) {
            tokenizeWordsAndAddToSet(words, tweetString);
        }
        List<Word> wordList = new ArrayList<>();
        for (String word : words) {
            Word wd = new Word();
            wd.setWordText(word);
            wd.setId(UUID.randomUUID().toString());
            wd.setPersistentValue(Math.random());
            wordList.add(wd);
        }
        Collections.sort(wordList);
        return wordList;
    }
}
