package net.porillo.galleries;

import net.porillo.downloads.Image;
import net.porillo.downloads.Media;
import net.porillo.downloads.Metadata;
import net.porillo.downloads.Video;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static net.porillo.downloads.Metadata.doesExist;
import static net.porillo.downloads.Metadata.truncateId;

public class Gallery {

    // https://api.imgur.com/3/gallery/t/{t_name}/{sort}/{page}
    public final static String endpoint = "https://api.imgur.com/3/";
    public final static Path workingDir = Paths.get("");
    public final static JSONParser parser = new JSONParser();
    public final static List<Media> mediaQueue = new ArrayList<Media>();

    public static String CID;

    public Gallery(String clientId) {
        Gallery.CID = clientId;
    }

    public void connect() {}

    public JSONObject handleResponse(InputStream in) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext())
            sb.append(scanner.next());
        scanner.close();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(sb.toString());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public List<JSONObject> gatherItems(JSONObject json) {
        List<JSONObject> items = new ArrayList<JSONObject>();
        JSONObject data = (JSONObject) json.get("data");
        JSONArray jitems = (JSONArray) data.get("items");
        for (int i = 0; i < jitems.size(); i++)
            items.add((JSONObject) jitems.get(i));
        return items;
    }

    public void assessItems(List<JSONObject> items) {
        for (JSONObject obj : items) {
            boolean isAlbum = (Boolean) obj.get("is_album");
            String link = (String) obj.get("link");
            int score = ((Long) obj.get("score")).intValue();
            if (isAlbum) {
                String id = (String) obj.get("id");
                AlbumGallery album = new AlbumGallery(CID, id, score);
                album.connect();
            } else {
                boolean isAnimated = (Boolean) obj.get("animated");
                long date = (Long) obj.get("datetime");
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
            if (!doesExist(metaData, truncateId(m.getLink())))
                toDownload.add(m);

        return toDownload;
    }

    public void updateDatabase(List<Media> downloadQueue) {
        Path metaData = workingDir.resolve("metadata");
        Path media = workingDir.resolve("media");
        if (!media.toFile().exists()) {
            media.toFile().mkdirs();
        }
        System.out.println("Downloading " + downloadQueue.size() + " medium");
        for (Media m : downloadQueue) {
            Metadata mm = new Metadata(metaData, media, m);
            Path mediaPath = mm.getMediaPath();
            try {
                Files.copy(new URL(m.getLink()).openStream(), mediaPath, REPLACE_EXISTING);
            } catch (Exception e) {
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
