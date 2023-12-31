package com.pacman.pacmanjavafx.model;


import com.pacman.pacmanjavafx.PacManGameController;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import static com.pacman.pacmanjavafx.PacManGameController.GhostType.*;
import static com.pacman.pacmanjavafx.PacManGameController.ACTION.*;


public class Ghost {
    private int x, y;
    private int speed;
    private PacManGameController.GhostType type;
    private char letter;
    private boolean vulnerable;
    private boolean isInJail;
    private boolean isMovingToReleasePoint;
    private boolean blinking;
    private static final int STEP_SIZE = 3; // Größe eines Schrittes
    private PacManGameController.ACTION direction = PacManGameController.ACTION.MOVE_UP;

    public Ghost(int startX, int startY, PacManGameController.GhostType type, int speed, char letter) {
        this.x = startX;
        this.y = startY;
        this.type = type;
        this.speed = speed;
        this.letter = letter;
        this.vulnerable = false;
        this.isInJail = true; // Alle Geister starten im Jail
        this.blinking = false;
    }

    public void move() {
        // Bewegung basierend auf der aktuellen Richtung
        switch (this.direction) {
            case MOVE_UP:
                this.y -= this.speed;
                break;
            case MOVE_DOWN:
                this.y += this.speed;
                break;
            case MOVE_LEFT:
                this.x -= this.speed;
                break;
            case MOVE_RIGHT:
                this.x += this.speed;
                break;
        }
    }

    // Getter und Setter für Position, Richtung usw.
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public PacManGameController.ACTION getDirection() { return direction; }
    public void setDirection(PacManGameController.ACTION direction) { this.direction = direction; }
    public PacManGameController.GhostType getType() { return type; }

    public void moveUp() {
        y -= speed;
    }

    public void moveDown() {
        y += speed;

    }

    public void moveLeft() {
        x -= speed;

    }

    public void moveRight() {
        x += speed;

    }

    public int getSpeed() {return speed;}

    public void setSpeed(int newSpeed) {speed = newSpeed;}

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public void setVulnerable(boolean b) {
        this.vulnerable = b;
    }
    public boolean getVulnerable() {
        return vulnerable;
    }

    public boolean isInJail() {
        return isInJail;
    }

    public void setIsInJail(boolean isInJail) {
        this.isInJail = isInJail;
    }

    public void moveToReleasePoint(Point2D releasePoint) {
        // Logik, um den Geist Schritt für Schritt zum Freigabepunkt zu bewegen
        // Bewegung auf der X-Achse
        if (x < releasePoint.getX()) {
            if (releasePoint.getX() - x < STEP_SIZE) {
                x = (int) releasePoint.getX(); // Setze x direkt auf das Ziel, wenn der verbleibende Abstand kleiner als STEP_SIZE ist
            } else {
                x += STEP_SIZE;
            }
        } else if (x > releasePoint.getX()) {
            if (x - releasePoint.getX() < STEP_SIZE) {
                x = (int) releasePoint.getX(); // Setze x direkt auf das Ziel, wenn der verbleibende Abstand kleiner als STEP_SIZE ist
            } else {
                x -= STEP_SIZE;
            }
        }

        // Bewegung auf der Y-Achse
        if (x == releasePoint.getX()) {
            if (y < releasePoint.getY()) {
                if (releasePoint.getY() - y < STEP_SIZE) {
                    y = (int) releasePoint.getY(); // Setze y direkt auf das Ziel, wenn der verbleibende Abstand kleiner als STEP_SIZE ist
                } else {
                    y += STEP_SIZE;
                }
            } else if (y > releasePoint.getY()) {
                if (y - releasePoint.getY() < STEP_SIZE) {
                    y = (int) releasePoint.getY(); // Setze y direkt auf das Ziel, wenn der verbleibende Abstand kleiner als STEP_SIZE ist
                } else {
                    y -= STEP_SIZE;
                }
            }
        }

        if (erreichtReleasePoint(releasePoint)) {
            isMovingToReleasePoint = false;
            isInJail = false; // Der Geist ist jetzt nicht mehr im Gefängnis
        }
    }

    private boolean erreichtReleasePoint(Point2D releasePoint) {
        // Überprüfen, ob der Geist den releasePoint erreicht hat
        return x == releasePoint.getX() && y == releasePoint.getY();
    }

    public void setIsMovingToReleasePoint(boolean b) {
        isMovingToReleasePoint = b;
    }

    public boolean isMovingToReleasePoint() {
        return isMovingToReleasePoint;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }

    public boolean isBlinking() {
        return blinking;
    }

    // Methode, um die Farbe des Geistes basierend auf seinem Typ zu bestimmen
    public Color getColor() {
        switch (this.getType()) {
            case SHADOW:
                return Color.RED;
            case SPEEDY:
                return Color.PINK;
            case POKEY:
                return Color.ORANGE;
            case BASHFUL:
                return Color.CYAN;
            default:
                return Color.GRAY;
        }
    }
}
