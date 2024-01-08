package com.pacman.pacmanjavafx;

import com.pacman.pacmanjavafx.model.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PacManGameManager {

    private static final String HIGHSCORE_FILE_NAME = "pacman_highscore.txt";
    private int highScore;
    private int score;
    private static PacManGameManager instance;
    private SoundManager soundManager;


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
            String userHome = System.getProperty("user.home");
            Path highScorePath = Paths.get(userHome, HIGHSCORE_FILE_NAME);

            if (Files.exists(highScorePath)) {
                String highScoreStr = new String(Files.readAllBytes(highScorePath));
                highScore = Integer.parseInt(highScoreStr);
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }

    // Methode zum Überprüfen, ob ein neuer Highscore erreicht wurde
    public String[] getGameOverMessages(int currentScore) {
        if (currentScore > highScore) {
            return new String[]{
                    "Neuer Highscore: " + currentScore + "!",
                    "Großartig, du hast den alten Highscore geschlagen!"
            };
        } else {
            return new String[]{
                    "Oh nein, du hast verloren!",
                    "Versuche es noch einmal und schlage den Highscore von " + highScore + "."
            };
        }
    }

    public void saveHighScore() {
        try {
            // Pfad zum Benutzerverzeichnis ermitteln
            String userHome = System.getProperty("user.home");
            Path highScorePath = Paths.get(userHome, HIGHSCORE_FILE_NAME);

            // Highscore in der Datei speichern
            Files.write(highScorePath, String.valueOf(highScore).getBytes());
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
            // saveHighScore();
        }
    }

    public static void exitGame(Stage stage) {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
