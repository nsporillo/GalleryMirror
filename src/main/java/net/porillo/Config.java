package net.porillo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;

public class Config {

    private String gallery;
    private String clientId;
    private Integer delay;

    @SuppressWarnings("unchecked")
    public Config(Path dir) {
        StringBuilder sb = new StringBuilder();
        Path confile = dir.resolve("config.json");
        try {
            if (!exists(confile)) {
                this.gallery = "reaction_gifs";
                this.delay = 30;
                this.clientId = "null";
                FileIO.write(confile, GalleryMirror.getGson().toJson(this));
            } else {
                InputStream in = newInputStream(dir.resolve("config.json"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println(sb.toString());
                JsonElement jsonElement = new JsonParser().parse(sb.toString());
                if (jsonElement.isJsonObject()) {
                    JsonObject obj = jsonElement.getAsJsonObject();
                    this.setGallery(obj.get("gallery").getAsString());
                    this.setDelay(obj.get("delay").getAsInt());
                    this.setClientId(obj.get("clientId").getAsString());
                }
            }
        } catch (Exception ex) {
        }
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public Integer getDelay() {
        if (delay == null) {
            return 30;
        }
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getClientId() {
        return clientId;
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String toString() {
        return "Config [gallery=" + gallery + ", delay= " + delay + ", clientId=" + clientId + "]";
    }
}
