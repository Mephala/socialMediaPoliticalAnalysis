package com.gokhanozg.ptnla;

import com.gokhanozg.ptnla.api.TwitterConnector;
import com.gokhanozg.ptnla.dao.PoliticanDao;
import org.apache.http.HttpException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by mephala on 4/24/17.
 */
public class Main {

    private static final String KK_TWITTER_NAME = "kilicdarogluk";
    private static final int THREAD = 8;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
    private static final int EPOCH = 50;
    private static PoliticanDao politicanDao = new PoliticanDao();
    private static List<Politician> allPoliticians;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

    public static void main(String[] args) {
        try {
            createPoliticansIfNotExists();
            initTrendsIfNotInitiated();
            initTweetsIfNotInitiated();
            createRegressionCoefficients();
        } catch (Throwable t) {
            System.err.println("Program terminated due to Fatal error.");
            t.printStackTrace();
        }


    }

    private static void createRegressionCoefficients() throws InterruptedException, ExecutionException {
        if (allPoliticians == null || allPoliticians.isEmpty()) {
            allPoliticians = politicanDao.getAllPoliticians();
        }
        for (Politician politician : allPoliticians) {
            List<FacebookTrendInterval> trendIntervals = politician.getTrendIntervals();
            List<Future<List<Word>>> calculatedWordListListFutures = new ArrayList<>();
            List<List<Word>> calculatedWordListList = new ArrayList<>();
            for (int i = 0; i < EPOCH; i++) {
                CalculateWordCoefficients wordCoefficientsCalculator = new CalculateWordCoefficients(trendIntervals);
                calculatedWordListListFutures.add(executorService.submit(wordCoefficientsCalculator));
            }
            executorService.shutdown();
            executorService.awaitTermination(999999999999L, TimeUnit.DAYS);// Wait until job is done.
            for (Future<List<Word>> wordListListFuture : calculatedWordListListFutures) {
                calculatedWordListList.add(wordListListFuture.get());
            }

            System.out.println("******************************************************");
            System.out.println("Top 10 rewarding words in tweets:");
            for (int i = 0; i < 10; i++) {
                System.out.println(calculatedWordListList.get(i).get(0));
            }
        }

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
