package net.porillo;

import com.google.gson.Gson;
import net.porillo.downloads.Media;
import net.porillo.galleries.Gallery;
import net.porillo.galleries.TagGallery;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GalleryMirror {

    private final static ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(1);
    private final static Gson gson = new Gson();
    private Config config;

    public GalleryMirror() {
        config = new Config(Paths.get(""));
    }

    public static void main(String[] args) {
        GalleryMirror gd = new GalleryMirror();
        gd.start();
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        TagGallery tg = new TagGallery(config.getClientId(), config.getGallery());
        this.schedule(tg, config.getDelay());
    }

    private void schedule(final TagGallery gallery, Integer delay) {
        EXEC.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                gallery.connect();
                List<Media> media = gallery.getDownloadQueue(Gallery.mediaQueue);
                gallery.updateDatabase(media);
            }
        }, 0, delay, TimeUnit.MINUTES);
    }
}
