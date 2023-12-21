package com.pacman.pacmanjavafx;

import com.pacman.pacmanjavafx.model.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PacManGameManager {

    private static final String HIGHSCORE_FILE_PATH = "src/main/java/com/pacman/pacmanjavafx/highscore.txt";
    private int highScore;
    private int score;
    private static PacManGameManager instance;

    public static PacManGameManager getInstance() {
        if (instance == null) {
            instance = new PacManGameManager();
        }
        return instance;
    }

    public PacManGameManager() {
        loadHighScore();
    }

    private void loadHighScore() {
        try {
            String scoreString = new String(Files.readAllBytes(Paths.get(HIGHSCORE_FILE_PATH)));
            highScore = Integer.parseInt(scoreString.trim());
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    public void saveHighScore() {
        try {
            Files.write(Paths.get(HIGHSCORE_FILE_PATH), String.valueOf(highScore).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getHighScore() {
        return highScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void updateHighscore() {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }

    public static void exitGame(Stage stage) {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
