package com.pacman.pacmanjavafx;

import javafx.scene.image.*;

import java.io.InputStream;

import static com.pacman.pacmanjavafx.PacManGameController.GhostType.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpriteSheet {
    private Image spriteSheet;
    private int tileSize;

    public SpriteSheet(String path, int tileSize) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.out.println("Ressource nicht gefunden");
        } else {
            this.spriteSheet = new Image(is);
        }
        this.tileSize = tileSize;
    }

    public Image getSprite(int index) {
        int numSpritesPerRow = (int) (spriteSheet.getWidth() / tileSize);
        int x = (index % numSpritesPerRow) * tileSize;
        int y = (index / numSpritesPerRow) * tileSize;

        PixelReader reader = spriteSheet.getPixelReader();
        return new WritableImage(reader, x, y, tileSize, tileSize);
    }

    public Image getImage() {
        return this.spriteSheet;
    }

}

