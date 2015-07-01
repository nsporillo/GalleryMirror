package net.porillo.galleries;

import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TagGallery extends Gallery {

    // https://api.imgur.com/3/gallery/t/{tag}
    private final static String surl = endpoint + "gallery/t/{tag}.json";

    private String tag;

    public TagGallery(String cid, String tag) {
        super(cid);
        this.tag = tag;
    }

    @Override
    public void connect() {
        HttpURLConnection conn;
        try {
            URL url = new URL(surl.replace("{tag}", tag));
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Client-ID " + CID);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                List<JsonObject> items = super.gatherItems(handleResponse(conn.getInputStream()));
                System.out.println(url.toString() + " -> " + conn.getResponseCode() + " -> " + items.size() + " medium");
                super.assessItems(items);
            } else {
                super.handleError(conn);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
