package com.pacman.pacmanjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
