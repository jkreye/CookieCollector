module com.pacman.pacmanjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.pacman.pacmanjavafx to javafx.fxml;
    exports com.pacman.pacmanjavafx;
}