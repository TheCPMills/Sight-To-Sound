package main;
 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.*;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;

// colordesigner.io
 
public class GUI extends Application {
    static File selectedImage;
    static File selectedDirectory;
    // launch the application
    public void start(Stage stage) {
        try {
            stage.setTitle("FileChooser");

            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg, *gif)", "*.png", "*.jpg", "*.jpeg", "*.gif");
            fileChooser.getExtensionFilters().add(extFilter);
 
            DirectoryChooser directoryChooser = new DirectoryChooser();

            Button convertButton = new Button("Convert");
            convertButton.setDisable(true);
 
            Label imageLabel = new Label("no image selected");
            Button imageButton = new Button("Select Image");

            Label directoryLabel = new Label("no location selected");
            Button directoryButton = new Button("Choose Export Location");
 
            
            EventHandler<ActionEvent> imageEvent = new EventHandler<ActionEvent>() {
 
                public void handle(ActionEvent e) {
                    selectedImage = fileChooser.showOpenDialog(stage);
 
                    if (selectedImage != null) {
                        imageLabel.setText(selectedImage.getAbsolutePath());
                        imageButton.setTextFill(Color.color(0.8705882353, 0.8823529412, 0.9176470588));
                        imageLabel.setTextFill(Color.color(0.85098039215, 0.79607843137, 0.61960784313));
                        fileChooser.setInitialDirectory(new File(selectedImage.getAbsolutePath().substring(0, selectedImage.getAbsolutePath().lastIndexOf("/"))));
                    }
                    allowConversion(imageLabel, directoryLabel, convertButton);
                }
            };
            imageButton.setOnAction(imageEvent);
 
            EventHandler<ActionEvent> directoryEvent = new EventHandler<ActionEvent>() {
 
                public void handle(ActionEvent e) {
                    selectedDirectory = directoryChooser.showDialog(stage);
 
                    if (selectedDirectory != null) {
                        directoryLabel.setText(selectedDirectory.getAbsolutePath());
                        directoryButton.setTextFill(Color.color(0.8705882353, 0.8823529412, 0.9176470588));
                        directoryLabel.setTextFill(Color.color(0.85098039215, 0.79607843137, 0.61960784313));
                        directoryChooser.setInitialDirectory(new File(selectedDirectory.getAbsolutePath()));
                    }
                    allowConversion(imageLabel, directoryLabel, convertButton);
                }
            };
            directoryButton.setOnAction(directoryEvent);

            EventHandler<ActionEvent> convertEvent = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    Algorithm.startConversion(selectedImage.getAbsolutePath(), selectedDirectory.getAbsolutePath());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    stage.close();
                }
            };
            convertButton.setOnAction(convertEvent);
 
            Pane scene = new Pane();
 
            imageButton.setLayoutX(10);
            imageButton.setLayoutY(10);
            imageButton.setTextFill(Color.color(0.8705882353, 0.8823529412, 0.9176470588)); 
            imageButton.setBackground(new Background(new BackgroundFill(Color.color(0.3647058824, 0.4117647059, 0.568627451), new CornerRadii(5), Insets.EMPTY)));  
 
            imageLabel.setLayoutX(102.25);
            imageLabel.setLayoutY(13.75);
            imageLabel.setTextFill(Color.color(0.85098039215, 0.79607843137, 0.61960784313)); 
 
            directoryButton.setLayoutX(10);
            directoryButton.setLayoutY(45);
            directoryButton.setTextFill(Color.color(0.8705882353, 0.8823529412, 0.9176470588)); 
            directoryButton.setBackground(new Background(new BackgroundFill(Color.color(0.3647058824, 0.4117647059, 0.568627451), new CornerRadii(5), Insets.EMPTY)));  
 
            directoryLabel.setLayoutX(162.5);
            directoryLabel.setLayoutY(48.75);
            directoryLabel.setTextFill(Color.color(0.85098039215, 0.79607843137, 0.61960784313));
 
            convertButton.layoutXProperty().bind(scene.widthProperty().subtract(convertButton.widthProperty()).divide(2));
            convertButton.setLayoutY(85);
            
            scene.getChildren().add(imageButton);
            scene.getChildren().add(imageLabel);
            scene.getChildren().add(directoryButton);
            scene.getChildren().add(directoryLabel);
            scene.getChildren().add(convertButton);
 
            scene.setBackground(new Background(new BackgroundFill(Color.color(0.15686274509, 0.17647058823, 0.2431372549), CornerRadii.EMPTY, Insets.EMPTY)));
            
            stage.setScene(new Scene(scene, 600, 130));
            stage.setResizable(false);
            stage.show();
        }
 
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void allowConversion(Label imageLabel, Label directoryLabel, Button convertButton) {
        String i = imageLabel.getText();
        String d = directoryLabel.getText();

        if (i.equals("no image selected") && d.equals("no location selected")) {
            imageLabel.setTextFill(Color.color(0.6862745098, 0.00392156862, 0.00392156862));
            directoryLabel.setTextFill(Color.color(0.6862745098, 0.00392156862, 0.00392156862));
        } else if (i.equals("no image selected")) {
            imageLabel.setTextFill(Color.color(0.6862745098, 0.00392156862, 0.00392156862));
        } else if (d.equals("no location selected")) {
            directoryLabel.setTextFill(Color.color(0.6862745098, 0.00392156862, 0.00392156862));
        } else {
            convertButton.setDisable(false);
        }
    }

    public static void main(String args[]) {
        // launch the application
        launch(args);
    }
}