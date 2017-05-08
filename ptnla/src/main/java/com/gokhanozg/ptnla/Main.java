package com.gokhanozg.ptnla;

import com.gokhanozg.ptnla.api.TwitterConnector;
import com.gokhanozg.ptnla.dao.PoliticanDao;
import org.apache.http.HttpException;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mephala on 4/24/17.
 */
public class Main {

    private static final String KK_TWITTER_NAME = "kilicdarogluk";
    private static PoliticanDao politicanDao = new PoliticanDao();
    private static List<Politician> allPoliticians;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");


    public static void main(String[] args) {
        try {
//            createPoliticansIfNotExists();
//            initTrendsIfNotInitiated();
//            initTweetsIfNotInitiated();
            createRegressionCoefficients();
        } catch (Throwable t) {
            System.err.println("Program terminated due to Fatal error.");
            t.printStackTrace();
        }


    }

    private static void createRegressionCoefficients() {
        if (allPoliticians == null || allPoliticians.isEmpty()) {
            allPoliticians = politicanDao.getAllPoliticians();
        }
        List<String> tweetStrings = new ArrayList<>();
        for (Politician politician : allPoliticians) {
            List<FacebookTrendInterval> trendIntervals = politician.getTrendIntervals();


            List<Word> wordList = createTweetWordsList(tweetStrings, trendIntervals);
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
            System.out.println(A);
            System.out.println("*********************************************************************");
            SimpleMatrix w = A.transpose().mult(A).invert().mult(A.transpose().mult(y));
            System.out.println(w);
            for (int i = 0; i < wordList.size(); i++) {
                Word word = wordList.get(i);
                word.setCoefficient(w.get(i, 0));
            }
            Collections.sort(wordList, new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o2.getCoefficient().compareTo(o1.getCoefficient());
                }
            });

            System.out.println("******************************************************");

            System.out.println("Top 10 rewarding words in tweets:");
            for (int i = 0; i < 10; i++) {
                System.out.println(wordList.get(i));
            }
        }

    }

    private static List<Word> createTweetWordsList(List<String> tweetStrings, List<FacebookTrendInterval> trendIntervals) {
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

    private static void tokenizeWordsAndAddToSet(Set<String> words, String tweetString) {
        StringTokenizer tokenizer = new StringTokenizer(tweetString, " ");
        while (tokenizer.hasMoreTokens()) {
            words.add(tokenizer.nextToken());
        }
    }

    private static double[] getPersistentValues(List<Word> wordList, FacebookTrendInterval facebookTrendInterval) {
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

        if (!allPoliticians.contains(kkilic)) {
            allPoliticians.add(kkilic);
            politicanDao.savePolitician(kkilic);
        }


    }
}
