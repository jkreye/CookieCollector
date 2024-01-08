package com.pacman.pacmanjavafx;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Duration;

import static com.pacman.pacmanjavafx.Config.HEIGHT;
import static com.pacman.pacmanjavafx.Config.WIDTH;

public class PacManGameOverController {

    @FXML
    private Button retryButton; // Button, um das Spiel neu zu starten
    @FXML
    private Button mainMenuButton; // Button, um zum Hauptmenü zurückzukehren

    @FXML
    private void onRetryButtonClicked() {
        playAgain();
    }

    @FXML
    private void onMainMenuButtonClicked() {
        exitToMenu();
    }


    public void initialize() {
        // Animation für den Start-Button
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), retryButton);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();

    }

    private void playAgain() {
        try {
            // Lädt das Spiel erneut

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PacManGameView.fxml"));
            Parent gameRoot = loader.load();

            Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
            Stage stage = (Stage) retryButton.getScene().getWindow();
            stage.setScene(gameScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitToMenu() {
        try {
            // Geht zurück zum Hauptmenü
            SoundManager.getInstance().playSound("pacman_titlemusic", true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PacManMenuView.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot, WIDTH, HEIGHT);
            Stage stage = (Stage) mainMenuButton.getScene().getWindow();
            stage.setScene(menuScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
