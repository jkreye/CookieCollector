package com.pacman.pacmanjavafx;

import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final SoundManager instance = new SoundManager();
    private Map<String, AudioClip> soundMap;

    public SoundManager() {
        soundMap = new HashMap<>();
        this.loadSound("pacman_titlemusic", "assets/music/intermission.wav");
        this.loadSound("pacman_gamestart", "assets/music/game_start.wav");

    }
    public static SoundManager getInstance() {
        return instance;
    }

    public void loadSound(String name, String path) {
        try {
            AudioClip clip = new AudioClip(getClass().getResource(path).toExternalForm());
            soundMap.put(name, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(String name, boolean loop) {
        AudioClip clip = soundMap.get(name);
        if (clip != null) {
            if (clip.isPlaying()) {
                return; // Sound is already playing, so do nothing
            }
            clip.setCycleCount(loop ? AudioClip.INDEFINITE : 1);
            clip.play();
        }
    }

    public void stopSound(String name) {
        AudioClip clip = soundMap.get(name);
        if (clip != null) {
            clip.stop();
        }
    }

    public AudioClip getSound(String name) {
        return soundMap.get(name);
    }
}
