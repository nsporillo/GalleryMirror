package net.porillo.downloads;

public class Media {

    private String link;
    private int score;
    private long date;

    public Media(String link, long date, int score) {
        this.link = link;
        this.score = score;
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public int getScore() {
        return score;
    }

    public long getDate() {
        return date;
    }
}
