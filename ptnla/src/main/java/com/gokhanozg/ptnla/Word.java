package com.gokhanozg.ptnla;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by gokhanozg on 5/8/17.
 */
@Entity(name = "WORD")
public class Word implements Comparable<Word> {

    @Column(name = "WORD_TEXT")
    private String wordText;

    @Id
    private String id;

    @Column(name = "COEFFICIENT")
    private Double coefficient;


    @Column(name = "PERSISTENT_VALUE")
    private Double persistentValue;

    @Override
    public String toString() {
        return "Word{" +
                "wordText='" + wordText + '\'' +
                ", coefficient=" + coefficient +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return wordText.equals(word.wordText);
    }

    public String getWordText() {
        return wordText;
    }

    public void setWordText(String wordText) {
        this.wordText = wordText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    @Override
    public int hashCode() {
        return wordText.hashCode();
    }

    @Override
    public int compareTo(Word o) {
        return this.wordText.compareTo(o.getWordText());
    }

    public Double getPersistentValue() {
        return persistentValue;
    }

    public void setPersistentValue(Double persistentValue) {
        this.persistentValue = persistentValue;
    }
}
