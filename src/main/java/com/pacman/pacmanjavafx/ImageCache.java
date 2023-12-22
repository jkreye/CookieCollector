package com.pacman.pacmanjavafx;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static final Map<String, Image> cache = new HashMap<>();

    public static Image getImage(String path) {
        if (!cache.containsKey(path)) {
            cache.put(path, new Image(path));
        }
        return cache.get(path);
    }
}
