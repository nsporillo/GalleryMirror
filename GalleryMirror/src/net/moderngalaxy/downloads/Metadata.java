package net.moderngalaxy.downloads;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Metadata {

	private Path mediaPath;
	private Path metaFile;
	private Path metaDir;
	private String id;
	private boolean animated;
	private long date;
	private int score;

	public Metadata(Path dir, String id) {
		this.metaDir = dir;
		this.id = id;
		this.metaFile = dir.resolve(id + ".json");
		try {
			if (Files.exists(metaFile)) {
				JSONParser parser = new JSONParser();
				StringBuilder sb = new StringBuilder();
				InputStream in = newInputStream(dir.resolve("config.json"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				
				while ((line = reader.readLine()) != null)
					sb.append(line);
				
				JSONObject obj = (JSONObject) parser.parse(sb.toString());
				this.id = (String) obj.get("id");
				this.date = (Long) obj.get("date");
				this.score = (Integer) obj.get("score");
				this.animated = (Boolean) obj.get("animated");
				this.mediaPath = Paths.get((URI) obj.get("path"));
			} else {
				throw new RuntimeException("Cannot call MediaMeta by ID if it does not exist");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Metadata(Path metaDir, Path mediaDir, Media m) {
		this.metaDir = metaDir;
		this.id = truncateId(m.getLink());
		this.date = m.getDate();
		if (m instanceof Video)
			this.animated = true;
		else
			this.animated = false;
		this.score = m.getScore();
		this.metaFile = metaDir.resolve(id + ".json");
		this.mediaPath = mediaDir.resolve(truncateFile(m.getLink()));
		if (!exists(metaFile)) {
			this.create();
		}
	}

	public static boolean doesExist(Path metaDir, String id) {
		return exists(metaDir.resolve(id + ".json"));
	}

	public static String truncateId(String link) {
		return link.substring(link.lastIndexOf("/") + 1, link.length() - 4);
	}

	public static String truncateFile(String link) {
		return link.substring(link.lastIndexOf("/") + 1, link.length());
	}

	@SuppressWarnings("unchecked")
	public void create() {
		try {
			JSONObject jobj = new JSONObject();
			jobj.put("id", this.id);
			jobj.put("date", this.date);
			jobj.put("score", this.score);
			jobj.put("animated", this.animated);
			jobj.put("path", this.mediaPath.toAbsolutePath().normalize().toString());
			Path metadata = metaDir.resolve(this.id + ".json");
			byte data[] = jobj.toJSONString().getBytes();
			OutputStream out = new BufferedOutputStream(newOutputStream(metadata, CREATE, APPEND));
			out.write(data, 0, data.length);
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Path getMediaPath() {
		return mediaPath;
	}

	public Path getMetaFile() {
		return metaFile;
	}

	public Path getMetaDir() {
		return metaDir;
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
}
