package net.moderngalaxy;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static java.nio.file.Files.*;

import java.nio.file.Path;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Config {

	private String gallery;
	private String clientId;
	private Integer delay;

	@SuppressWarnings("unchecked")
	public Config(Path dir) {
		JSONParser parser = new JSONParser();
		StringBuilder sb = new StringBuilder();
		Path confile = dir.resolve("config.json");
		try {
			if (!exists(confile)) {
				JSONObject jobj = new JSONObject();
				jobj.put("gallery", "reaction_gifs");
				jobj.put("clientId", "<your_client_id>");
				jobj.put("delay", 30);

				byte data[] = jobj.toJSONString().getBytes();
				OutputStream out = new BufferedOutputStream(
						newOutputStream(confile, CREATE, APPEND));
				out.write(data, 0, data.length);
				out.flush();
				out.close();

				this.setGallery("reaction_gifs");
				this.setDelay(30);
				this.setClientId("null");
			} else {
				InputStream in = newInputStream(dir.resolve("config.json"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				System.out.println(sb.toString());

				JSONObject obj = (JSONObject) parser.parse(sb.toString());
				this.setGallery((String) obj.get("gallery"));
				this.setClientId((String)obj.get("clientId"));
				this.setDelay((Integer) obj.get("delay"));
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
		if(delay == null) {
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
}
