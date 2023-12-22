package com.pacman.pacmanjavafx;

import com.pacman.pacmanjavafx.model.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;


public class PacManRenderer {
    private final GraphicsContext gc;
    private PacManGameController gameController;
    private int animationIndex = 0;
    private long lastAnimationTime = 0;
    private static final long ANIMATION_INTERVAL = 100; // Zeit in Millisekunden zwischen den Animationsframes


    private SpriteSheet ghostSpriteSheet;
    private SpriteSheet pacmanSpriteSheet;
    private SpriteSheet coinSpriteSheet;

    public PacManRenderer(GraphicsContext gc, PacManGameController gameController) {
        this.gc = gc;
        this.gameController = gameController;
        // this.ghostSpriteSheet = SpriteSheet.getGhostSprite(PacManGameController.GhostType.SPEEDY); // Beispiel
        this.pacmanSpriteSheet = new SpriteSheet("path/to/pacman_spritesheet.png", 32);
        // this.coinSpriteSheet = SpriteSheet.getCoinSprite();
    }

    public void updateAnimationIndex() {
        // Aktualisiere den Animationsindex, wenn genug Zeit vergangen ist
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAnimationTime > ANIMATION_INTERVAL) {
            animationIndex = (animationIndex + 1) % 8; // Es gibt 8 Sprites für die Animation
            lastAnimationTime = currentTime;
        }
    }

    public void renderPacMan(PacMan pacman, int radius,int startX, int startY) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(startX+pacman.getX(), startY+pacman.getY(), radius * 2, radius * 2);
        // ImageView pacmanSprite = pacmanSpriteSheet.getSprite(animationIndex);
        // gc.drawImage(pacmanSprite.getImage(), startX + img.getX(), startY + img.getY(), radius * 2, radius * 2);

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
        gc.setFill(Color.BROWN);
        gc.fillOval(killCoinX, killCoinY, killCoinSize, killCoinSize);
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
            int ghostX = ghost.getX() +mazeStartX;
            int ghostY = ghost.getY() +mazeStartY;

            // Farbe basierend auf dem Geistertyp setzen
            if (ghost.getVulnerable()) {
                if (ghost.isBlinking() && (System.currentTimeMillis() / 250) % 2 == 0) {
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(ghostX, ghostY, radius * 2, radius * 2); // Blinkfarbe
                } else {
                    gc.setFill(Color.BLUE);
                    gc.fillOval(ghostX, ghostY, radius * 2, radius * 2); // Normale Farbe für verwundbare Geister
                }
            } else {
                // Farbe basierend auf dem Geistertyp setzen
                gc.setFill(Color.GREEN);
                gc.fillOval(ghostX, ghostY, radius * 2, radius * 2);

            }

            // Zeichnen des Geists
            // g.fillOval(ghostX, ghostY, cellSize, cellSize);
            // g.drawRect(ghostX, ghostY, cellSize, cellSize);

        }
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
}
