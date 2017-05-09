package com.gokhanozg.ptnla;

/**
 * Created by mephala on 5/9/17.
 */
public class CalculatedWord implements Comparable<CalculatedWord> {

    private String text;
    private Long rating;

    public CalculatedWord(String text, Long rating) {
        this.text = text;
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculatedWord that = (CalculatedWord) o;

        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return "CalculatedWord{" +
                "text='" + text + '\'' +
                ", rating=" + rating +
                '}';
    }

    @Override
    public int compareTo(CalculatedWord o) {
        return o.getRating().compareTo(getRating());
    }
}
