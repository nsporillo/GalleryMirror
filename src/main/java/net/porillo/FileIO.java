package net.porillo;


import net.porillo.downloads.Media;
import net.porillo.downloads.Metadata;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileIO {

    private static Path root = Paths.get("");

    public static boolean exists(Media media) {
        return Files.exists(root.resolve("media").resolve(getId(media.getLink())));
    }

    public static boolean exists(Metadata meta) {
        return Files.exists(root.resolve("metadata").resolve(getId(meta.getId()) + ".json"));
    }

    public static String getId(String link) {
        return link.substring(link.lastIndexOf("/") + 1, link.length() - 4);
    }

    public static String getFilePath(String link) {
        return link.substring(link.lastIndexOf("/") + 1, link.length());
    }

    public static void write(Metadata metadata) throws IOException {
        String write = GalleryMirror.getGson().toJson(metadata);
        write(root.resolve("metadata").resolve(metadata.getId() + ".json"), write);
    }

    public static void write(Path path, String json) throws IOException {
        byte data[] = json.getBytes();
        OutputStream out = new BufferedOutputStream(newOutputStream(path, CREATE, APPEND));
        out.write(data, 0, data.length);
        out.flush();
        out.close();
    }


    public static boolean download(Media media) throws IOException {
        URL file = new URL(media.getLink());
        Files.copy(file.openStream(), root.resolve("media").resolve(getId(media.getLink())), REPLACE_EXISTING);
        return Files.exists(root.resolve("media").resolve(getFilePath(media.getLink())));
    }
}
