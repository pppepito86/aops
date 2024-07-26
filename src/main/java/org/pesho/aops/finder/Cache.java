package org.pesho.aops.finder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Cache {

//    public static final String DIRECTORY = "C:\\Users\\pppep\\OneDrive\\Desktop\\Math\\aops";
    public static final int BUCKET_SIZE = 100;

    @Value("${work.dir}")
    private String workDir;

    public boolean has(String key) {
        Path path = getPath(key);
        return Files.exists(path);
    }

    public String get(String key) throws IOException {
        Path path = getPath(key);
        if (!Files.exists(path)) return null;

        return new String(Files.readAllBytes(path));
    }

    public String put(String key, String value) throws IOException {
        Path path = getPath(key);
        Files.createDirectories(path.getParent());
        Files.write(path, value.getBytes(StandardCharsets.UTF_8));
        return value;
    }

    public Path getPath(String key) {
        String hash = String.valueOf(Math.abs(key.hashCode()%BUCKET_SIZE));
        return Paths.get(workDir, hash, key);
    }

}
