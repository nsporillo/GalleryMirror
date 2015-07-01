package net.porillo.galleries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.porillo.downloads.Image;
import net.porillo.downloads.Video;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumGallery extends Gallery {

    // https://api.imgur.com/3/album/{id}
    private final static String surl = endpoint + "album/{id}";
    private String id;
    private int score;

    public AlbumGallery(String cid, String id, int score) {
        super(cid);
        this.id = id;
        this.score = score;
    }

    @Override
    public void connect() {
        HttpURLConnection conn;
        try {
            URL url = new URL(surl.replace("{id}", id));
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Client-ID " + CID);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<JsonObject> items = this.gatherItems(handleResponse(conn.getInputStream()));
                System.out.println(url.toString() + " -> " + conn.getResponseCode() + " -> " + items.size() + " medium");
                this.assessItems(items);
            } else {
                super.handleError(conn);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<JsonObject> gatherItems(JsonObject json) {
        List<JsonObject> items = new ArrayList<JsonObject>();
        JsonObject data = json.get("data").getAsJsonObject();
        JsonArray images = data.get("images").getAsJsonArray();
        for (int i = 0; i < images.size(); i++)
            items.add(images.get(i).getAsJsonObject());
        return items;
    }

    @Override
    public void assessItems(List<JsonObject> items) {
        for (JsonObject obj : items) {
            String link = obj.get("link").getAsString();
            long date = obj.get("datetime").getAsLong();
            boolean isAnimated = obj.get("animated").getAsBoolean();
            if (isAnimated)
                mediaQueue.add(new Video(link, date, this.score));
            else
                mediaQueue.add(new Image(link, date, this.score));
        }
    }
}
