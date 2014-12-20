package net.moderngalaxy;

import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.moderngalaxy.downloads.Media;
import net.moderngalaxy.galleries.Gallery;
import net.moderngalaxy.galleries.TagGallery;

public class GalleryMirror {

	private final static ScheduledExecutorService EXEC = Executors.newScheduledThreadPool(1);

	private Config config;

	public static void main(String[] args) {
		GalleryMirror gd = new GalleryMirror();
		gd.start();
	}

	public GalleryMirror() {
		config = new Config(Paths.get(""));
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
