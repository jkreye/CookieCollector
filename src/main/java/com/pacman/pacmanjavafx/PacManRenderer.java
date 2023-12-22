package com.pacman.pacmanjavafx;

import com.pacman.pacmanjavafx.model.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.List;


public class PacManRenderer {
    private final GraphicsContext gc;
    private PacManGameController gameController;
    private int animationIndex = 0;


    private SpriteSheet pacmanSpriteSheet;
    private SpriteSheet ghostSHADOWSprites;
    private SpriteSheet ghostPOKEYSprites;
    private SpriteSheet ghostSPEEDYSprites;
    private SpriteSheet ghostBASHFULSprites;
    private SpriteSheet ghostVULNERABLESprites;
    private final SpriteSheet powerpillSpriteSheet;

    public PacManRenderer(GraphicsContext gc, PacManGameController gameController) {
        this.gc = gc;
        this.gameController = gameController;

        this.pacmanSpriteSheet = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/PacMan.png", 16);
        this.powerpillSpriteSheet = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/cookie.png", 353);
        this.ghostVULNERABLESprites = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/vulnerableGhost.png", 16);
        this.ghostSHADOWSprites = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/blueGhost.png", 16);
        this.ghostPOKEYSprites = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/greenGhost.png", 16);
        this.ghostBASHFULSprites = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/redGhost.png", 16);
        this.ghostSPEEDYSprites = new SpriteSheet("/com/pacman/pacmanjavafx/assets/img/yellowGhost.png", 16);

    }

    public void renderPacMan(PacMan pacman, int radius,int startX, int startY) {
        Image pacmanSprite = pacmanSpriteSheet.getSprite(animationIndex);
        PacManGameController.ACTION pacmanDirection = gameController.getLastDirection();

        ImageView pacmanImageView = new ImageView(pacmanSprite);
        // Rotieren des Sprites basierend auf der Richtung
        double rotationAngle = 0;
        switch (pacmanDirection) {
            case MOVE_UP:
                rotationAngle = -90;
                break;
            case MOVE_DOWN:
                rotationAngle = 90;
                break;
            case MOVE_LEFT:
                rotationAngle = 180;
                pacmanImageView.setScaleY(-1);
                break;
            case MOVE_RIGHT:
                // Keine Drehung notwendig
                break;
        }

        pacmanImageView.setRotate(rotationAngle);
        // Skalieren des Bildes auf die Größe der Zelle
        pacmanImageView.setFitWidth(radius*2);
        pacmanImageView.setFitHeight(radius*2);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        Image rotatedImage = pacmanImageView.snapshot(params, null);
        // Zeichnen des Sprites an der Position von Pac-Man
        gc.drawImage(rotatedImage, startX + pacman.getX(), startY + pacman.getY(), radius*2, radius*2);

    }


    public void renderMaze(Maze maze, int startX, int startY) {
        char[][] grid = maze.getGrid();
        int cellSize = maze.getCellSize();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                switch (grid[row][col]) {
                    case '#':
                        drawWalls(gc, startX, startY, cellSize, row, col);
                        break;
                    case '-':
                        drawGates(gc, startX, startY, cellSize, row, col);
                        break;
                    case '.':
                        drawCoins(gc, startX, startY, cellSize, row, col);
                        break;
                    case 'o':
                        drawKillCoins(gc, startX, startY, cellSize, row, col);
                        break;
                    case 'T':
                        drawTeleportPoints(gc, startX, startY, cellSize, row, col);
                        break;
                }
            }
        }
    }

    private void drawWalls(GraphicsContext gc, int startX, int startY, int cellSize, int row, int col) {
        gc.setFill(Color.BLUE);
        gc.fillRect(startX + col * cellSize, startY + row * cellSize, cellSize, cellSize);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(startX + col * cellSize, startY + row * cellSize, cellSize, cellSize);
    }

    private void drawGates(GraphicsContext gc, int startX, int startY, int cellSize, int row, int col) {
        if (gameController.isAnyGhostMovingToReleasePoint()) {
            gc.setFill(Color.BLACK);
        } else {
            gc.setFill(Color.GRAY);
        }
        gc.fillRect(startX + col * cellSize, startY + row * cellSize + cellSize / 3, cellSize, cellSize / 3);
    }

    private void drawCoins(GraphicsContext gc, int startX, int startY, int cellSize, int row, int col) {
        int coinSize = cellSize / 5;
        int coinX = startX + col * cellSize + cellSize / 2 - coinSize / 2;
        int coinY = startY + row * cellSize + cellSize / 2 - coinSize / 2;
        gc.setFill(Color.YELLOW);
        gc.fillOval(coinX, coinY, coinSize, coinSize);
    }

    private void drawKillCoins(GraphicsContext gc, int startX, int startY, int cellSize, int row, int col) {
        int killCoinSize = (int) (cellSize * 0.8);
        int killCoinX = startX + col * cellSize + cellSize / 2 - killCoinSize / 2;
        int killCoinY = startY + row * cellSize + cellSize / 2 - killCoinSize / 2;
        Image killCoinSprite = powerpillSpriteSheet.getImage();

        ImageView imageView = new ImageView(killCoinSprite);

        imageView.setFitWidth(killCoinSize);
        imageView.setFitHeight(killCoinSize);

        imageView.setPreserveRatio(true);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        Image scaledImage = imageView.snapshot(params, null);

        // Zeichnen Sie das skalierte Bild auf den Canvas
        gc.drawImage(scaledImage, killCoinX, killCoinY);

    }

    private void drawTeleportPoints(GraphicsContext gc, int startX, int startY, int cellSize, int row, int col) {
        int telPointSize = (int) Math.round(cellSize * 0.8);
        int telX = startX + col * cellSize + cellSize / 2 - telPointSize / 2;
        int telY = startY + row * cellSize + cellSize / 2 - telPointSize / 2;
        gc.setStroke(Color.MAGENTA);
        gc.setLineWidth(2);
        gc.strokeOval(telX, telY, telPointSize, telPointSize);
    }


    public void renderGhosts(List<Ghost> ghosts, int radius, int mazeStartX, int mazeStartY) {
        for (Ghost ghost : ghosts) {
            // Umrechnen der Geisterposition in Bildschirmkoordinaten
            int ghostX = ghost.getX() + mazeStartX;
            int ghostY = ghost.getY() + mazeStartY;

            SpriteSheet ghostSpriteSheet;
            if (ghost.getVulnerable()) {
                if (ghost.isBlinking() && (System.currentTimeMillis() / 250) % 2 == 0) {
                    ghostSpriteSheet = getGhostSprite(ghost); // Blinkfarbe
                } else {
                    ghostSpriteSheet = ghostVULNERABLESprites;
                }
            } else {
                ghostSpriteSheet = getGhostSprite(ghost);
            }

            Image ghostSprite = ghostSpriteSheet.getSprite(animationIndex);

            ImageView imageView = new ImageView(ghostSprite);
            imageView.setFitWidth(radius * 2); // Skalieren auf die Größe des Geistes
            imageView.setFitHeight(radius * 2);

            imageView.setPreserveRatio(true);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            Image scaledImage = imageView.snapshot(params, null);

            // Zeichnen Sie das skalierte Bild auf den Canvas
            gc.drawImage(scaledImage, ghostX, ghostY);
        }
    }

    public SpriteSheet getGhostSprite(Ghost ghost) {
        SpriteSheet ghostSpriteSheet;

        switch (ghost.getType()) {
            case SHADOW:
                ghostSpriteSheet = ghostSHADOWSprites;
                break;
            case SPEEDY:
                ghostSpriteSheet = ghostSPEEDYSprites;
                break;
            case BASHFUL:
                ghostSpriteSheet = ghostBASHFULSprites;
                break;
            case POKEY:
                ghostSpriteSheet = ghostPOKEYSprites;
                break;
            default:
                throw new IllegalStateException("Unbekannter Geistertyp");
        }
        return ghostSpriteSheet;
    }


    public void renderLevelAndProgress(PacManGameController gameController, int startX, int mazeWidth) {
        // Level-Text
        int currentLevel = gameController.getMazeInst().getCurrentLevel() + 1;
        String levelText = "Level " + currentLevel;
        gc.setFill(Color.WHITE); // Weißer Text
        gc.setFont(new Font("Arial", 20)); // Schriftart und -größe einstellen

        // Berechnen der Position für den Level-Text
        double levelTextWidth = gc.getFont().getSize() * levelText.length() * 0.6; // angenäherte Breite
        double x = startX + mazeWidth - levelTextWidth - 10; // 10 Pixel vom rechten Rand
        double y = 30; // y-Position für den Text

        gc.fillText(levelText, x, y);

        // Fortschrittsleiste
        renderProgressBar(gameController, x, y + 5, levelTextWidth); // 5 Pixel unterhalb des Textes
    }

    public void renderScoreAndLives(Text scoreText, Maze maze) {
        // Score
        int score = gameController.getScore();
        scoreText.setText("Score: " + score); // Aktualisieren des Score-Texts

        // Leben
        int lives = gameController.getLives();
        int livesX = maze.getMazeStartX(); // X-Position für die Leben
        int livesY = maze.getMazeStartY() - 75; // Y-Position für die Leben
        int circleDiameter = 15; // Durchmesser der Kreise

        for (int i = 0; i < lives; i++) {
            gc.setFill(Color.YELLOW);
            gc.fillOval(livesX + i * (circleDiameter + 5), livesY, circleDiameter, circleDiameter);
        }
    }

    private void renderProgressBar(PacManGameController gameController, double x, double y, double width) {
        float progress = gameController.getProgress();
        int progressBarHeight = 8;

        // Hintergrund der Fortschrittsleiste
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x, y, width, progressBarHeight);

        // Fortschritt
        gc.setFill(Color.GREEN);
        double progressWidth = progress * width;
        gc.fillRect(x, y, progressWidth, progressBarHeight);
    }

    public void setAnimationIndex(int i) {
        this.animationIndex=i;
    }
    public int getAnimationIndex() {
        return this.animationIndex;
    }
}
