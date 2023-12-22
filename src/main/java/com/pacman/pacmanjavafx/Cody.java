package com.pacman.pacmanjavafx;
import java.io.File;
import java.net.URL;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Cody extends Group {
    private static final int SIZE = 200;
    private ImageView sprite = new ImageView("/assets/cody/Cody.png");
    private int xPosCody = 100;
    private int yPosCody = 100;
    public Group codyContainer = new Group();
    private int textBoxWidth = 800;
    private int containerWidth;
    private AudioClip audio;
    private static final int WINDOWHEIGHT = 1080;
    private static final int WINDOWWIDTH = 1920;
    private static Label message = new Label();
    private Timeline timeline;

    public Cody() {
        this.containerWidth = 200 + this.xPosCody + this.textBoxWidth;
        this.init();
        this.codyContainer.prefWidth((double)(200 + this.xPosCody + this.textBoxWidth));
        this.codyContainer.prefHeight(540.0D);
        this.codyContainer.setLayoutY(740.0D);
        this.codyContainer.setLayoutX((double)((1920 - this.containerWidth) / 2));
        this.getAudio();
        this.styleLabel();
    }

    private void getAudio() {
        String audioFilePath = "/com/pacman/pacmanjavafx/assets/cody/voice.mp3";

        try {
            URL resourceUrl = Cody.class.getResource(audioFilePath);
            this.audio = new AudioClip(resourceUrl.toString());
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void init() {
        this.sprite.setFitHeight(200.0D);
        this.sprite.setFitWidth(200.0D);
        this.sprite.setLayoutX((double)this.xPosCody);
        this.sprite.setLayoutY((double)this.yPosCody);
        this.codyContainer.getChildren().add(this.sprite);
        this.getChildren().add(this.codyContainer);
    }

    private void styleLabel() {
        Font font = new Font("Monocraft", 20.0D);
        message.setWrapText(true);
        message.setMaxWidth((double)this.textBoxWidth);
        message.setMaxHeight(200.0D);
        message.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)}));
        message.setStyle("-fx-border-color: black; -fx-border-width: 10px; -fx-border-style: solid; ");
        message.setPadding(new Insets(10.0D, 10.0D, 10.0D, 10.0D));
        message.setLayoutX((double)(this.xPosCody + 200));
        message.setLayoutY((double)(this.yPosCody + 20));
        message.setFont(font);
        message.setAlignment(Pos.TOP_LEFT);
    }

    public void say(String[] strings) {
        this.timeline = new Timeline();
        if (!this.getChildren().contains(this.codyContainer)) {
            this.getChildren().add(this.codyContainer);
        }

        String dynamicMessage = "";
        String[] var7 = strings;
        int var6 = strings.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String str = var7[var5];
            Duration duration = new Duration(timeline.getTotalDuration().toMillis() + 100.0D);
            timeline.getKeyFrames().add(new KeyFrame(duration, (e) -> {
                this.playAudio();
            }, new KeyValue[0]));
            char[] var12;
            int var11 = (var12 = str.toCharArray()).length;

            for(int var10 = 0; var10 < var11; ++var10) {
                char c = var12[var10];
                KeyFrame keyFrame = new KeyFrame(Duration.millis(timeline.getTotalDuration().toMillis() + 100.0D), new KeyValue[]{new KeyValue(message.textProperty(), dynamicMessage + c)});
                dynamicMessage = dynamicMessage + c;
                timeline.getKeyFrames().add(keyFrame);
            }

            duration = new Duration(timeline.getTotalDuration().toMillis() + 100.0D);
            timeline.getKeyFrames().add(new KeyFrame(duration, (e) -> {
                this.stopAudio();
            }, new KeyValue[0]));
            duration = new Duration(timeline.getTotalDuration().toMillis() + 200.0D);
            timeline.getKeyFrames().add(new KeyFrame(duration, (e) -> {
            }, new KeyValue[0]));
            dynamicMessage = "";
        }

        Duration duration = new Duration(timeline.getTotalDuration().toMillis() + 3000.0D);
        timeline.getKeyFrames().add(new KeyFrame(duration, (e) -> {
            this.quit();
        }, new KeyValue[0]));
        timeline.play();
        this.codyContainer.getChildren().add(message);
    }

    private void playAudio() {
        if (this.audio != null) {
            this.audio.play();
        }
    }

    private void stopAudio() {
        if (this.audio != null) {
            this.audio.stop();
        }
    }

    public void quit() {
        this.codyContainer.getChildren().remove(message);
        this.getChildren().remove(this.codyContainer);
        message.setText("");
        if (this.timeline != null) {
            this.timeline.stop(); // Stoppt die Timeline
        }
        if (this.audio != null) {
            if (this.audio.isPlaying()) {
                this.audio.stop();
            }
        }

    }
}