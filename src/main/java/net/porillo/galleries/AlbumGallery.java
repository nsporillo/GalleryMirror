package net.porillo.galleries;

import net.porillo.downloads.Image;
import net.porillo.downloads.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumGallery extends Gallery {

    // https://api.imgur.com/3/album/{id}
    private final String surl = endpoint + "album/{id}";
    private String id;
    private int score;

    public AlbumGallery(String cid, String id, int score) {
        super(cid);
        this.id = id;
        this.score = score;
    }

    @Override
    public void connect() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(surl.replace("{id}", id));
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Client-ID " + CID);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<JSONObject> items = super.gatherItems(handleResponse(conn.getInputStream()));
                System.out.println(url.toString() + " -> " + conn.getResponseCode() + " -> "
                        + items.size() + " medium");
                this.assessItems(items);
            } else {
                super.handleError(conn);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<JSONObject> gatherItems(JSONObject json) {
        List<JSONObject> items = new ArrayList<JSONObject>();
        JSONObject data = (JSONObject) json.get("data");
        JSONArray images = (JSONArray) data.get("images");
        for (int i = 0; i < images.size(); i++)
            items.add((JSONObject) images.get(i));
        return items;
    }

    @Override
    public void assessItems(List<JSONObject> items) {
        for (JSONObject obj : items) {
            String link = (String) obj.get("link");
            long date = (Long) obj.get("datetime");
            boolean isAnimated = (Boolean) obj.get("animated");
            if (isAnimated)
                mediaQueue.add(new Video(link, date, this.score));
            else
                mediaQueue.add(new Image(link, date, this.score));
        }
    }
}
