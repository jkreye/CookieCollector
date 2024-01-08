package com.pacman.pacmanjavafx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import static com.pacman.pacmanjavafx.Config.HEIGHT;
import static com.pacman.pacmanjavafx.Config.WIDTH;

public class PacManApplication extends Application {

    public PacManApplication() {

    }

    public Scene getScene() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("PacManMenuView.fxml"));
            Parent gameRoot = (Parent)loader.load();

            Scene scene = new Scene(gameRoot, WIDTH, HEIGHT);
            scene.setFill(Color.BLACK);
            return scene;
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public void start(Stage primaryStage) {
        SoundManager.getInstance().playSound("pacman_titlemusic", true);


        Font.loadFont(getClass().getResourceAsStream("assets/font/8bit_wonder.TTF"), 25);

        Scene scene = this.getScene();
        primaryStage.setTitle("Cookie Collector");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
