package com.gokhanozg.ptnla;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mephala on 4/24/17.
 */
@Entity(name = "POLITICIAN")
public class Politician {

    @Id
    @Column(name = "POLITICIAN_ID")
    private String politicianId;

    @Column(name = "POLITICIAN_TURKISH_NAME", nullable = false)
    private String politicianTurkishName;

    @Column(name = "POLITICIAN_TWITTER_ACCOUNT_NAME", nullable = false)
    private String politicianTwitterAccountName;


    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FacebookTrendInterval> trendIntervals;

    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Word> words;

    public String getPoliticianId() {
        return politicianId;
    }

    public void setPoliticianId(String politicianId) {
        this.politicianId = politicianId;
    }

    public String getPoliticianTurkishName() {
        return politicianTurkishName;
    }

    public void setPoliticianTurkishName(String politicianTurkishName) {
        this.politicianTurkishName = politicianTurkishName;
    }

    public String getPoliticianTwitterAccountName() {
        return politicianTwitterAccountName;
    }

    public void setPoliticianTwitterAccountName(String politicianTwitterAccountName) {
        this.politicianTwitterAccountName = politicianTwitterAccountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Politician that = (Politician) o;

        return politicianId.equals(that.politicianId);
    }

    @Override
    public int hashCode() {
        return politicianId.hashCode();
    }


    public List<FacebookTrendInterval> getTrendIntervals() {
        return trendIntervals;
    }

    public void setTrendIntervals(List<FacebookTrendInterval> trendIntervals) {
        this.trendIntervals = trendIntervals;
    }

    @Override
    public String toString() {
        return "Politician{" +
                "politicianId='" + politicianId + '\'' +
                ", politicianTurkishName='" + politicianTurkishName + '\'' +
                ", politicianTwitterAccountName='" + politicianTwitterAccountName + '\'' +
                '}';
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
