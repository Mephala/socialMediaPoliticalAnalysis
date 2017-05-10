package com.gokhanozg.ptnla.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gokhanozg.ptnla.FacebookTrendInterval;
import com.gokhanozg.ptnla.Politician;
import com.gokhanozg.ptnla.TweetObject;
import com.gokhanozg.ptnla.dao.PoliticanDao;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gokhanozg on 5/7/17.
 */
public class TwitterConnector {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
    //Tue Mar 21 07:42:47 +0000 2017


    public static void fillTweets(Politician p) throws URISyntaxException, IOException, HttpException, ParseException, InterruptedException {
        List<FacebookTrendInterval> trendIntervalList = p.getTrendIntervals();
        //sorting based on start date.
        Collections.sort(trendIntervalList, new Comparator<FacebookTrendInterval>() {
            @Override
            public int compare(FacebookTrendInterval o1, FacebookTrendInterval o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });
        FacebookTrendInterval earliestTrend = trendIntervalList.get(0);
        Date earliestNeededTweetDate = earliestTrend.getStart();

        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = new DefaultHttpClient();
        //&max_id=844580920074100700
        HttpGet getTweetsRequest = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=kilicdarogluk");
        getTweetsRequest.addHeader("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAANoW0QAAAAAAaBvJr1w3UIjHqYDMu4OY62J0NDw%3DYY0pp4g2YQtEJpdp3yvpaQ1d7bcPz9Xgqc3IDM8X28ILA9Nle0");
        HttpResponse response = httpClient.execute(getTweetsRequest);
        String tweets = EntityUtils.toString(response.getEntity());
        TweetObject[] tweetObjects = objectMapper.readValue(tweets, TweetObject[].class);
        while (SDF.parse(tweetObjects[tweetObjects.length - 1].getCreatedAt()).after(earliestNeededTweetDate)) {
            if (tweetObjects.length > 0) {
                Set<String> idSet = new HashSet<>();
                for (TweetObject tweetObject : tweetObjects) {
                    String id = tweetObject.getId();
                    if (idSet.contains(id)) {
                        System.out.println("Twitter API returned same tweet two times..Why ? id:" + id);
                    }
                    idSet.add(id);
                }
                asignTweetsToIntervals(trendIntervalList, tweetObjects);
                String maxId = tweetObjects[tweetObjects.length - 1].getId();
                getTweetsRequest = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=kilicdarogluk&max_id=" + maxId);
                getTweetsRequest.addHeader("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAANoW0QAAAAAAaBvJr1w3UIjHqYDMu4OY62J0NDw%3DYY0pp4g2YQtEJpdp3yvpaQ1d7bcPz9Xgqc3IDM8X28ILA9Nle0");
                System.out.println("Waiting (15 sec) to limit twitter api requests. Fetching for politician:" + p + ", latest tweet date:" + SDF.parse(tweetObjects[tweetObjects.length - 1].getCreatedAt()));
                //Timeout to limit api requests.
                Thread.sleep(15000L);
                response = httpClient.execute(getTweetsRequest);
                tweets = EntityUtils.toString(response.getEntity());
                tweetObjects = objectMapper.readValue(tweets, TweetObject[].class);
                if (tweetObjects == null || tweetObjects.length == 0)
                    break;
            } else {
                System.out.println("No tweets for earliest trend interval for politician:" + p + ", earliestDate:" + earliestNeededTweetDate);
            }
        }
        Set<String> idSet = new HashSet<>();
        for (FacebookTrendInterval facebookTrendInterval : trendIntervalList) {
            List<TweetObject> tweetObjectList = facebookTrendInterval.getTweets();
            if (tweetObjectList != null) {
                for (TweetObject tweetObject : tweetObjectList) {
                    String id = tweetObject.getId();
                    if (idSet.contains(id)) {
                        System.out.println("Duplicate id in trend:" + facebookTrendInterval + ", duplicate tweet:" + tweetObject + "id:" + id);
                    } else {
                        idSet.add(id);
                    }
                }
            }

        }
        new PoliticanDao().savePolitician(p);
    }

    private static void asignTweetsToIntervals(List<FacebookTrendInterval> trendIntervalList, TweetObject[] tweetObjects) throws ParseException {
        for (FacebookTrendInterval facebookTrendInterval : trendIntervalList) {
            for (TweetObject tweet : tweetObjects) {
                Set<String> idSet = new HashSet<>();
                for (FacebookTrendInterval trendInterval : trendIntervalList) {
                    List<TweetObject> tweetObjectList = trendInterval.getTweets();
                    if (tweetObjectList == null || tweetObjectList.isEmpty())
                        continue;
                    for (TweetObject tweetObject : tweetObjectList) {
                        String id = tweetObject.getId();
                        idSet.add(id);
                    }
                }
                if (idSet.contains(tweet.getId())) {
                    System.out.println("Duplicate tweet detected, omitting extra one since it is already been processed before. tweet:" + tweet);
                    continue;
                }
                String tweetStartString = tweet.getCreatedAt();
                Date createdDate = SDF.parse(tweetStartString);
                if (createdDate.after(facebookTrendInterval.getStart()) && createdDate.before(facebookTrendInterval.getEnd())) {
                    List<TweetObject> intervalTweets = facebookTrendInterval.getTweets();
                    if (intervalTweets == null) {
                        intervalTweets = new ArrayList<>();
                        facebookTrendInterval.setTweets(intervalTweets);
                    }
                    intervalTweets.add(tweet);
                }
            }
        }
    }


}
