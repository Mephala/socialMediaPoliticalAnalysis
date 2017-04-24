package com.gokhanozg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mephala on 4/24/17.
 */
@Entity(name = "FACEBOOK_TREND")
public class FacebookTrendInterval {

    @ManyToOne
    Politician politician;
    @Id
    @Column(name = "TREND_ID")
    private String trendId;
    @Column(name = "START_DATE", nullable = false)
    private Date start;
    @Column(name = "END_DATE", nullable = false)
    private Date end;
    @Column(name = "WEEKLY_CHANGE")
    private BigDecimal weeklyChange;

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

    public BigDecimal getWeeklyChange() {
        return weeklyChange;
    }

    public void setWeeklyChange(BigDecimal weeklyChange) {
        this.weeklyChange = weeklyChange;
    }

    public Politician getPolitician() {
        return politician;
    }

    public void setPolitician(Politician politician) {
        this.politician = politician;
    }
}
