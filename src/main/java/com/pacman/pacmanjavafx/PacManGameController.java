package com.pacman.pacmanjavafx;

import com.pacman.pacmanjavafx.model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.pacman.pacmanjavafx.Config.*;

public class PacManGameController {

    @FXML
    private Canvas gameCanvas;
    @FXML
    private Text scoreText;

    // Timer
    private AnimationTimer gameLoop;

    // Instanzen
    private PacMan pacman;
    private Maze maze;
    private List<Ghost> ghosts;
    private Random random = new Random();

    // Spielzustände und Objekte
    private int totalDots; // Total number of dots in the level
    private int collectedDots = 0; // Number of collected dots
    private int totalPpillscoins; // Total number of bitcoins in the level
    private int collectedPpills = 0;

    private static final int OFFSET = 4; // Ein Viertel der Zellengröße als Versatz

    private ACTION lastDirection = ACTION.MOVE_NONE;
    private ACTION nextDirection = ACTION.MOVE_NONE;
    private int score = 0; // Punktestand
    private int lives = 3; // Anzahl der Leben

    private long vulnerabilityDuration = 0;
    private static final long VULNERABILITY_TIME = 5 * 1_000_000_000L; // 5 Sekunden in Nanosekunden

    private long nextGhostReleaseTime;
    private static final long GHOST_RELEASE_INTERVAL = 10_000L * 1_000_000L; // 5 Sekunden in Nanosekunden
    private long lastGhostReleaseTime = 0;
    private static final long INITIAL_RELEASE_DELAY = 2_000L * 1_000_000L; // 2 Sekunde in Nanosekunden
    private static final long BLINK_THRESHOLD = 2 * 1_000_000_000L; // Blinken beginnt 2 Sekunden vor Ende der Verwundbarkeit

    // GameLoop
    private static final long UPDATE_TIME = 1_000_000_000 / 20; // 20 Updates pro Sekunde
    private static final long FRAME_TIME = 1_000_000_000 / 60; // 60 Frames pro Sekunde
    private long lastUpdateTime = 0; // Zeit des letzten Updates
    private long lastFrameTime = 0; // Zeit des letzten Frames


    //



    // ESC GAMEOVER
    private long lastEscPressTime = 0;
    private static final long DOUBLE_PRESS_INTERVAL = 500; // Zeitintervall für Doppelklick in Millisekunden

    // Enum
    public enum ACTION{
        QUIT,
        START,
        PAUSE_TOGGLE,
        MENU,
        HIGH_SCORES,
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_NONE,
        LOST,
        WIN
    }

    // Ghost
    public enum GhostType {
        SHADOW, // Blinky
        SPEEDY, // Pinky
        BASHFUL, // Inky
        POKEY // Clyde
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE // kann verwendet werden, wenn der Geist nicht in Bewegung ist
    }

    public void initialize() {
        // Initialisierung des Spiels
        setupGame();
        setupInputHandling();

        this.gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Update der Spiellogik
                if (now - lastUpdateTime >= UPDATE_TIME) {
                    updateGame(now);
                    lastUpdateTime = now;
                }

                // Rendering des Spiels
                if (now - lastFrameTime >= FRAME_TIME) {
                    renderGame();
                    lastFrameTime = now;
                }
            }
        };
        gameLoop.start();
    }

    private void initializePacmanAndGhosts() {
        initializePacman();
        initializeGhosts();
    }

    private void initializeGhosts() {
        this.ghosts = new ArrayList<>();
        int ghostSpeed = 10; // Konstante für die Geschwindigkeit der Geister

        // Initialisieren der Geister an ihren Startpositionen
        addGhostToGame('B', GhostType.SHADOW, ghostSpeed);
        addGhostToGame('I', GhostType.BASHFUL, ghostSpeed);
        addGhostToGame('S', GhostType.SPEEDY, ghostSpeed);
        addGhostToGame('C', GhostType.POKEY, ghostSpeed);
    }

    /**
     * Initialisiert Pac-Man mit Startwerten.
     * Setzt die Startposition, Geschwindigkeit und andere relevante Eigenschaften von Pac-Man.
     */
    private void initializePacman() {
        Point2D pacmanStart = maze.findPacmanStart();
        System.out.println(pacmanStart.getX());
        this.pacman = new PacMan((int) pacmanStart.getX(), (int) pacmanStart.getY(), 0); // Startposition von Pac-Man
        this.pacman.setSpeed(10); // Setzen der Geschwindigkeit
    }

    private void addGhostToGame(char ghostChar, GhostType type, int speed) {
        Point2D ghostStart = maze.findGhostStart(ghostChar);
        if (ghostStart != null) {
            this.ghosts.add(new Ghost((int) ghostStart.getX(), (int) ghostStart.getY(), type, speed, ghostChar));
        } else {
            System.err.println("Startposition für Geist " + ghostChar + " nicht gefunden.");
        }
    }

    private void setupGame() {
        // Initialisieren Sie Pac-Man, Geister, Labyrinth, etc.
        this.maze = new Maze();
        maze.calculateMazeSize(WIDTH, HEIGHT, maze.getGrid(), PADDING_TOP);
        lastDirection = ACTION.MOVE_NONE;
        nextDirection = ACTION.MOVE_NONE;
        lives = 3;
        score = 0;
        nextGhostReleaseTime = System.currentTimeMillis() + INITIAL_RELEASE_DELAY;

        totalDots = maze.getTotalDots();
        totalPpillscoins = maze.getTotalPpills();
        collectedDots = 0;
        collectedPpills = 0;

        initializePacmanAndGhosts();
    }

    private void setupInputHandling() {
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                nextDirection = ACTION.MOVE_UP;
                if (!isCollision(ACTION.MOVE_UP)) {
                    setLastDirection(ACTION.MOVE_UP);
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                nextDirection = ACTION.MOVE_DOWN;
                if (!isCollision(ACTION.MOVE_DOWN)) {
                    setLastDirection(ACTION.MOVE_DOWN);
                }
            } else if (event.getCode() == KeyCode.LEFT) {
                nextDirection = ACTION.MOVE_LEFT;
                if (!isCollision(ACTION.MOVE_LEFT)) {
                    setLastDirection(ACTION.MOVE_LEFT);
                }
            } else if (event.getCode() == KeyCode.RIGHT) {
                nextDirection = ACTION.MOVE_RIGHT;
                if (!isCollision(ACTION.MOVE_RIGHT)) {
                    setLastDirection(ACTION.MOVE_RIGHT);
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                handleEscapeKeyPress();
            }
        });
    }

    private void updateGame(long now) {
        // Aktualisieren Sie Spielzustände: Bewegungen von Pac-Man und Geistern, Kollisionen, etc.
        processMovement();
        updateGhostPositions();
        checkAndTeleportPacman();
        checkPacmanGhostCollision();
        checkForPowerPill();

        if (ghosts != null) {
            updateGhostStates(now);
        }

        if (now - lastGhostReleaseTime >= GHOST_RELEASE_INTERVAL) {
            releaseGhostFromJail();
            lastGhostReleaseTime = now;
        }

    }


    private void renderGame() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        int cellSize = Maze.getCellSize();
        int mazeStartX = maze.getMazeStartX();
        int mazeStartY = maze.getMazeStartY();

        // Zeichnen Sie das Spiel: Labyrinth, Pac-Man, Geister, Punkte, etc.
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight()); // Leeren des Canvas

        PacManRenderer renderer = new PacManRenderer(gc, this);
        // Setzen Sie den Hintergrund des Canvas auf Schwarz
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Zeichnen von Pac-Man als gelber Kreis
        renderer.renderMaze(maze, mazeStartX,mazeStartY);
        renderer.renderPacMan(pacman, cellSize/2, mazeStartX,mazeStartY);
        renderer.renderGhosts(getGhosts(), cellSize/2, mazeStartX,mazeStartY);
    }


    // GAMEOVER
    private void gameOver() {
        gameLoop.stop(); // Stoppt den AnimationTimer
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOverView.fxml"));
            Parent gameOverRoot = loader.load();
            // Optional: Hier könnten Sie Highscore-Logik oder andere Aktionen durchführen
            PacManGameManager.getInstance().updateHighscore();

            Scene gameOverScene = new Scene(gameOverRoot, WIDTH, HEIGHT);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(gameOverScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Besseres Fehlerhandling
        }
    }

    private void handleEscapeKeyPress() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEscPressTime < DOUBLE_PRESS_INTERVAL) {
            gameOver(); // Beendet das Spiel, wenn ESC doppelt schnell gedrückt wird
        }
        lastEscPressTime = currentTime;
    }

    // Spiellogik Methoden
    public List<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * Überprüft, ob sich mindestens ein Geist auf dem Weg zum Freilassungspunkt befindet.
     * Durchläuft die Liste der Geister und prüft, ob irgendeiner von ihnen sich gerade zum Freilassungspunkt bewegt.
     *
     * @return true, wenn mindestens ein Geist auf dem Weg zum Freilassungspunkt ist, sonst false.
     */
    public boolean isAnyGhostMovingToReleasePoint() {
        if (ghosts == null) {
            return false;
        }
        for (Ghost ghost : ghosts) {
            if (ghost.isMovingToReleasePoint()) {
                return true;
            }
        }
        return false;
    }


    private void updateGhostStates(long deltaTime) {
        if (vulnerabilityDuration > 0) {
            updateVulnerabilityState(deltaTime);
        }

        for (Ghost ghost : ghosts) {
            if (ghost.isMovingToReleasePoint()) {
                ghost.moveToReleasePoint(maze.findReleasePoint());
            }
        }
    }

    private void updateVulnerabilityState(long deltaTime) {
        vulnerabilityDuration -= deltaTime;
        if (vulnerabilityDuration <= BLINK_THRESHOLD) {
            for (Ghost ghost : ghosts) {
                ghost.setBlinking(true);
            }
        }
        if (vulnerabilityDuration <= 0) {
            for (Ghost ghost : ghosts) {
                ghost.setVulnerable(false);
                ghost.setBlinking(false);
            }
        }
    }

    /**
     * Überprüft, ob Pac-Man eine Power-Pille aufgenommen hat.
     * Wenn Pac-Man eine Power-Pille aufnimmt, wird der Effekt der Power-Pille aktiviert,
     * der Punktestand erhöht und die Anzahl der gesammelten Power-Pillen aktualisiert.
     */
    private void checkForPowerPill() {
        Point2D pacmanPosition = getPacmanGridPosition();
        char[][] grid = maze.getGrid();

        if (grid[(int) pacmanPosition.getY()][(int) pacmanPosition.getX()] == 'o') {
            grid[(int) pacmanPosition.getY()][(int) pacmanPosition.getX()] = ' '; // Power-Pille entfernen
            activatePowerPillEffect(); // Effekt der Power-Pille aktivieren
            increaseScore(50); // Punktestand erhöhen
            collectedPpills++;
            checkLevelCompletion(); // Überprüfen, ob das Level abgeschlossen ist
        }
    }

    /**
     * Aktiviert den Effekt einer Power-Pille.
     * Alle Geister im Spiel werden für eine festgelegte Zeitdauer verwundbar.
     * Während dieser Zeit können die Geister von Pac-Man gefangen werden.
     * Setzt auch die Dauer der Verwundbarkeit der Geister auf einen festgelegten Wert.
     */
    private void activatePowerPillEffect() {
        if (ghosts != null && !ghosts.isEmpty()) {
            for (Ghost ghost : ghosts) {
                ghost.setVulnerable(true);
                ghost.setBlinking(false);
            }
            vulnerabilityDuration = VULNERABILITY_TIME;
        } else {
            System.err.println("Warnung: Keine Geister vorhanden, um den Power-Pill-Effekt zu aktivieren.");
        }
    }

    /**
     * Überprüft, ob das aktuelle Level abgeschlossen ist.
     * Der Fortschritt wird anhand der Anzahl der gesammelten Punkte und der Gesamtzahl der Punkte im Level berechnet.
     * Wenn der Fortschritt 100% erreicht, wird die Methode onLevelComplete aufgerufen, um zum nächsten Level überzugehen oder das Spiel zu beenden.
     */
    private void checkLevelCompletion() {
        if (Math.abs(getProgress() - 1.0f) < 0.001f) {
            onLevelComplete();
        }
    }

    /**
     * Berechnet den Fortschritt im aktuellen Level des Spiels.
     * Der Fortschritt wird als Verhältnis der gesammelten Gegenstände (Dots und Power-Pills)
     * zur Gesamtzahl der Gegenstände im Level berechnet.
     *
     * @return Den Fortschritt als Gleitkommazahl zwischen 0 und 1,
     *         wobei 1 den vollständigen Abschluss des Levels bedeutet.
     */
    public float getProgress() {
        int totalItems = totalDots + totalPpillscoins;
        int collectedItems = collectedDots + collectedPpills;

        if (totalItems == 0) return 0; // Vermeidung einer Division durch Null
        return (float) collectedItems / totalItems;
    }

    /**
     * Wird aufgerufen, wenn ein Level im Spiel abgeschlossen ist.
     * Diese Methode setzt das Spiel für das nächste Level zurück,
     * einschließlich der Aktualisierung des Labyrinths, der Anzeige des Level-Overlays,
     * der Rücksetzung von Pac-Man und den Geistern sowie der Aktualisierung der Tickrate.
     * Wenn das dritte Level abgeschlossen ist, wird das Spiel als gewonnen markiert.
     */
    private void onLevelComplete() {
        // next level!

    }

    /**
     * Lässt einen Geist aus dem Gefängnis frei, falls vorhanden.
     * Geht die Liste der Geister durch und setzt den ersten Geist, der sich im Gefängnis befindet und noch nicht
     * auf dem Weg zum Freilassungspunkt ist, auf den Weg zur Freilassung.
     * Nachdem ein Geist freigelassen wurde, wird der Zeitpunkt für die nächste Geisterfreilassung aktualisiert.
     */
    private void releaseGhostFromJail() {
        for (Ghost ghost : ghosts) {
            if (ghost.isInJail() && !ghost.isMovingToReleasePoint()) {
                ghost.setIsMovingToReleasePoint(true);
                break; // Nur einen Geist zur Zeit freilassen
            }
        }
        nextGhostReleaseTime = System.currentTimeMillis() + GHOST_RELEASE_INTERVAL;
    }

    /**
     * Verarbeitet die Bewegungslogik von Pac-Man.
     * Überprüft die aktuelle und nächste Richtung und bewegt Pac-Man entsprechend,
     * wenn das Spiel nicht pausiert ist und keine Kollision vorliegt.
     */
    private void processMovement() {
        if (lastDirection != ACTION.MOVE_NONE) {
            if (nextDirection != ACTION.MOVE_NONE && !isCollision(nextDirection)) {
                lastDirection = nextDirection;
                nextDirection = ACTION.MOVE_NONE;
            }
            movePacman(lastDirection);
        }
    }

    /**
     * Überprüft, ob Pac-Man auf einem Teleportationsfeld steht und teleportiert ihn bei Bedarf.
     * Teleportiert Pac-Man zum gegenüberliegenden Rand des Labyrinths, wenn er sich auf einem Teleportationsfeld befindet.
     */
    public void checkAndTeleportPacman() {
        if (this.pacman != null) {
            Point2D pacmanPosition = getPacmanGridPosition();
            char[][] grid = maze.getGrid();
            int cellSize = maze.getCellSize();

            // Prüfen, ob die Position von Pac-Man innerhalb der Grenzen des Grids liegt
            if (pacmanPosition.getY() >= 0 && pacmanPosition.getY() < grid.length &&
                    pacmanPosition.getX() >= 0 && pacmanPosition.getX() < grid[(int) pacmanPosition.getY()].length) {
                char cell = grid[(int) pacmanPosition.getY()][(int) pacmanPosition.getX()];
                if (cell == 'T') {

                    // triggerPortalBlink(); // Auslösen des Blinkens am Portal
                    teleportPacman(pacmanPosition, grid, cellSize);
                }
            }
        }
    }

    /**
     * Aktualisiert die Positionen aller Geister im Spiel.
     * Für jeden Geist, der sich nicht im Gefängnis befindet, wird überprüft, ob er sich an einer Kreuzung befindet,
     * sich umdrehen soll oder auf eine Kollision zusteuert.
     * Basierend auf diesen Prüfungen wird die Bewegungsrichtung des Geistes festgelegt und der Geist entsprechend bewegt.
     * Die Methode berücksichtigt, ob der Geist in eine neue Richtung gehen, sich umdrehen oder bei einer Kollision
     * eine zufällige neue Richtung wählen soll.
     */
    private void updateGhostPositions() {
        for (Ghost ghost : ghosts) {
            if (!ghost.isInJail()) {
                if (isAtIntersection(ghost)) {
                    ghost.setDirection(chooseDirectionAtIntersection(ghost));
                } else if (shouldTurnAround()) {
                    ghost.setDirection(getOppositeDirection(ghost.getDirection()));
                } else if (isCollisionForGhost(ghost, ghost.getDirection())) {
                    ghost.setDirection(getRandomDirection(ghost.getDirection(), ghost));
                }
                moveGhost(ghost, ghost.getDirection());
            }
        }
    }

    /**
     * Überprüft Kollisionen zwischen Pac-Man und allen Geistern im Spiel.
     * Wenn Pac-Man einen Geist berührt, wird abhängig vom Zustand des Geistes eine entsprechende Aktion ausgeführt.
     * Bei Berührung mit einem verwundbaren Geist wird dieser zurück zum Startpunkt gesetzt und ist nicht mehr verwundbar.
     * Bei einer Kollision mit einem nicht-verwundbaren Geist verliert Pac-Man ein Leben, und das Spiel wird zurückgesetzt.
     */
    private void checkPacmanGhostCollision() {
        List<Ghost> ghostsCopy = new ArrayList<>(ghosts);
        if (pacman != null) {
            for (Ghost ghost : ghostsCopy) {
                if (Math.abs(pacman.getX() - ghost.getX()) < Maze.getCellSize() &&
                        Math.abs(pacman.getY() - ghost.getY()) < Maze.getCellSize()) {

                    if (ghost.getVulnerable()) {
                        resetVulnerableGhost(ghost);
                    } else {
                        handleCollisionWithInvulnerableGhost();
                        break; // Unterbrechen der Schleife, da die Kollision bereits behandelt wird
                    }
                }
            }
        }
    }

    /**
     * Überprüft, ob sich ein Geist an einer Kreuzung befindet.
     * Eine Kreuzung wird als ein Punkt definiert, an dem der Geist mehr als eine mögliche Bewegungsrichtung hat,
     * ohne mit einem Hindernis zu kollidieren. Die entgegengesetzte Richtung zur aktuellen Bewegungsrichtung des Geistes
     * wird dabei nicht berücksichtigt, da ein Geist sich normalerweise nicht umdrehen sollte.
     *
     * @param ghost Der Geist, dessen Position überprüft wird.
     * @return true, wenn sich der Geist an einer Kreuzung befindet, sonst false.
     */
    private boolean isAtIntersection(Ghost ghost) {
        if (ghost == null) {
            throw new IllegalArgumentException("Geist darf nicht null sein.");
        }

        int availableDirections = 0;
        List<ACTION> movementDirections = Arrays.asList(
                ACTION.MOVE_UP, ACTION.MOVE_DOWN, ACTION.MOVE_LEFT, ACTION.MOVE_RIGHT);

        for (ACTION direction : movementDirections) {
            if (direction != getOppositeDirection(ghost.getDirection()) &&
                    !isCollisionForGhost(ghost, direction)) {
                availableDirections++;
                if (availableDirections > 1) {
                    return true; // Frühes Beenden, sobald mehr als eine Richtung verfügbar ist
                }
            }
        }

        return false; // Keine Kreuzung, wenn weniger als zwei Richtungen verfügbar sind
    }

    /**
     * Wählt an einer Kreuzung eine neue Richtung für den Geist aus.
     * Zuerst wird überprüft, ob die Fortsetzung in der aktuellen Richtung möglich ist, ohne eine Kollision zu riskieren.
     * Es besteht eine 30%ige Chance, dass der Geist in seiner aktuellen Richtung weitergeht. Andernfalls
     * werden andere mögliche Richtungen, die keine Kollision verursachen, in Betracht gezogen und eine davon zufällig gewählt.
     *
     * @param ghost Der Geist, für den die Richtung bestimmt wird.
     * @return Die gewählte Richtung, in die der Geist gehen soll.
     */
    private ACTION chooseDirectionAtIntersection(Ghost ghost) {
        List<ACTION> possibleDirections = new ArrayList<>();
        List<ACTION> movementDirections = Arrays.asList(
                ACTION.MOVE_UP, ACTION.MOVE_DOWN, ACTION.MOVE_LEFT, ACTION.MOVE_RIGHT);

        // Füge die aktuelle Richtung als Option hinzu, wenn keine Kollision vorliegt
        if (!isCollisionForGhost(ghost, ghost.getDirection())) {
            possibleDirections.add(ghost.getDirection());
        }

        for (ACTION direction : movementDirections) {
            if (direction != getOppositeDirection(ghost.getDirection()) &&
                    !isCollisionForGhost(ghost, direction)) {
                possibleDirections.add(direction);
            }
        }

        if (!possibleDirections.isEmpty()) {
            // Entscheiden, ob der Geist in der aktuellen Richtung weitergeht oder eine neue Richtung wählt
            return random.nextInt(100) < 30 && possibleDirections.contains(ghost.getDirection()) ?
                    ghost.getDirection() :
                    possibleDirections.get(random.nextInt(possibleDirections.size()));
        }
        return ghost.getDirection();
    }

    /**
     * Entscheidet zufällig, ob ein Geist sich umdrehen soll.
     * Die Entscheidung basiert auf einem Zufallswert: Es gibt eine 0,4% Chance (4 in 1000)
     *
     * @return true, wenn der Geist sich umdrehen soll, sonst false.
     */
    private boolean shouldTurnAround() {
        return new Random().nextInt(1000) < 4;
    }


    /**
     * Bewegt einen Geist in die angegebene Richtung.
     * Diese Methode ändert die Position des Geistes basierend auf der übergebenen Richtung.
     *
     * @param ghost Der Geist, der bewegt werden soll.
     * @param direction Die Richtung, in die der Geist bewegt werden soll.
     */
    private void moveGhost(Ghost ghost, ACTION direction) {
        if (ghost == null || direction == null) {
            throw new IllegalArgumentException("Geist und Richtung dürfen nicht null sein.");
        }
        switch (direction) {
            case MOVE_UP:
                ghost.moveUp();
                break;
            case MOVE_DOWN:
                ghost.moveDown();
                break;
            case MOVE_LEFT:
                ghost.moveLeft();
                break;
            case MOVE_RIGHT:
                ghost.moveRight();
                break;
        }
    }

    /**
     * Wählt eine zufällige Richtung für einen Geist aus, unter Berücksichtigung möglicher Kollisionen und der aktuellen Richtung.
     * Richtungen, die zu einer Kollision führen oder direkt entgegengesetzt zur aktuellen Richtung sind, werden ausgeschlossen.
     * Falls keine Richtung ohne Kollision möglich ist, wird die entgegengesetzte Richtung zur aktuellen Richtung gewählt.
     *
     * @param currentDirection Die aktuelle Bewegungsrichtung des Geistes.
     * @param lghost Der Geist, für den die Richtung bestimmt wird.
     * @return Die gewählte ACTION, die die neue Richtung des Geistes angibt.
     */
    private ACTION getRandomDirection(ACTION currentDirection, Ghost lghost) {
        List<ACTION> possibleDirections = new ArrayList<>(Arrays.asList(
                ACTION.MOVE_UP, ACTION.MOVE_DOWN, ACTION.MOVE_LEFT, ACTION.MOVE_RIGHT));

        removeInvalidDirections(possibleDirections, currentDirection, lghost);

        if (!possibleDirections.isEmpty()) {
            return possibleDirections.get(random.nextInt(possibleDirections.size()));
        }

        return getOppositeDirection(currentDirection);
    }

    /**
     * Entfernt ungültige Bewegungsrichtungen aus der Liste der möglichen Richtungen für einen Geist.
     * Eine Richtung gilt als ungültig, wenn sie zu einer Kollision mit einem Hindernis führen würde
     * oder wenn sie direkt entgegengesetzt zur aktuellen Bewegungsrichtung des Geistes ist.
     *
     * @param possibleDirections Die Liste der möglichen Bewegungsrichtungen.
     * @param currentDirection Die aktuelle Bewegungsrichtung des Geistes.
     * @param lghost Der Geist, für den die Bewegungsrichtungen überprüft werden.
     */
    private void removeInvalidDirections(List<ACTION> possibleDirections, ACTION currentDirection, Ghost lghost) {
        if (possibleDirections == null || lghost == null) {
            throw new IllegalArgumentException("Die Liste der möglichen Richtungen und der Geist dürfen nicht null sein.");
        }

        possibleDirections.removeIf(direction ->
                isCollisionForGhost(lghost, direction) || direction == getOppositeDirection(currentDirection)
        );
    }


    /**
     * Ermittelt die entgegengesetzte Bewegungsrichtung zu einer gegebenen Richtung.
     * Diese Methode wird verwendet, um die Richtung zu bestimmen, in die sich ein Charakter umdrehen sollte.
     * Zum Beispiel ist die entgegengesetzte Richtung zu MOVE_UP MOVE_DOWN.
     *
     * @param direction Die aktuelle Bewegungsrichtung.
     * @return Die entgegengesetzte Bewegungsrichtung. Gibt MOVE_NONE zurück, falls die übergebene Richtung ungültig ist.
     */
    private ACTION getOppositeDirection(ACTION direction) {
        return switch (direction) {
            case MOVE_UP -> ACTION.MOVE_DOWN;
            case MOVE_DOWN -> ACTION.MOVE_UP;
            case MOVE_LEFT -> ACTION.MOVE_RIGHT;
            case MOVE_RIGHT -> ACTION.MOVE_LEFT;
            default -> ACTION.MOVE_NONE;
        };
    }

    /**
     * Prüft, ob ein Geist bei einer Bewegung in eine bestimmte Richtung mit einem Hindernis kollidieren würde.
     * Berechnet die zukünftige Position des Geistes basierend auf seiner aktuellen Geschwindigkeit und Richtung.
     * Überprüft, ob an dieser Position ein Hindernis oder ein Teleport ist.
     *
     * @param ghost Der Geist, dessen Kollision überprüft wird.
     * @param direction Die Richtung, in die der Geist sich bewegen soll.
     * @return true, wenn eine Kollision erkannt wird, sonst false.
     */
    private boolean isCollisionForGhost(Ghost ghost, ACTION direction) {
        if (ghost == null || direction == null) {
            throw new IllegalArgumentException("Geist und Richtung dürfen nicht null sein.");
        }

        int cellSize = maze.getCellSize();
        int speed = ghost.getSpeed();

        Point2D futurePosition = calculateFuturePosition(ghost.getX(), ghost.getY(), speed, direction);

        // Berechnen der vier Ecken um den Geist
        Point2D[] corners = new Point2D[] {
                new Point2D(futurePosition.getX() + OFFSET, futurePosition.getY() + OFFSET), // Oben links
                new Point2D(futurePosition.getX() + cellSize - OFFSET, futurePosition.getY() + OFFSET), // Oben rechts
                new Point2D(futurePosition.getX() + OFFSET, futurePosition.getY() + cellSize - OFFSET), // Unten links
                new Point2D(futurePosition.getX() + cellSize - OFFSET, futurePosition.getY() + cellSize - OFFSET) // Unten rechts
        };

        for (Point2D corner : corners) {
            int gridX = (int) (corner.getX()) / cellSize;
            int gridY = (int) (corner.getY()) / cellSize;

            if (maze.isWallOrTeleportForGhost(gridX, gridY)) {
                return true; // Kollision erkannt
            }
        }

        return false; // Keine Kollision
    }


    /**
     * Berechnet die zukünftige Position basierend auf der aktuellen Position, Geschwindigkeit und Bewegungsrichtung.
     *
     * @param x Die aktuelle X-Position.
     * @param y Die aktuelle Y-Position.
     * @param speed Die Geschwindigkeit des Geistes.
     * @param direction Die Bewegungsrichtung.
     * @return Die berechnete zukünftige Position.
     */
    private Point2D calculateFuturePosition(int x, int y, int speed, ACTION direction) {
        switch (direction) {
            case MOVE_UP:
                return new Point2D(x, y - speed);
            case MOVE_DOWN:
                return new Point2D(x, y + speed);
            case MOVE_LEFT:
                return new Point2D(x - speed, y);
            case MOVE_RIGHT:
                return new Point2D(x + speed, y);
            default:
                return new Point2D(x, y);
        }
    }

    /**
     * Setzt einen verwundbaren Geist nach einer Kollision mit Pac-Man zurück.
     * Der Geist wird zum Startpunkt zurückgesetzt, ist nicht mehr verwundbar und wird ins "Gefängnis" geschickt.
     * Zusätzlich wird der Timer für die nächste Geisterfreilassung zurückgesetzt.
     *
     * @param ghost Der verwundbare Geist, der zurückgesetzt werden soll.
     */
    private void resetVulnerableGhost(Ghost ghost) {
        Point2D ghostStart = maze.findGhostStart(ghost.getLetter());
        ghost.setX((int) ghostStart.getX());
        ghost.setY((int) ghostStart.getY());
        ghost.setIsInJail(true);
        ghost.setVulnerable(false);
        nextGhostReleaseTime = System.currentTimeMillis() + GHOST_RELEASE_INTERVAL;
    }

    /**
     * Behandelt die Kollision zwischen Pac-Man und einem nicht-verwundbaren Geist.
     * In der Regel verliert Pac-Man dabei ein Leben. Das Spiel wird daraufhin zurückgesetzt,
     * und alle Bewegungsrichtungen werden auf MOVE_NONE gesetzt.
     */
    private void handleCollisionWithInvulnerableGhost() {
        loseLife();
        resetPacmanAndGhosts();
        lastDirection = ACTION.MOVE_NONE;
        nextDirection = ACTION.MOVE_NONE;
    }

    /**
     * Setzt Pac-Man und alle Geister auf ihre jeweiligen Startpositionen zurück.
     * Diese Methode wird aufgerufen, wenn eine Kollision zwischen Pac-Man und einem nicht-verwundbaren Geist auftritt.
     * Sie setzt die Position von Pac-Man zurück, initialisiert seine Bewegungsrichtung neu und
     * setzt jeden Geist auf seine Anfangsposition, macht ihn nicht mehr verwundbar und setzt ihn ins "Gefängnis".
     */
    private void resetPacmanAndGhosts() {

        // Setze Pac-Man zurück
        Point2D pacmanStart = maze.findPacmanStart();
        pacman.setX((int) pacmanStart.getX());
        pacman.setY((int) pacmanStart.getY());
        setLastDirection(ACTION.MOVE_NONE);
        setNextDirection(ACTION.MOVE_NONE);

        // Setze jeden Geist zurück
        for (Ghost ghost : ghosts) {
            Point2D ghostStart = maze.findGhostStart(ghost.getLetter());
            ghost.setX((int) ghostStart.getX());
            ghost.setY((int) ghostStart.getY());
            ghost.setIsInJail(true);
            ghost.setVulnerable(false);
        }
    }

    /**
     * Teleportiert Pac-Man zum gegenüberliegenden Rand des Labyrinths.
     * @param pacmanPosition Die aktuelle Position von Pac-Man.
     * @param grid Das Labyrinth-Grid.
     * @param cellSize Die Größe einer Zelle im Labyrinth.
     */
    private void teleportPacman(Point2D pacmanPosition, char[][] grid, int cellSize) {
        if (pacmanPosition.getX() == 0) { // Linker Rand
            pacman.setX((grid[0].length - 2) * cellSize); // Teleportieren zum rechten Rand
        } else if (pacmanPosition.getX() == grid[0].length - 1) { // Rechter Rand
            pacman.setX(cellSize); // Teleportieren zum linken Rand
        }
    }

    /**
     * Überprüft, ob eine Kollision zwischen Pac-Man und einer Wand bei einer bestimmten Bewegungsrichtung auftritt.
     * Berechnet die zukünftige Position von Pac-Man basierend auf seiner aktuellen Geschwindigkeit und Richtung,
     * und prüft, ob an dieser Position eine Wand im Labyrinth ist.
     *
     * @param action Die geplante Bewegungsrichtung von Pac-Man.
     * @return true, wenn eine Kollision auftritt, sonst false.
     */
    private boolean isCollision(ACTION action) {
        int cellSize = maze.getCellSize();
        int speed = pacman.getSpeed();
        int offset = cellSize / 4; // Ein Viertel der Zellengröße als Versatz

        // Bestimmen der zukünftigen Position basierend auf der Aktion
        Point2D futurePosition = getFuturePosition(action, pacman.getX(), pacman.getY(), speed);

        // Berechnen der vier Ecken um Pac-Man
        Point2D[] corners = getCorners(futurePosition, cellSize, offset);

        // Überprüfen jeder Ecke auf Kollision
        return checkCornersForCollision(corners, cellSize);
    }

    private Point2D getFuturePosition(ACTION action, int x, int y, int speed) {
        switch (action) {
            case MOVE_UP:    return new Point2D(x, y - speed);
            case MOVE_DOWN:  return new Point2D(x, y + speed);
            case MOVE_LEFT:  return new Point2D(x - speed, y);
            case MOVE_RIGHT: return new Point2D(x + speed, y);
            default:         return new Point2D(x, y);
        }
    }

    private Point2D[] getCorners(Point2D position, int cellSize, int offset) {
        int x = (int) position.getX();
        int y = (int) position.getY();
        return new Point2D[]{
                new Point2D(x + offset, y + offset), // Oben links
                new Point2D(x + cellSize - offset, y + offset), // Oben rechts
                new Point2D(x + offset, y + cellSize - offset), // Unten links
                new Point2D(x + cellSize - offset, y + cellSize - offset) // Unten rechts
        };
    }

    private boolean checkCornersForCollision(Point2D[] corners, int cellSize) {
        for (Point2D corner : corners) {
            int gridX = (int) (corner.getX() / cellSize);
            int gridY = (int) (corner.getY() / cellSize);

            if (maze.isWall(gridX, gridY)) {
                return true; // Kollision erkannt
            }
        }
        return false; // Keine Kollision
    }

    /**
     * Berechnet die Position von Pac-Man im Labyrinth-Gitter basierend auf seiner aktuellen Pixelposition.
     * Diese Methode wandelt die Pixelkoordinaten von Pac-Man in Gitterkoordinaten um, indem sie die Pixelposition
     * durch die Größe der Zellen im Labyrinth teilt.
     *
     * @return Ein Point-Objekt, das die Gitterposition von Pac-Man repräsentiert.
     */
    public Point2D getPacmanGridPosition() {
        int cellSize = maze.getCellSize();

        // Stellen Sie sicher, dass cellSize nicht Null ist, um eine Division durch Null zu vermeiden
        if (cellSize == 0) {
            throw new IllegalStateException("Zellengröße des Labyrinths ist 0. Kann Gitterposition nicht berechnen.");
        }

        int adjustedX = pacman.getX() / cellSize;
        int adjustedY = pacman.getY() / cellSize;

        return new Point2D(adjustedX, adjustedY);
    }
    /**
     * Bewegt Pac-Man in die angegebene Richtung und sammelt Münzen, falls vorhanden.
     * Diese Methode aktualisiert die Position von Pac-Man basierend auf der angegebenen Aktion.
     * Anschließend wird überprüft, ob Pac-Man eine Münze sammelt.
     *
     * @param action Die Bewegungsrichtung, in die Pac-Man bewegt werden soll.
     */
    private synchronized void movePacman(ACTION action) {
        switch (action) {
            case MOVE_UP:
                movePacmanUp();
                break;
            case MOVE_DOWN:
                movePacmanDown();
                break;
            case MOVE_LEFT:
                movePacmanLeft();
                break;
            case MOVE_RIGHT:
                movePacmanRight();
                break;
        }

        collectCoin();
    }

    /**
     * Überprüft, ob Pac-Man eine Münze an seiner aktuellen Position eingesammelt hat und aktualisiert den Spielstand.
     * Entfernt die Münze aus dem Labyrinth und erhöht den Punktestand sowie die Anzahl der gesammelten Münzen.
     * Überprüft zudem, ob das aktuelle Level abgeschlossen ist.
     */
    private void collectCoin() {
        Point2D pacmanGridPosition = getPacmanGridPosition();
        char[][] grid = maze.getGrid();

        // Überprüfen, ob sich an der Position von Pac-Man eine Münze befindet
        if (pacmanGridPosition.getY() >= 0 && pacmanGridPosition.getY() < grid.length &&
                pacmanGridPosition.getX() >= 0 && pacmanGridPosition.getX() < grid[(int) pacmanGridPosition.getY()].length &&
                grid[(int) pacmanGridPosition.getY()][(int) pacmanGridPosition.getX()] == '.') {

            grid[(int) pacmanGridPosition.getY()][(int) pacmanGridPosition.getX()] = ' '; // Münze entfernen
            increaseScore(20); // Punktestand um 20 Punkte erhöhen
            collectedDots++; // Anzahl der gesammelten Münzen erhöhen
            checkLevelCompletion(); // Überprüfen, ob das Level abgeschlossen ist
        }
    }


    /**
     * Bewegt Pac-Man nach oben, wenn keine Kollision auftritt.
     */
    private void movePacmanUp() {
        if (!isCollision(ACTION.MOVE_UP)) {
            pacman.moveUp();
        }
    }

    /**
     * Bewegt Pac-Man nach unten, wenn keine Kollision auftritt.
     */
    private void movePacmanDown() {
        if (!isCollision(ACTION.MOVE_DOWN)) {
            pacman.moveDown();
        }
    }

    /**
     * Bewegt Pac-Man nach links, wenn keine Kollision auftritt.
     */
    private void movePacmanLeft() {
        if (!isCollision(ACTION.MOVE_LEFT)) {
            pacman.moveLeft();
        }
    }

    /**
     * Bewegt Pac-Man nach rechts, wenn keine Kollision auftritt.
     */
    private void movePacmanRight() {
        if (!isCollision(ACTION.MOVE_RIGHT)) {
            pacman.moveRight();
        }
    }

    public void setLastDirection(ACTION direction) {
        this.lastDirection = direction;
    }
    public void setNextDirection(ACTION direction) {
        this.nextDirection = direction;
    }
    public void increaseScore(int amount) {
        score += amount;
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameOver();
        }
    }

    // Getter-Methoden
    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

}
