package com.gokhanozg.ptnla;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mephala on 5/12/17.
 */
public class TweetPopularity {

    private String tweetId;
    private Date tweetDate;
    private String mdy;
    private BigDecimal weight; //based on fave count
    private String tweetText;
    private BigDecimal popularityGain;

    public TweetPopularity(Date d, String tweetId, String tweetText, Long faveCount) {
        this.tweetId = tweetId;
        this.tweetText = tweetText;
        this.weight = BigDecimal.valueOf(faveCount);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int m = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int y = c.get(Calendar.YEAR);
        this.mdy = Integer.valueOf(m).toString() + Integer.valueOf(day).toString() + Integer.valueOf(y).toString();
    }


    @Override
    public String toString() {
        return "TweetPopularity{" +
                "mdy=" + mdy +
                ", popularityGain=" + popularityGain +
                '}';
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public Date getTweetDate() {
        return tweetDate;
    }

    public void setTweetDate(Date tweetDate) {
        this.tweetDate = tweetDate;
    }

    public String getMdy() {
        return mdy;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public BigDecimal getPopularityGain() {
        return popularityGain;
    }

    public void setPopularityGain(BigDecimal popularityGain) {
        this.popularityGain = popularityGain;
    }
}
