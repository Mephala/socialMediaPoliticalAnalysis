package com.gokhanozg.ptnla;

import org.apache.commons.io.FileUtils;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by mephala on 5/9/17.
 */
public class CalculateWordCoefficients implements Callable<List<Word>> {

    private Politician p;

    public CalculateWordCoefficients(Politician p) {
        this.p = p;
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

    private List<Word> calculateMostWordCoefficients() throws IOException, ParseException {
        List<Word> wordList = createTweetWordsList(p.getTrendIntervals());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        Map<String, List<TweetPopularity>> popularityTweets = getPopularityTweets(sdf, p);
        List<TweetPopularity> orderedPopularityTweets = new ArrayList<>();
        for (List<TweetPopularity> tweetPopularities : popularityTweets.values()) {
            orderedPopularityTweets.addAll(tweetPopularities);
        }
        SimpleMatrix y = new SimpleMatrix(orderedPopularityTweets.size(), 1);
        int row = 0;
        for (TweetPopularity orderedPopularityTweet : orderedPopularityTweets) {
            y.setRow(row, 0, orderedPopularityTweet.getPopularityGain().doubleValue());
            row++;
        }
        List<Word> filteredWordList = p.getWords();
        if (filteredWordList == null)
            filteredWordList = wordList;
        filterWordLists(filteredWordList);
        int size = y.numRows();
        List<Word> randomizedWords = randomlySelectWithSize(size - 1, filteredWordList); // bcuz 1st coefficient has no multiplier
        System.out.println(randomizedWords.size());
        Collections.sort(randomizedWords);
        SimpleMatrix A = new SimpleMatrix(size, size); // Design matrix
        int Arow = 0;
        for (TweetPopularity orderedPopularityTweet : orderedPopularityTweets) {
            double[] persistentValues = getPersistentValues(orderedPopularityTweet, randomizedWords);
            A.setRow(Arow, 0, persistentValues);
            Arow++;
        }
        SimpleMatrix w = A.transpose().mult(A).invert().mult(A.transpose().mult(y));
        for (int i = 0; i < randomizedWords.size(); i++) {
            randomizedWords.get(i).setCoefficient(w.get(i + 1, 0));
        }
        Collections.sort(wordList, (o1, o2) -> {
            Double impact1 = o1.getCoefficient() * o1.getPersistentValue();
            Double impact2 = o2.getCoefficient() * o2.getPersistentValue();
            return impact2.compareTo(impact1);
        });
        return wordList;
    }

    private double[] getPersistentValues(TweetPopularity orderedPopularityTweet, List<Word> randomizedWords) {
        String tweetText = orderedPopularityTweet.getTweetText();
        String[] words = tweetText.split(" ");
        double[] values = new double[randomizedWords.size() + 1];
        for (int i = 1; i < values.length; i++) {
            String word = randomizedWords.get(i - 1).getWordText();
            double v = 0;
            for (String s : words) {
                if (s.equals(word))
                    v = randomizedWords.get(i - 1).getPersistentValue();
            }
            values[i] = v;
        }
        values[0] = 1; //Design matrix first value is 1 due to model.
        return values;
    }

    private List<Word> randomlySelectWithSize(int size, List<Word> filteredWordList) {
        if (filteredWordList.size() <= size)
            return filteredWordList;
        List<Word> random = new ArrayList<>();
        Random r = new Random();
        while (random.size() != size) {
            Word rWord = filteredWordList.get(r.nextInt(filteredWordList.size()));
            if (random.contains(rWord)) {
                continue;
            } else {
                random.add(rWord);
            }
        }
        return random;
    }

    private void filterWordLists(List<Word> wordList) throws IOException {
        Set<String> ignoredWords = new HashSet<>();
        File f = new File(this.getClass().getClassLoader().getResource("ignoredTurkishWords.txt").getPath());
        List<String> lines = FileUtils.readLines(f);
        for (String line : lines) {
            ignoredWords.add(line.trim());
        }
        Locale locale = Locale.forLanguageTag("tr");
        Iterator<Word> wordIterator = wordList.iterator();
        Set<String> duplicateWordSet = new HashSet<>();
        while (wordIterator.hasNext()) {
            Word next = wordIterator.next();
            String val = next.getWordText();
            val = val.toLowerCase(locale);
            val = val.trim();
            val = val.replaceAll("\\!", "");
            val = val.replaceAll("\\.", "");
            val = val.replaceAll(",", "");
            val = val.replaceAll("”", "");
            if (val.endsWith("dir") || val.endsWith("dır")) {
                wordIterator.remove();
            } else if (val.endsWith("miştir") || val.endsWith("muştur") || val.endsWith("müştür") || val.endsWith("mıştır")) {
                wordIterator.remove();
            } else if (isNumeric(val)) {
                wordIterator.remove();
            } else if (val.endsWith("'da") || val.endsWith("'de") || val.endsWith("’de") || val.endsWith("’da") || (val.endsWith("'dan") || val.endsWith("'den") || val.endsWith("’den") || val.endsWith("’dan"))) {
                wordIterator.remove();
            } else if (val.endsWith("'na") || val.endsWith("'ü") || val.endsWith("’ü") || val.endsWith("'u") || val.endsWith("’u") || val.endsWith("'yü") || val.endsWith("’yü") || val.endsWith("'yu") || val.endsWith("’yu") || val.endsWith("'la") || val.endsWith("’la") || val.endsWith("'le") || val.endsWith("’le") || val.endsWith("'teki") || val.endsWith("’teki") || val.endsWith("'taki") || val.endsWith("’taki") || val.endsWith("'ni") || val.endsWith("’ni") || val.endsWith("'nı") || val.endsWith("’nı") || val.endsWith("'i") || val.endsWith("’i") || val.endsWith("'ı") || val.endsWith("’ı") || val.endsWith("'ne") || val.endsWith("’ne") || val.endsWith("’na") || (val.endsWith("'ndan") || val.endsWith("'nden") || val.endsWith("’nden") || val.endsWith("’ndan"))) {
                wordIterator.remove();
            } else if (val.endsWith("'ta") || val.endsWith("'te") || val.endsWith("’te") || val.endsWith("’ta") || (val.endsWith("'tan") || val.endsWith("'ten") || val.endsWith("’ten") || val.endsWith("’tan"))) {
                wordIterator.remove();
            } else if (val.endsWith("'a") || val.endsWith("'e") || val.endsWith("’i") || val.endsWith("’ı") || val.endsWith("’ya") || val.endsWith("’ye") || val.endsWith("’a") || val.endsWith("’e") || (val.endsWith("'yi") || val.endsWith("'yı")) || (val.endsWith("'ye") || val.endsWith("'ya") || val.endsWith("’yi"))) {
                wordIterator.remove();
            } else if (val.endsWith("'nın") || val.endsWith("'nin") || val.endsWith("'nun") || val.endsWith("'nün") || val.endsWith("’nin") || val.endsWith("’nun") || val.endsWith("’nün") || val.endsWith("’nın") || (val.endsWith("'yi") || val.endsWith("'yı"))) {
                wordIterator.remove();
            } else if (val.endsWith("'") || val.endsWith("’") || val.endsWith("?")) {
                wordIterator.remove();
            } else if (val.endsWith("'yla") || val.endsWith("'yle") || val.endsWith("’yla") || val.endsWith("’yle")) {
                wordIterator.remove();
            } else if (val.length() <= 2) {
                wordIterator.remove();
            } else if (val.endsWith("...")) {
                wordIterator.remove();
            } else if (val.endsWith("'in") || val.endsWith("'ın") || val.endsWith("'nin") || val.endsWith("'nın") || val.endsWith("’in") || val.endsWith("’ın") || val.endsWith("’nin") || val.endsWith("’nın")) {
                wordIterator.remove();
            } else if (ignoredWords.contains(val)) {
                wordIterator.remove();
            } else if (val.startsWith("@") || val.startsWith("#")) {
                wordIterator.remove();
            } else if (val.startsWith("http://") || val.startsWith("https://")) {
                wordIterator.remove();
            } else if (duplicateWordSet.contains(val)) {
                wordIterator.remove();
            } else {
                for (String ignoredWord : ignoredWords) {
                    if (val.startsWith(ignoredWord) || val.endsWith(ignoredWord)) {
                        wordIterator.remove();
                        break;
                    }
                }
            }
            duplicateWordSet.add(val);
        }
        Collections.sort(wordList);
    }

    private boolean isNumeric(String val) {
        try {
            new BigDecimal(val);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private Map<String, List<TweetPopularity>> getPopularityTweets(SimpleDateFormat sdf, Politician p) throws ParseException {
        List<FacebookTrendInterval> intervals = p.getTrendIntervals();
        Map<String, List<TweetPopularity>> popularityTweets = new HashMap<>();
        for (FacebookTrendInterval interval : intervals) {
            if (interval.getTweets() == null || interval.getTweets().isEmpty())
                continue;
            BigDecimal pchange = interval.getPopulationChange();
            List<TweetObject> tweetObjects = interval.getTweets();
            BigDecimal perDayPChange = pchange.divide(BigDecimal.valueOf(tweetObjects.size()), 2, BigDecimal.ROUND_HALF_UP);
            for (TweetObject tweetObject : tweetObjects) {
                String createAt = tweetObject.getCreatedAt();
                Date d = sdf.parse(createAt);
                String tweetId = tweetObject.getId();
                String tweetText = tweetObject.getText();
                Long faveCount = tweetObject.getFavoriteCount();
                TweetPopularity tp = new TweetPopularity(d, tweetId, tweetText, faveCount);
                String mdy = tp.getMdy();
                if (popularityTweets.containsKey(mdy)) {
                    List<TweetPopularity> tpList = popularityTweets.get(mdy);
                    tpList.add(tp);
                    BigDecimal total = perDayPChange;
                    BigDecimal weightSum = BigDecimal.ZERO;
                    for (TweetPopularity tweetPopularity : tpList) {
                        weightSum = weightSum.add(tweetPopularity.getWeight());
                    }
                    BigDecimal popularityPerWeight = total.divide(weightSum, 2, BigDecimal.ROUND_HALF_UP);
                    for (TweetPopularity tweetPopularity : tpList) {
                        tweetPopularity.setPopularityGain(popularityPerWeight.multiply(tweetPopularity.getWeight()));
                    }

                } else {
                    List<TweetPopularity> tpList = new ArrayList<>();
                    tpList.add(tp);
                    tp.setPopularityGain(perDayPChange);
                    popularityTweets.put(mdy, tpList);
                }
            }
        }


        return popularityTweets;
    }

    private double[] getPersistentValues(List<Word> wordList, FacebookTrendInterval facebookTrendInterval) {
        //tokenizing all used words in the tweet.
        Set<String> words = new HashSet<>();
        List<TweetObject> tweets = facebookTrendInterval.getTweets();
        if (tweets != null) {
            for (TweetObject tweet : tweets) {
                tokenizeWordsAndAddToSet(words, tweet.getText());
            }
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
            if (tweets != null) {
                for (TweetObject tweet : tweets) {
                    String tweetString = tweet.getText();
                    tweetStrings.add(tweetString);
                }
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
