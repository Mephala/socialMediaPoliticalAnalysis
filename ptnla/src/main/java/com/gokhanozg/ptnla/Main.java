package com.gokhanozg.ptnla;

import com.gokhanozg.ptnla.api.TwitterConnector;
import com.gokhanozg.ptnla.argument.ArgumentParser;
import com.gokhanozg.ptnla.dao.PoliticanDao;
import org.apache.http.HttpException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mephala on 4/24/17.
 */
public class Main {

    private static final String KK_TWITTER_NAME = "kilicdarogluk";
    //    private static final String RTE_TWITTER_NAME = "RT_Erdogan";
    private static final int THREAD = 8;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
    private static final int EPOCH = 1;
    private static final int BUFFED_RATIO = 500;
    private static PoliticanDao politicanDao = new PoliticanDao();
    private static List<Politician> allPoliticians;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

    public static void main(String[] args) {
        try {
            Map<String, String> arguments = ArgumentParser.getArguments(args);
            if (arguments.containsKey("build")) {
                createPoliticansIfNotExists();
                initTrendsIfNotInitiated();
                initTweetsIfNotInitiated();
                createRegressionCoefficients();
            }
            if (arguments.containsKey("predictFor")) {
                predictFor(arguments);
            }
            if (arguments.containsKey("evaluate")) {
                evaluate(arguments);
            }
        } catch (Throwable t) {
            System.err.println("Program terminated due to Fatal error.");
            t.printStackTrace();
        }


    }

    private static void evaluate(Map<String, String> arguments) {
        String tweeterName = arguments.get("evaluate");
        Politician politician = findPoliticianByName(tweeterName);
        if (politician == null) {
            System.err.println("No politician with given tweeterName:" + tweeterName);
            System.exit(0);
        }
        Map<String, Word> textToWordsMap = buildWordMapForPolitician(politician);
        List<FacebookTrendInterval> trendIntervals = politician.getTrendIntervals();
        for (FacebookTrendInterval trendInterval : trendIntervals) {
            List<TweetObject> tweetObjects = trendInterval.getTweets();
            BigDecimal modelPrediction = BigDecimal.ZERO;
            for (TweetObject tweetObject : tweetObjects) {
                String[] words = tweetObject.getText().split(" ");
                for (String word : words) {
                    modelPrediction = modelPrediction.add(BigDecimal.valueOf(textToWordsMap.get(word).getCoefficient()).multiply(BigDecimal.valueOf(textToWordsMap.get(word).getPersistentValue())));
                }
            }
            System.out.println("From:" + trendInterval.getStart() + ", Until:" + trendInterval.getEnd() + ", popularityGain:" + trendInterval.getPopulationChange() + " (people), modelPrediction:" + modelPrediction + " , error:" + (trendInterval.getPopulationChange().subtract(modelPrediction)));
        }

    }

    private static void predictFor(Map<String, String> arguments) {
        String tweeterName = arguments.get("predictFor");
        Politician p = findPoliticianByName(tweeterName);
        if (p == null) {
            System.err.println("No politician with given twitter account name is found. Please check typo.");
            System.exit(0);
        }

        String tweet = arguments.get("tweet");
        if (tweet == null || tweet.length() == 0) {
            System.err.println("Tweet String is mandatory for prediction. Example: --predictFor <politicanTweeterAccount> --tweet something everything is sometimes.");
            System.exit(0);
        } else {
            predictUserGainFromSingleTweet(tweet, p);
        }
    }

    private static Politician findPoliticianByName(String tweeterName) {
        Politician p = null;
        allPoliticians = politicanDao.getAllPoliticians();
        for (Politician politician : allPoliticians) {
            if (politician.getPoliticianTwitterAccountName().equals(tweeterName)) {
                p = politician;
                break;
            }
        }
        return p;
    }

    private static void predictUserGainFromSingleTweet(String tweet, Politician politician) {
        System.out.println("Prediction analysis for politician:" + politician.getPoliticianTurkishName() + ", tweet:" + tweet);
        Map<String, Word> textToWordsMap = buildWordMapForPolitician(politician);
        String[] tweetWords = tweet.split(" ");
        BigDecimal totalPopularity = BigDecimal.ZERO;
        for (String tweetWord : tweetWords) {
            if (textToWordsMap.containsKey(tweetWord)) {
                Word w = textToWordsMap.get(tweetWord);
                BigDecimal gain = BigDecimal.valueOf(w.getCoefficient() * w.getPersistentValue());
                totalPopularity = totalPopularity.add(gain);
                System.out.println("Impact of the word:" + tweetWord + " is " + gain + " users.");
            } else {
                System.out.println("Word:" + tweetWord + " is not found in the database.");
            }
        }
        System.out.println("Total predicted user gain for the tweet is:" + totalPopularity.toPlainString());
    }

    private static Map<String, Word> buildWordMapForPolitician(Politician politician) {
        Map<String, Word> textToWordsMap = new HashMap<>();
        List<Word> words = politician.getWords();
        for (Word word : words) {
            textToWordsMap.put(word.getWordText(), word);
        }
        return textToWordsMap;
    }

    private static void createRegressionCoefficients() throws Exception {
        if (allPoliticians == null || allPoliticians.isEmpty()) {
            allPoliticians = politicanDao.getAllPoliticians();
        }
        for (Politician politician : allPoliticians) {

            if (politician.getWords() != null && politician.getWords().size() > 0) {
                System.out.println("Previous word analysis found for politician:" + politician.getPoliticianTurkishName() + " , removing previous data");
                politician.setWords(null);
                politicanDao.savePolitician(politician);
            }
            System.out.println("Calculating word coefficients for politician:" + politician.getPoliticianTurkishName());


//            List<Future<List<Word>>> calculatedWordListListFutures = new ArrayList<>();
//            List<List<Word>> calculatedWordListList = new ArrayList<>();
//            for (int i = 0; i < EPOCH; i++) {
//                CalculateWordCoefficients wordCoefficientsCalculator = new CalculateWordCoefficients(politician);
//                calculatedWordListListFutures.add(executorService.submit(wordCoefficientsCalculator));
//            }
//            executorService.shutdown();
//            executorService.awaitTermination(999999999999L, TimeUnit.DAYS);// Wait until job is done.
//            for (Future<List<Word>> wordListListFuture : calculatedWordListListFutures) {
//                calculatedWordListList.add(wordListListFuture.get());
//            }
//
//            final int size = calculatedWordListList.get(0).size();
//            final int ratingThreshold = size / BUFFED_RATIO;
//            Set<CalculatedWord> calculatedWords = new HashSet<>();
//
//            for (List<Word> wordList : calculatedWordListList) {
//                for (int i = 0; i < wordList.size(); i++) {
//                    Word w = wordList.get(i);
//                    long rating = wordList.size() - i; // if word is at start of the list, gets highest rating.
//                    if (rating < ratingThreshold) //if rating is lower than the most important quarter, it is ignored for this turn
//                        rating = 0;
//                    rating = rating * rating * rating * rating;
//                    CalculatedWord calculatedWord = new CalculatedWord(w.getWordText(), rating);
//                    if (calculatedWords.contains(calculatedWord)) {
//                        for (CalculatedWord cWord : calculatedWords) {
//                            if (cWord.equals(calculatedWord)) {
//                                cWord.setRating(cWord.getRating() + rating);
//                                break;
//                            }
//                        }
//                    } else {
//                        calculatedWords.add(calculatedWord);
//                    }
//                }
//            }
//            List<CalculatedWord> sortedCalculatedWords = new ArrayList<>();
//            for (CalculatedWord calculatedWord : calculatedWords) {
//                sortedCalculatedWords.add(calculatedWord);
//            }
//            Collections.sort(sortedCalculatedWords);
//
//
//            System.out.println("*********** TOP 50 words for politician:" + politician.getPoliticianTurkishName() + "*******************");
//            List<Word> mostAccurateWordList = findMostAccurateWordList(sortedCalculatedWords, calculatedWordListList);
//            for (int i = 0; i < 50; i++) {
//                System.out.println(mostAccurateWordList.get(i).getWordText());
//            }
            List<Word> mostAccurateWordList = new CalculateWordCoefficients(politician).call();
            System.out.println("*********** TOP 50 words for politician:" + politician.getPoliticianTurkishName() + "*******************");
            for (int i = 0; i < 50; i++) {
                System.out.println(mostAccurateWordList.get(i).getWordText());
            }
            System.out.println("*** Saving most accurate word coefficients for future predictions... ***");
            politician.setWords(mostAccurateWordList);
            politicanDao.savePolitician(politician);

        }
    }

    private static List<Word> findMostAccurateWordList(List<CalculatedWord> sortedCalculatedWords, List<List<Word>> calculatedWordListList) {
        int precisionDepth = 0;
        while (calculatedWordListList.size() != 1) {
            Iterator<List<Word>> wordListIterator = calculatedWordListList.iterator();
            while (wordListIterator.hasNext()) {
                List<Word> nextList = wordListIterator.next();
                if (!sortedCalculatedWords.get(precisionDepth).getText().equals(nextList.get(precisionDepth).getWordText())) {
                    if (calculatedWordListList.size() == 1) {
                        System.out.println("None of the randomized coefficient succeed precision depth:" + precisionDepth + ", moving along with the most precise one.");
                        return calculatedWordListList.get(0);
                    } else {
                        wordListIterator.remove();
                    }
                } else {
                    System.out.println("Randomized coefficient succeed precision depth:" + precisionDepth);
                }
            }
            precisionDepth++;
        }
        return calculatedWordListList.get(0);
    }


    private static void initTweetsIfNotInitiated() throws HttpException, IOException, InterruptedException, ParseException, URISyntaxException {
        for (Politician politician : allPoliticians) {
            List<FacebookTrendInterval> trendIntervals = politician.getTrendIntervals();
            for (FacebookTrendInterval trendInterval : trendIntervals) {
                if (trendInterval.getTweets() == null || trendInterval.getTweets().isEmpty()) {
                    TwitterConnector.fillTweets(politician);
                    break;
                }
            }
        }
    }

    private static void initTrendsIfNotInitiated() throws ParseException {
        for (Politician politician : allPoliticians) {
            if (politician.getTrendIntervals() == null || politician.getTrendIntervals().isEmpty()) {
                initTrendInterval(politician);
            }
        }

    }

    private static void initTrendInterval(Politician politician) throws ParseException {

        String politicanTwitterName = politician.getPoliticianTwitterAccountName();
        switch (politicanTwitterName) {
            case KK_TWITTER_NAME:
                initKilicdarogluTrends(politician);
                break;
            default:
                System.out.println("Unrecognices politician for trends:" + politician);
        }
    }

    private static void initKilicdarogluTrends(Politician politician) throws ParseException {
        /*
        * Source = https://www.socialbakers.com/statistics/facebook/pages/detail/76599755300
         */
        if (politician.getTrendIntervals() == null || politician.getTrendIntervals().isEmpty()) {
            List<FacebookTrendInterval> trendIntervals = new ArrayList<>();
            addInterval(trendIntervals, "03.12.16", "17.12.16", BigDecimal.valueOf(-75000));
            addInterval(trendIntervals, "17.12.16", "31.12.16", BigDecimal.valueOf(-85000));
            addInterval(trendIntervals, "31.12.16", "14.01.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "14.01.17", "28.01.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "28.01.17", "11.02.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "11.02.17", "25.02.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "25.02.17", "11.03.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "11.03.17", "25.03.17", BigDecimal.valueOf(-70000));
            addInterval(trendIntervals, "25.03.17", "08.04.17", BigDecimal.valueOf(10000));
            addInterval(trendIntervals, "08.04.17", "22.04.17", BigDecimal.valueOf(80000));


            politician.setTrendIntervals(trendIntervals);
            politicanDao.savePolitician(politician);
        }


    }

    private static void addInterval(List<FacebookTrendInterval> trendIntervals, String startDate, String endDate, BigDecimal change) throws ParseException {
        FacebookTrendInterval fti = new FacebookTrendInterval();
        fti.setTrendId(UUID.randomUUID().toString());
        fti.setStart(sdf.parse(startDate));
        fti.setEnd(sdf.parse(endDate));
        fti.setPopulationChange(change);
        trendIntervals.add(fti);
    }


    private static void createPoliticansIfNotExists() {

        allPoliticians = politicanDao.getAllPoliticians();


        /**
         * Kemal Kilicdaroglu
         * twitter = kilicdarogluk
         */
        Politician kkilic = new Politician();
        kkilic.setPoliticianTurkishName("Kemal Kılıçdaroğlu");
        kkilic.setPoliticianId(KK_TWITTER_NAME); // twitter account name is unique enough
        kkilic.setPoliticianTwitterAccountName(KK_TWITTER_NAME);

//        Politician rte = new Politician();
//        rte.setPoliticianTurkishName("Recep Tayyip Erdoğan");
//        rte.setPoliticianId(RTE_TWITTER_NAME); // twitter account name is unique enough
//        rte.setPoliticianTwitterAccountName(RTE_TWITTER_NAME);

        if (!allPoliticians.contains(kkilic)) {
            allPoliticians.add(kkilic);
            politicanDao.savePolitician(kkilic);
        }

//        if (!allPoliticians.contains(rte)) {
//            allPoliticians.add(rte);
//            politicanDao.savePolitician(rte);
//        }


    }
}
