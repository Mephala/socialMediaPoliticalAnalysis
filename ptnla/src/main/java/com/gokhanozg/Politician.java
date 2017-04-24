package com.gokhanozg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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
}
