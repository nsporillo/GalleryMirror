package net.porillo.galleries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.porillo.FileIO;
import net.porillo.downloads.Image;
import net.porillo.downloads.Media;
import net.porillo.downloads.Metadata;
import net.porillo.downloads.Video;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Gallery {

    // https://api.imgur.com/3/gallery/t/{t_name}/{sort}/{page}
    public final static String endpoint = "https://api.imgur.com/3/";
    public final static Path workingDir = Paths.get("");
    public final static JsonParser parser = new JsonParser();
    public final static List<Media> mediaQueue = new ArrayList<Media>();

    public static String CID;

    public Gallery(String clientId) {
        Gallery.CID = clientId;
    }

    public void connect() {
    }

    public JsonObject handleResponse(InputStream in) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext())
            sb.append(scanner.next());
        scanner.close();

        JsonObject obj = null;
        try {
            obj = parser.parse(sb.toString()).getAsJsonObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public List<JsonObject> gatherItems(JsonObject json) {
        List<JsonObject> items = new ArrayList<JsonObject>();
        JsonObject data = json.get("data").getAsJsonObject();
        JsonArray jItems = data.get("items").getAsJsonArray();
        for (int i = 0; i < jItems.size(); i++) {
            items.add(jItems.get(i).getAsJsonObject());
        }
        return items;
    }

    public void assessItems(List<JsonObject> items) {
        for (JsonObject obj : items) {
            boolean isAlbum = obj.get("is_album").getAsBoolean();
            String link = obj.get("link").getAsString();
            int score = (int) obj.get("score").getAsLong();
            if (isAlbum) {
                String id = obj.get("id").getAsString();
                AlbumGallery album = new AlbumGallery(CID, id, score);
                album.connect();
            } else {
                boolean isAnimated = obj.get("animated").getAsBoolean();
                long date = obj.get("datetime").getAsLong();
                if (isAnimated)
                    mediaQueue.add(new Video(link, date, score));
                else
                    mediaQueue.add(new Image(link, date, score));
            }
        }
    }

    public List<Media> getDownloadQueue(List<Media> mediaQueue) {
        List<Media> toDownload = new ArrayList<Media>();
        Path metaData = workingDir.resolve("metadata");
        if (!metaData.toFile().exists())
            metaData.toFile().mkdirs();

        for (Media m : mediaQueue)
            if (!FileIO.exists(m)) {
                toDownload.add(m);
            }
        return toDownload;
    }

    public void updateDatabase(List<Media> downloadQueue) {
        Path media = workingDir.resolve("media");
        if (!media.toFile().exists()) {
            media.toFile().mkdirs();
        }
        System.out.println("Downloading " + downloadQueue.size() + " medium");
        for (Media m : downloadQueue) {
            new Metadata(m).load();
            try {
                FileIO.download(m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleError(HttpURLConnection conn) {
        try {
            System.out.println("ResponseCode: " + conn.getResponseCode());
            InputStream errorStream = conn.getErrorStream();
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(errorStream);
            while (scanner.hasNext())
                sb.append(scanner.next());
            System.out.println("Error: " + sb.toString());
            scanner.close();
            errorStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
