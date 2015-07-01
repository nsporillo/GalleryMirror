package net.porillo.downloads;

import net.porillo.FileIO;

import java.io.IOException;

public class Metadata {
    private String id;
    private boolean animated;
    private long date;
    private int score;

    public Metadata(Media m) {
        this.id = FileIO.getId(m.getLink());
        this.date = m.getDate();
        this.animated = m instanceof Video;
        this.score = m.getScore();
    }

    public void load() {
        if (!FileIO.exists(this)) {
            try {
                FileIO.write(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Metadata [");
        sb.append(", id='").append(id).append('\'');
        sb.append(", animated=").append(animated);
        sb.append(", date=").append(date);
        sb.append(", score=").append(score);
        sb.append(']');
        return sb.toString();
    }
}
