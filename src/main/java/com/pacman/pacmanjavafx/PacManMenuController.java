package com.pacman.pacmanjavafx;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import static com.pacman.pacmanjavafx.Config.HEIGHT;
import static com.pacman.pacmanjavafx.Config.WIDTH;

public class PacManMenuController {
    @FXML
    private VBox vbox;
    @FXML
    private StackPane stackPane;

    @FXML
    private Button startButton;
    @FXML
    private Button exitButton;
    @FXML
    private Text highscoreText;

    public void initialize() {
        highscoreText.setText("Highscore: " + PacManGameManager.getInstance().getHighScore());
        startButton.setFocusTraversable(true);

        // Animation für den Start-Button
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), startButton);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();

        // Ereignisbehandlung für den Start-Button
        startButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                onStartButtonClicked();
            }
        });
    }

    @FXML
    private void onStartButtonClicked() {
        try {
            SoundManager.getInstance().stopSound("pacman_titlemusic");
            // Laden Sie Ihre Pac-Man-Spielszene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PacManGameView.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot, WIDTH, HEIGHT);
            Stage stage = (Stage) startButton.getScene().getWindow();

            stage.setScene(gameScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExitButtonClicked() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        // Angenommen, Sie haben eine Exit-Methode in Ihrem GameManager
        PacManGameManager.exitGame(stage);
    }
}
