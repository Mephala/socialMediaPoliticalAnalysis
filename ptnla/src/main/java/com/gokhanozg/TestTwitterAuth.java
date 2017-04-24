package com.gokhanozg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by gokhanozg on 4/22/17.
 */
public class TestTwitterAuth {

    public static void main(String[] args) throws URISyntaxException, IOException, HttpException {
        String concatenatedKeySecret = "M50FgPfYYmPBRyZi4mq7xohve:3NO10fQBYUXTNFBiNegpE8flXK95bBX1hOXo3SeqlWaFpfVyB5";
        byte[] encodedBytes = Base64.encodeBase64(concatenatedKeySecret.getBytes());
        String encodedKeySecret = new String(encodedBytes);
        System.out.println(encodedKeySecret);
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = new DefaultHttpClient();
//        HttpPost request = new HttpPost("https://api.twitter.com/oauth2/token");
//        request.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//        request.addHeader("Authorization", "Basic " + encodedKeySecret);
//        StringEntity body = new StringEntity("grant_type=client_credentials","UTF-8");
//        request.setEntity(body);
//        HttpResponse response = httpClient.execute(request);
//        String responseBody = EntityUtils.toString(response.getEntity());
//        System.out.println(responseBody);

//        TwitterAuthorizationResponse twitterAuthorizationResponse = objectMapper.readValue(responseBody,TwitterAuthorizationResponse.class);
//        System.out.println(twitterAuthorizationResponse);
        HttpGet getTweetsRequest = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=kilicdarogluk&max_id=844580920074100700");
        getTweetsRequest.addHeader("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAANoW0QAAAAAAaBvJr1w3UIjHqYDMu4OY62J0NDw%3DYY0pp4g2YQtEJpdp3yvpaQ1d7bcPz9Xgqc3IDM8X28ILA9Nle0");
        HttpResponse response = httpClient.execute(getTweetsRequest);
        String tweets = EntityUtils.toString(response.getEntity());
        System.out.println(tweets);
        TweetObject[] tweetObjects = objectMapper.readValue(tweets, TweetObject[].class);
        System.out.println(tweetObjects);
    }
}
