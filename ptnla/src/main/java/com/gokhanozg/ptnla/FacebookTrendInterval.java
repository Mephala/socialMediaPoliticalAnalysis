package com.gokhanozg.ptnla;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mephala on 4/24/17.
 */
@Entity(name = "FACEBOOK_TREND")
public class FacebookTrendInterval {


    @Id
    @Column(name = "TREND_ID")
    private String trendId;
    @Column(name = "START_DATE", nullable = false)
    private Date start;
    @Column(name = "END_DATE", nullable = false)
    private Date end;
    @Column(name = "POPULATION_CHANGE")
    private BigDecimal populationChange;

    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TweetObject> tweets;

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public BigDecimal getPopulationChange() {
        return populationChange;
    }

    public void setPopulationChange(BigDecimal weeklyChange) {
        this.populationChange = weeklyChange;
    }

    public List<TweetObject> getTweets() {
        return tweets;
    }

    public void setTweets(List<TweetObject> tweets) {
        this.tweets = tweets;
    }

    @Override
    public String toString() {
        return "FacebookTrendInterval{" +
                "trendId='" + trendId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
