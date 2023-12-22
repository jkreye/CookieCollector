package com.pacman.pacmanjavafx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

import static com.pacman.pacmanjavafx.PacManGameController.GhostType.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpriteSheet {
    private Image spriteSheet;
    private int tileSize;

    public SpriteSheet(String path, int tileSize) {
        this.spriteSheet = new Image(path);
        this.tileSize = tileSize;
    }

    public ImageView getSprite(int row, int column) {
        ImageView imageView = new ImageView(spriteSheet);
        imageView.setViewport(new javafx.geometry.Rectangle2D(column * tileSize, row * tileSize, tileSize, tileSize));
        return imageView;
    }
}

}
