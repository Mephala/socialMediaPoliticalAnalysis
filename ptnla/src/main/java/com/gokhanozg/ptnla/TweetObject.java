package com.gokhanozg.ptnla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by mephala on 4/24/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "created_at",
        "id",
        "id_str",
        "text",
        "truncated",
        "retweet_count",
        "favorite_Count"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "TWEET")
public class TweetObject {

    @JsonProperty("created_at")
    @Column
    private String createdAt;

    @JsonProperty("id")
    @Id
    @Column(name = "TWEET_ID")
    private String id;

    @JsonProperty("id_str")
    @Column
    private String idStr;

    @JsonProperty("text")
    @Column
    private String text;

    @JsonProperty("truncated")
    @Column(name = "TRUNCATED")
    private Boolean truncated;


    @JsonProperty("retweet_count")
    @Column
    private Long retweetCount;


    @JsonProperty("favorite_count")
    @Column
    private Long favoriteCount;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public Long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
