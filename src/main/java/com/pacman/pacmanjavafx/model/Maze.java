package com.pacman.pacmanjavafx.model;

/* Maze.java */

import javafx.geometry.Point2D;

import static com.pacman.pacmanjavafx.Config.PADDING_TOP;

public class Maze {
    private char[][][] grids;
    private char[][] grid;
    private int currentLevel = 0;

    private static int cellSize = 30;

    private int mazeStartX;
    private int mazeStartY;

    public Maze() {

        grids = new char[][][] {

                {
                        // Layout für Level 1
                        "############################".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#o####.#####.##.#####.####o#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#..........................#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "######.##### ## #####.######".toCharArray(),
                        "     #.##### ## #####.#     ".toCharArray(),
                        "     #.##     R    ##.#     ".toCharArray(),
                        "     #.## ###--### ##.#     ".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "T     .   # CBSI #   .     T".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "     #.## ######## ##.#     ".toCharArray(),
                        "     #.##    P     ##.#     ".toCharArray(), // 'P' als Startpunkt
                        "     #.## ######## ##.#     ".toCharArray(),
                        "######.## ######## ##.######".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#o..##.......  .......##..o#".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#..........................#".toCharArray(),
                        "############################".toCharArray()
                },
                {
                        // Layout für Level 2
                        "############################".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#o##########.##.##########o#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#..........................#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "######.##### ## #####.######".toCharArray(),
                        "     #.##### ## #####.#     ".toCharArray(),
                        "     #.##     R    ##.#     ".toCharArray(),
                        "     #.## ###--### ##.#     ".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "T     .   # CBSI #   .     T".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "     #.## ######## ##.#     ".toCharArray(),
                        "     #.##    P     ##.#     ".toCharArray(), // 'P' als Startpunkt
                        "     #.## ######## ##.#     ".toCharArray(),
                        "######.## ######## ##.######".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#o..##.......  .......##..o#".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#..........................#".toCharArray(),
                        "############################".toCharArray()
                },
                {
                        // Layout für Level 3
                        "############################".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#o##########.##.##########o#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#......##..................#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#.####.##.########.##.####.#".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "######.##### ## #####.######".toCharArray(),
                        "     #.##### ## #####.#     ".toCharArray(),
                        "     #.##     R    ##.#     ".toCharArray(),
                        "     #.## ###--### ##.#     ".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "T     .   # CBSI #   .     T".toCharArray(),
                        "######.## #      # ##.######".toCharArray(),
                        "     #.## ######## ##.#     ".toCharArray(),
                        "     #.##    P     ##.#     ".toCharArray(), // 'P' als Startpunkt
                        "     #.## ######## ##.#     ".toCharArray(),
                        "######.## ######## ##.######".toCharArray(),
                        "#............##............#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#.####.#####.##.#####.####.#".toCharArray(),
                        "#o..##.......  .......##..o#".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "###.##.##.########.##.##.###".toCharArray(),
                        "#......##....##....##......#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#.##########.##.##########.#".toCharArray(),
                        "#..........................#".toCharArray(),
                        "############################".toCharArray()
                }

        };

        grid = copyGrid(grids[currentLevel]);

    }

    private char[][] copyGrid(char[][] source) {
        char[][] copy = new char[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }

    public void resetMaze() {
        grid = copyGrid(grids[currentLevel]);
    }

    public void changeLevel(int level) {
        currentLevel = (level % 3) == 0 ? 2 : (level % 3) - 1;
        resetMaze();
    }

    public char[][] getGrid() {
        return grid;
    }

    public Point2D findPacmanStart() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 'P') {
                    int startX = col * cellSize;
                    int startY = row * cellSize;
                    // System.out.println("P "+cellSize + "*" + col + " " + row);

                    return new Point2D(startX, startY);
                }
            }
        }
        return null; // oder Standard-Startposition, falls 'P' nicht gefunden wurde
    }

    // Methode zum Finden des Freigabepunktes
    public Point2D findReleasePoint() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 'R') { // 'R' markiert den Freigabepunkt
                    int startX = col * cellSize;
                    int startY = row * cellSize;
                    // System.out.println("P "+cellSize + "*" + col + " " + row);

                    return new Point2D(startX, startY);
                }
            }
        }
        return null; // Falls kein Freigabepunkt gefunden wird
    }

    public Point2D findGhostStart(char ghostChar) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == ghostChar) {
                    int startX = col * cellSize;
                    int startY = row * cellSize;
                    // System.out.println("P "+cellSize + "*" + col + " " + row);

                    return new Point2D(startX, startY);
                }
            }
        }
        return null; // oder Standard-Startposition, falls 'P' nicht gefunden wurde
    }

    public void calculateMazeSize(int panelWidth, int panelHeight, char[][] grid, int padding) {
        cellSize = Math.min(panelWidth / grid[0].length, panelHeight / (grid.length + padding));
        // Berechnen der Größe des gesamten Labyrinths
        int mazeWidth = grid[0].length * cellSize;
        int mazeHeight = (grid.length+PADDING_TOP) * cellSize;

        // Berechnen der Startposition, um das Labyrinth in der Mitte zu zeichnen
        this.setMazeStartX((panelWidth - mazeWidth) / 2);

        // Definieren eines oberen Randes
        int paddingTop = 150;
        this.setMazeStartY(paddingTop + (panelHeight - mazeHeight - 100) / 2);
    }
    public static int getCellSize() {
        return cellSize;
    }


    public int getMazeStartY() {
        return mazeStartY;
    }

    public void setMazeStartY(int mazeStartY) {
        this.mazeStartY = mazeStartY;
    }

    public int getMazeStartX() {
        return mazeStartX;
    }

    public void setMazeStartX(int mazeStartX) {
        this.mazeStartX = mazeStartX;
    }

    public boolean isWall(int gridX, int gridY) {
        // Überprüfen, ob die Koordinaten innerhalb der Grenzen des Labyrinths liegen
        if (gridX < 0 || gridX >= grid[0].length || gridY < 0 || gridY >= grid.length) {
            return true; // Position außerhalb des Labyrinths wird als Wand betrachtet
        }

        return grid[gridY][gridX] == '-' || grid[gridY][gridX] == '#'; // '#' repräsentiert eine Wand
    }

    public boolean isWallOrTeleportForGhost(int gridX, int gridY) {
        // Überprüfen, ob die Koordinaten innerhalb der Grenzen des Labyrinths liegen
        if (gridX < 0 || gridX >= grid[0].length || gridY < 0 || gridY >= grid.length) {
            return true; // Position außerhalb des Labyrinths wird als Wand betrachtet
        }

        // '#'-Zeichen repräsentiert eine Wand, 'T' repräsentiert einen Teleportationspunkt
        return grid[gridY][gridX] == '#' || grid[gridY][gridX] == '-' || grid[gridY][gridX] == 'T';
    }

    public int getTotalDots() {
        int count = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == '.') {
                    count++;
                }
            }
        }
        return count;
    }

    public int getTotalPpills() {
        int count = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == 'o') {
                    count++;
                }
            }
        }
        return count;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Berechnet die Breite des Labyrinths in Pixeln.
     * @return Die Gesamtbreite des Labyrinths.
     */
    public int getMazeWidth() {
        if (grid == null) {
            return 0; // Kein Labyrinth vorhanden
        }
        int columns = grid[0].length; // Anzahl der Spalten (Annahme: alle Zeilen haben dieselbe Länge)
        return columns * cellSize; // Gesamtbreite = Anzahl der Spalten * Zellengröße
    }
}

