package com.pacman.pacmanjavafx.model;


public class PacMan {
    private int x, y;
    private int speed; // Geschwindigkeit von Pac-Man

    public PacMan(int startX, int startY, int speed) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
    }

    // Bewegungsmethoden
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

    // Getter- und Setter-Methoden
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
