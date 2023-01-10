// import org.jfugue.pattern.Atom;
// import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
// import org.jfugue.realtime.RealtimePlayer;
// import org.jfugue.theory.Note;

import javafx.application.*;
// import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.*;
import javafx.stage.*;
import javafx.embed.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;

import util.*;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // create a FileChooser
        FileChooser fileChooser = new FileChooser();

        // let file chooser to open at Desktop
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/OneDrive/Desktop"));

        fileChooser.setTitle("Open Resource File - SightToSound V2.0.1");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

        // show open file dialog
        File imageFile = fileChooser.showOpenDialog(stage);

        // load image and create Song object
        Song song = new Song(ImageIO.read(imageFile));

        // replace image with sliced image
        BufferedImage image = song.getSlicedImage();

        // draw the image to the scene
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight()); // create a canvas
        GraphicsContext gc = canvas.getGraphicsContext2D(); // get the graphics context of the canvas
        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0); // draw the image
        Scene scene = new Scene(new Group(canvas));

        // setup stage
        stage.setScene(scene);
        stage.setTitle("SightToSound V2.0.1");
        stage.initStyle(StageStyle.UNDECORATED); // remove window decorations
        stage.show();

        // if ESC is pressed, terminate the program
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });

        // if the GUI is closed, terminate the program
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });

        // draw sections
        for (int column = 1; column < song.columns; column++) {
            int xValue = column * song.sliceWidth;
            gc.strokeLine(xValue, 0, xValue, image.getHeight());
        }

        for (int row = 1; row < song.rows; row++) {
            int yValue = row * song.sliceHeight;
            gc.strokeLine(0, yValue, image.getWidth(), yValue);
        }

        // setup player
        Player realTimePlayer = new Player();

        // get BPM
        int bpm = song.getBPM();

        // add event handler to get mouse position every measure
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Point mouseCoords = MouseInfo.getPointerInfo().getLocation();

                int x = (int) mouseCoords.getX() - (int) stage.getX();
                int y = (int) mouseCoords.getY() - (int) stage.getY();

                // System.out.println("Mouse at (" + x + ", " + y + ")");

                if (MathOps.inBounds(x, y, 0, 0, image.getWidth(), image.getHeight())) {
                    String currentMeasure = song.getMeasure(x, y);
                    realTimePlayer.play("T" + bpm + " " + currentMeasure);

                    // System.out.println(currentMeasure);

                    if (realTimePlayer.getManagedPlayer().isPlaying()) {
                        System.out.println("Playing...");
                    }
                }
            }
        }, 0, 240000 / bpm - 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}