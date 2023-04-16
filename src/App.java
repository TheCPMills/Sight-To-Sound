// basic packages
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.vecmath.*;
import util.Date;
import util.SVGReader;

// JavaFX packages
import com.jfoenix.controls.*;
import eu.hansolo.applefx.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.knob.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.stage.*;

// audio packages
import jmusic.*;
import javax.sound.midi.*;
import javax.sound.sampled.*;
import org.jfugue.realtime.*;

// JSON packages
import org.json.simple.*;
import org.json.simple.parser.*;

public class App extends Application {
    /* Main Application Components */
    private static String uuid;
    private static Stage primaryStage;
    private static Scene prologueScene, music2ImageTutorialScene, music2ImageScene, responseConfidenceSceneM2I, image2MusicTutorialScene, image2MusicScene, responseConfidenceSceneI2M, epilogueScene;
    private static JSONObject levelsJSONObject;
    private static IntegerProperty currentPageProperty;
    private static int currentLevel;
    private static String levelParametersM2I, levelParametersI2M;
    private static Random rng;

    private static Date timestamp;

    /* Music to Image Components */
    private static MacosLabel titleLabelM2I = new MacosLabel("Adjust the visual appearence of the image below to match the musical piece.");
    private static SVGPath imageM2I = new SVGPath();
    private static Button colorChooserM2I = new Button();
    private static Group paletteM2I = new Group();
    private static ObjectProperty<Color> imageColorPropertyM2I = new SimpleObjectProperty<>();
    private static JFXButton playStopButtonM2I = new JFXButton();
    private static MacosButton submitButtonM2I = new MacosButton("Submit");
    private static SimpleAudioPlayer mediaPlayerM2I;
    private static int mediaPlayerStatusM2I = 1; // 0 = playing, 1 = paused, 2 = finished

    private static LinkedList<Point2d> splinePointsM2I = new LinkedList<>();
    private static LinkedList<Anchor> splineAnchorsM2I = new LinkedList<>();
    private static LinkedList<CubicCurve> splineCurvesM2I = new LinkedList<>();
    private static LinkedList<CurveInfo> splineCurvesInfoM2I = new LinkedList<>();
    private static Group splineGroupM2I = new Group();

    private static SVGPath audioPlayerBoxM2I = new SVGPath();
    private static Slider playheadM2I;
    private static Text playheadTextM2I;
    private static Text songDurationTextM2I;
    private static BufferedWriter responseWriterM2I;

    /* Image to Music Components */
    private static Label titleLabelI2M = new MacosLabel("Adjust the musical parameters to match the image below.");
    private static SVGPath imageI2M = new SVGPath();

    private static Text timbreClassLabelI2M = new Text("Timbre");
    private static Text modeLabelI2M = new Text("Mode");
    private static Text volumeLabelI2M = new Text("Volume");
    private static Text tempoLabelI2M = new Text(" Tempo");
    private static Text dynamicVariationLabelI2M = new Text("Dynamic\nVariation");
    private static Text rhythmicRegularityLabelI2M = new Text(" Rhythmic\nRegularity");

    private static Knob timbreClassKnobI2M = new Knob(0, 4, 0);
    private static Knob modeKnobI2M = new Knob(0, 3, 0);
    private static Knob volumeKnobI2M = new Knob(0, 92, 44);
    private static Knob tempoKnobI2M = new Knob(0, 180, 80);
    private static Knob dynamicVariationKnobI2M = new Knob(0, 3, 0);
    private static Knob rhythmicRegularityKnobI2M = new Knob(0, 5, 5);

    private static RealtimePlayer mediaPlayerI2M;
    private static Button playButtonI2M = new MacosButton("Play");
    private static Button submitButtonI2M = new MacosButton("Submit");

    private static int keyI2M;
    private static int[] instrumentsI2M = {25, 40, 71, 56, 11};
    private static int[][] modesI2M = {{-12, -12, -10, -8, -8, -7, -7, -5, -5, -3, -1, -1, 0, 0, 2, 4, 4, 5, 5, 7, 7, 9, 11, 11, 12, 12, 14, 16, 16, 17, 17, 19},
                                       {-12, -12, -10, -9, -9, -7, -7, -5, -5, -4, -2, -2, 0, 0, 2, 3, 3, 5, 5, 7, 7, 8, 10, 10, 12, 12, 14, 15, 15, 17, 17, 19},
                                       {-12, -12, -9, -9, -7, -7, -5, -5, -5, -2, -2, 0, 0, 0, 3, 3, 5, 5, 7, 7, 7, 10, 10, 12, 12, 12, 15, 15, 17, 17, 19, 19},
                                       {-12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19}};
    private static int[] pitchesI2M = new int[16];
    @SuppressWarnings("unchecked") private static LinkedList<NoteDuration>[] durationsI2M = new LinkedList[6];
    private static BufferedWriter responseWriterI2M;

    /* Main Application Functions */
    @Override public void start(Stage stage) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AnchorPane prologueRoot = new AnchorPane();
        AnchorPane music2ImageTutorialRoot = new AnchorPane();
        AnchorPane music2ImageRoot = new AnchorPane();
        AnchorPane responseConfidenceRootM2I = new AnchorPane();
        AnchorPane image2MusicTutorialRoot = new AnchorPane();
        AnchorPane image2MusicRoot = new AnchorPane();
        AnchorPane responseConfidenceRootI2M = new AnchorPane();
        AnchorPane epilogueRoot = new AnchorPane();

        JSONParser parser = new JSONParser();
        try {
            levelsJSONObject = (JSONObject) parser.parse(new FileReader("resources/levels.json"));
        } catch (IOException e) {
            System.out.println("Error: Could not load levels.json file.");
            System.exit(1);
            return;
        } catch (ParseException e) {
            System.out.println("Error: Could not parse levels.json file.");
            System.exit(1);
            return;
        }

        try {
            responseWriterM2I = new BufferedWriter(new FileWriter("assets/data/responseM2I.csv", true));
            responseWriterI2M = new BufferedWriter(new FileWriter("assets/data/responseI2M.csv", true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentPageProperty = new SimpleIntegerProperty(0);
        currentLevel = 1;
        rng = new Random(currentLevel * 0xdeaf);

        prologueScene = createPrologueScene(prologueRoot);
        music2ImageTutorialScene = createMusic2ImageTutorialScene(music2ImageTutorialRoot);
        music2ImageScene = createMusic2ImageScene(music2ImageRoot);
        responseConfidenceSceneM2I = createResponseConfidenceSceneM2I(responseConfidenceRootM2I);
        image2MusicTutorialScene = createImage2MusicTutorialScene(image2MusicTutorialRoot);
        image2MusicScene = createImage2MusicScene(image2MusicRoot);
        responseConfidenceSceneI2M = createResponseConfidenceSceneI2M(responseConfidenceRootI2M);
        epilogueScene = createEpilogueScene(epilogueRoot);

        primaryStage = stage;
        primaryStage.setScene(prologueScene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    private static Scene createPrologueScene(AnchorPane root) {
        root.setPrefSize(660, 625);

        Text welcomeText = new Text("Welcome to the Sight\nto Sound IQP Study!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        welcomeText.setFill(Color.BLACK);
        welcomeText.setLayoutX(100);
        welcomeText.setLayoutY(200);
        welcomeText.setTextAlignment(TextAlignment.CENTER);

        Text text = new Text("Please enter your UUID:");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        text.setFill(Color.BLACK);
        text.setLayoutX(100);
        text.setLayoutY(300);
        text.setTextAlignment(TextAlignment.CENTER);

        TextField textField = new TextField();
        textField.setLayoutX(100);
        textField.setLayoutY(330);
        textField.setPrefSize(460, 50);
        textField.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        MacosButton nextButton = new MacosButton("Continue");
        nextButton.setLayoutX(575);
        nextButton.setLayoutY(585);
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> {
            uuid = textField.getText();
            currentPageProperty.set(currentPageProperty.get() + 1);
            changeScene(music2ImageTutorialScene);
        });

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean properLength = newValue.length() == 8;

            Matcher matcher = Pattern.compile("^[0-9]*").matcher(newValue);
            boolean properCharacters;
            if (matcher.find()) {
                properCharacters = matcher.group(0).equals(newValue);
            } else {
                properCharacters = false;
            }
            
            if (properLength && properCharacters) {
                nextButton.setDisable(false);
            } else {
                nextButton.setDisable(true);
            }
        });

        root.getChildren().add(welcomeText);
        root.getChildren().add(text);
        root.getChildren().add(textField);
        root.getChildren().add(nextButton);

        return new Scene(root);
    }
    
    private static Scene createMusic2ImageTutorialScene(AnchorPane root) {
        root.setPrefSize(660, 625); 

        ImageView background = new ImageView(new Image("file:resources/tutorialM2I/Page1.png"));
        background.setLayoutX(0);
        background.setLayoutY(0);

        ImageView gif = new ImageView(new Image("file:resources/tutorialM2I/anchorPoint.gif"));
        gif.setLayoutX(201);
        gif.setLayoutY(339.4);
        gif.setVisible(false);

        ImageView secondGif = new ImageView(new Image("file:resources/tutorialM2I/stop.gif"));
        secondGif.setLayoutX(104);
        secondGif.setLayoutY(442.2);
        secondGif.setVisible(false);

        MacosButton nextButton = new MacosButton("Continue");
        nextButton.setLayoutX(575);
        nextButton.setLayoutY(585);
        nextButton.setOnAction(e -> {
            currentPageProperty.set(currentPageProperty.get() + 1);
        });

        MacosButton previousButton = new MacosButton("Back");
        previousButton.setLayoutX(25);
        previousButton.setLayoutY(585);
        previousButton.setOnAction(e -> {
            currentPageProperty.set(currentPageProperty.get() - 1);
        });

        currentPageProperty.addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    changeScene(prologueScene);
                    break;
                case 1:
                    background.setImage(new Image("file:resources/tutorialM2I/Page1.png"));
                    break;
                case 2:
                    background.setImage(new Image("file:resources/tutorialM2I/Page2.png"));
                    gif.setVisible(false);
                    break;
                case 3:
                    background.setImage(new Image("file:resources/tutorialM2I/Page3.png"));
                    gif.setImage(new Image("file:resources/tutorialM2I/anchorPoint.gif"));
                    gif.setVisible(true);
                    break;
                case 4:
                    background.setImage(new Image("file:resources/tutorialM2I/Page4.png"));
                    gif.setImage(new Image("file:resources/tutorialM2I/controlPoint.gif"));
                    gif.setLayoutX(201);
                    gif.setLayoutY(339.4);
                    break;
                case 5:
                    background.setImage(new Image("file:resources/tutorialM2I/Page5.png"));
                    gif.setImage(new Image("file:resources/tutorialM2I/addPoint.gif"));
                    gif.setLayoutX(113.5);
                    gif.setLayoutY(177.4);
                    break;
                case 6:
                    background.setImage(new Image("file:resources/tutorialM2I/Page6.png"));
                    gif.setImage(new Image("file:resources/tutorialM2I/removePoint.gif"));
                    gif.setLayoutX(104);
                    gif.setLayoutY(159.4);
                    break;
                case 7:
                    background.setImage(new Image("file:resources/tutorialM2I/Page7.png"));
                    gif.setImage(new Image("file:resources/tutorialM2I/colorPicker.gif"));
                    gif.setLayoutX(106.5);
                    gif.setLayoutY(271.8);
                    secondGif.setVisible(false);
                    break;
                case 8:
                    background.setImage(new Image("file:resources/tutorialM2I/Page8.png"));
                    gif.setVisible(true);
                    gif.setImage(new Image("file:resources/tutorialM2I/play.gif"));
                    gif.setLayoutX(104);
                    gif.setLayoutY(253.2);
                    secondGif.setVisible(true);
                    break;
                case 9:
                    background.setImage(new Image("file:resources/tutorialM2I/Page9.png"));
                    gif.setVisible(false);
                    secondGif.setVisible(false);
                    break;
                case 10:
                    timestamp = new Date();
                    try {
                        setLevelM2I(1);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                    }
                    changeScene(music2ImageScene);
                    break;
            }
        });

        root.getChildren().add(background);
        root.getChildren().add(gif);
        root.getChildren().add(secondGif);
        root.getChildren().add(nextButton);
        root.getChildren().add(previousButton);

        return new Scene(root);
    }

    private static Scene createMusic2ImageScene(AnchorPane root) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        root.setPrefSize(660, 625);

        titleLabelM2I.setLayoutX(25);
        titleLabelM2I.setLayoutY(14);
        titleLabelM2I.setStyle("-fx-font-size: 18px;");

        splineGroupM2I.setLayoutX(0);
        splineGroupM2I.setLayoutY(0);
       
        imageM2I.setLayoutX(0);
        imageM2I.setLayoutY(0);

        imageM2I.fillProperty().bind(imageColorPropertyM2I);

        mediaPlayerM2I = new SimpleAudioPlayer();

        audioPlayerBoxM2I.setLayoutX(182);
        audioPlayerBoxM2I.setLayoutY(537.2);
        audioPlayerBoxM2I.setContent("M 21.4 0 L 278.2 0 C 290.018893646 0 299.6 9.58110635364 299.6 21.4 C 299.6 33.2188936464 290.018893646 42.8 278.2 42.8 L 21.4 42.8 C 9.58110635364 42.8 0 33.2188936464 0 21.4 C 0 9.58110635364 9.58110635364 0 21.4 0 Z");
        audioPlayerBoxM2I.setFill(new Color(0.7686274509803922, 0.7686274509803922, 0.7686274509803922, 1));
        audioPlayerBoxM2I.setStroke(Color.GREY);

        imageColorPropertyM2I.addListener((observable, oldValue, newValue) -> {
            colorChooserM2I.setStyle("-fx-background-radius: 15; -fx-background-color: rgb(" + (int) (newValue.getRed() * 255) + ", " + (int) (newValue.getGreen() * 255) + ", " + (int) (newValue.getBlue() * 255) + "); -fx-border-radius: 15; -fx-border-color: black;");
            
            double brightness = newValue.getBrightness();
            double x = (brightness < 0.5) ? (1 - Math.sqrt(1 - Math.pow(2 * brightness, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * brightness + 2, 2)) + 1) / 2;

            for (int i = 0; i < 3; i++) {
                ((SVGPath) (paletteM2I.getChildren().get(i))).setFill(Color.hsb(0, 0, 1 - x));
            }
        });

        colorChooserM2I.setLayoutX(444.2);
        colorChooserM2I.setLayoutY(543.5);
        colorChooserM2I.setPrefSize(30, 30);
        colorChooserM2I.setStyle("-fx-background-radius: 15; -fx-background-color: white; -fx-border-radius: 15; -fx-border-color: black;");
        colorChooserM2I.setOnAction(e -> {
            submitButtonM2I.setDisable(true);
            CustomColorPicker colorPickerM2I = new CustomColorPicker(submitButtonM2I);
            colorPickerM2I.setCurrentColor(imageColorPropertyM2I.get());

            CustomMenuItem colorPickerMenuM2I = new CustomMenuItem(colorPickerM2I);
            colorPickerMenuM2I.setHideOnClick(false);
            imageColorPropertyM2I.bind(colorPickerM2I.customColorProperty());
            ContextMenu contextMenuM2I = new ContextMenu(colorPickerMenuM2I);
            contextMenuM2I.setOnHiding(t -> imageColorPropertyM2I.unbind());
            colorPickerM2I.setContextMenu(contextMenuM2I);
            contextMenuM2I.show(root, 1100, 442);
        });

        paletteM2I.setLayoutX(449.2);
        paletteM2I.setLayoutY(548.5);
        paletteM2I.getChildren().add(new SVGPath());
        paletteM2I.getChildren().add(new SVGPath());
        paletteM2I.getChildren().add(new SVGPath());
        ((SVGPath) (paletteM2I.getChildren().get(0))).setContent("M10.355 14.05c-.93-.734-2.418-.734-3.343 0-.918.743-.918 1.938 0 2.68.926.739 2.414.739 3.343 0 .915-.742.915-1.937 0-2.68m-3.546-1.062c.71-.883.472-2.008-.54-2.5-1.015-.5-2.414-.175-3.128.711-.715.883-.473 2.008.535 2.504 1.023.496 2.418.172 3.133-.715M5.84 8.754c.68-.766.515-1.785-.371-2.274-.887-.5-2.16-.28-2.844.485-.688.765-.52 1.785.363 2.273.89.5 2.168.282 2.852-.484m.629-2.723c.847.5 2.082.313 2.761-.418.665-.726.532-1.71-.324-2.207-.844-.496-2.082-.308-2.754.418-.672.727-.53 1.719.317 2.207M13.64 4.77c.755-.383.84-1.145.188-1.696-.652-.554-1.797-.683-2.558-.297-.75.391-.832 1.145-.188 1.696.656.554 1.805.683 2.559.297");
        ((SVGPath) (paletteM2I.getChildren().get(1))).setContent("M16.395 10.375c-.676-.2-1.258-.371-1.415-.77-.093-.23-.058-.57.098-1.007C16.754 7.03 19.266 4.5 19.2 3.48c-.031-.417-.687-1-1.117-1h-.031c-.469.032-1.176.645-1.938 1.477a3.448 3.448 0 0 0-.238-.55c-.797-1.372-2.547-2.157-4.8-2.157-1.75 0-3.7.48-5.637 1.395-2.49 1.171-4.346 3.617-4.732 6.23-.289 1.938.055 4.875 3.328 7.832 1.453 1.32 3.735 2.043 6.438 2.043 2.168 0 4.359-.48 6.027-1.324 1.855-.938 2.875-2.238 2.875-3.656 0-2.52-1.719-3.024-2.98-3.395m1.687-7.426c.2 0 .637.406.648.563.055.863-3.043 3.945-5.117 5.785l-.351-.309-.23-.195c1.542-2.305 4.175-5.79 5.05-5.844M12.27 9.707l.48-.547.535.477-.473.539-.542-.469m3.945 7.164c-1.578.797-3.668 1.254-5.742 1.254-2.547 0-4.68-.668-6.016-1.875-3.07-2.781-3.398-5.496-3.133-7.281.36-2.41 2.078-4.672 4.38-5.762 1.855-.875 3.714-1.332 5.37-1.332 2 0 3.586.688 4.258 1.844.148.25.242.515.297.793-1.399 1.656-2.816 3.77-3.094 4.176l-.867.996c-.453-.008-.906.191-1.055.671-.515 1.77-1.664 2.864-1.664 2.864s2.922-.574 3.477-1.235a1.79 1.79 0 0 0 .449-1.168l.906-1.039.531-.468c-.003.191.02.367.083.53.277.68 1.03.9 1.824 1.134 1.246.367 2.531.746 2.531 2.797 0 1.171-.902 2.273-2.535 3.101");
        ((SVGPath) (paletteM2I.getChildren().get(2))).setContent("M16.918 12.438c-.93-.739-2.422-.739-3.344 0-.922.738-.922 1.937 0 2.675.922.742 2.414.742 3.344 0 .914-.738.914-1.937 0-2.675m-.461 2.312c-.664.543-1.758.543-2.426 0-.676-.535-.676-1.406 0-1.95.668-.534 1.762-.534 2.426 0 .672.544.672 1.415 0 1.95");

        playStopButtonM2I.setLayoutX(189.8);
        playStopButtonM2I.setLayoutY(543.5);
        playStopButtonM2I.setPrefSize(30, 30);
        playStopButtonM2I.setStyle("-fx-background-color: black; -fx-shape: \"M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm-3 18v-12l10 6-10 6z\";");
        Circle playPauseButtonBackground = new Circle(204.8, 558.5, 15, Color.WHITE);
        playStopButtonM2I.setOnAction(e -> {
            switch (mediaPlayerStatusM2I) {
                case 0:
                    try {
                        mediaPlayerM2I.stop();
                        playheadM2I.setValue(0);
                        playheadTextM2I.setText("0:00");
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                    }
                    mediaPlayerStatusM2I = 1;
                    playStopButtonM2I.setStyle("-fx-background-color: black; -fx-shape: \"M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm-3 18v-12l10 6-10 6z\";");
                    break;
                case 1:
                    try {
                        mediaPlayerM2I.restart();
                        mediaPlayerM2I.loop();
                    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e1) {
                    }
                    mediaPlayerStatusM2I = 0;
                    playStopButtonM2I.setStyle("-fx-background-color: black; -fx-shape: \"M12 0c-6.627 0-12 5.373-12 12s5.373 12 12 12 12-5.373 12-12-5.373-12-12-12zm-1 17h-3v-10h3v10zm5 0h-3v-10h3v10z\";");
                    break;
                }
        });
        
        Rectangle greySquare = new Rectangle(412, 550, 35, 10);
        greySquare.setFill(new Color(0.7686274509803922, 0.7686274509803922, 0.7686274509803922, 1));

        playheadM2I = new JFXSlider();
        playheadM2I.setLayoutX(253.4);
        playheadM2I.setLayoutY(550.5);
        playheadM2I.setPrefSize(195, 10);
        playheadM2I.setDisable(true);
        playheadM2I.skinProperty().addListener(e -> {
            Pane thumb = (Pane) playheadM2I.lookup(".thumb");
            thumb.setScaleX(0.5);
            thumb.setScaleY(0.5);
            thumb.setStyle("-fx-background-color: transparent;");

            Pane track = (Pane) playheadM2I.lookup(".track");
            track.setStyle("-fx-background-color: #7D7D7D;");

            Pane coloredProgress = (Pane) playheadM2I.lookup(".colored-track");
            coloredProgress.setStyle("-fx-background-color: #101010;");
        });

        playheadTextM2I = new Text("0:00");
        playheadTextM2I.setLayoutX(229.8);
        playheadTextM2I.setLayoutY(561);
        playheadTextM2I.setFill(Color.BLACK);
        playheadTextM2I.setFont(Font.font("Arial", FontWeight.NORMAL, 10));

        songDurationTextM2I = new Text();
        songDurationTextM2I.setLayoutX(418.3);
        songDurationTextM2I.setLayoutY(561);
        songDurationTextM2I.setFill(Color.BLACK);
        songDurationTextM2I.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
 
        submitButtonM2I.setLayoutX(279.1);
        submitButtonM2I.setLayoutY(590);
        submitButtonM2I.setPrefSize(100, 30);
        submitButtonM2I.setOnAction(e -> {
            try {
                mediaPlayerM2I.stop();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                e1.printStackTrace();
            }
            changeScene(responseConfidenceSceneM2I);
        });

        root.getChildren().add(titleLabelM2I);
        root.getChildren().add(imageM2I);
        root.getChildren().add(splineGroupM2I);
        root.getChildren().add(audioPlayerBoxM2I);
        root.getChildren().add(playPauseButtonBackground);
        root.getChildren().add(playStopButtonM2I);
        root.getChildren().add(playheadM2I);
        root.getChildren().add(greySquare);
        root.getChildren().add(playheadTextM2I);
        root.getChildren().add(colorChooserM2I);
        root.getChildren().add(paletteM2I);
        root.getChildren().add(songDurationTextM2I);
        root.getChildren().add(submitButtonM2I);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(App.class.getResource("color.css").toExternalForm());
        return scene;
    }

    private static Scene createResponseConfidenceSceneM2I(AnchorPane root) {
        root.setPrefSize(660, 625);

        Label titleLabel = new MacosLabel("Assessment of Response");
        titleLabel.setLayoutX(229);
        titleLabel.setLayoutY(14);
        titleLabel.setStyle("-fx-font-size: 18px;");

        Label confidenceLabel = new MacosLabel("How confident are you in your response?");
        confidenceLabel.setLayoutX(50);
        confidenceLabel.setLayoutY(60);

        ToggleGroup confidenceToggleGroup = new ToggleGroup();

        RadioButton hiddenRadioButton = new MacosRadioButton();
        hiddenRadioButton.setPrefSize(16, 16);
        hiddenRadioButton.setLayoutX(0);
        hiddenRadioButton.setLayoutY(0);
        hiddenRadioButton.setVisible(true);
        hiddenRadioButton.setDisable(false);
        hiddenRadioButton.setToggleGroup(confidenceToggleGroup);

        RadioButton veryConfidentRadioButton = new MacosRadioButton();
        veryConfidentRadioButton.setPrefSize(16, 16);
        veryConfidentRadioButton.setLayoutX(80);
        veryConfidentRadioButton.setLayoutY(90);
        veryConfidentRadioButton.setToggleGroup(confidenceToggleGroup);
        veryConfidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton confidentRadioButton = new MacosRadioButton();
        confidentRadioButton.setPrefSize(16, 16);
        confidentRadioButton.setLayoutX(181);
        confidentRadioButton.setLayoutY(90);
        confidentRadioButton.setToggleGroup(confidenceToggleGroup);
        confidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton slightlyConfidentRadioButton = new MacosRadioButton();
        slightlyConfidentRadioButton.setPrefSize(16, 16);
        slightlyConfidentRadioButton.setLayoutX(282);
        slightlyConfidentRadioButton.setLayoutY(90);
        slightlyConfidentRadioButton.setToggleGroup(confidenceToggleGroup);
        slightlyConfidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton slightlyUnsureRadioButton = new MacosRadioButton();
        slightlyUnsureRadioButton.setPrefSize(16, 16);
        slightlyUnsureRadioButton.setLayoutX(383);
        slightlyUnsureRadioButton.setLayoutY(90);
        slightlyUnsureRadioButton.setToggleGroup(confidenceToggleGroup);
        slightlyUnsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton unsureRadioButton = new MacosRadioButton();
        unsureRadioButton.setPrefSize(16, 16);
        unsureRadioButton.setLayoutX(484);
        unsureRadioButton.setLayoutY(90);
        unsureRadioButton.setToggleGroup(confidenceToggleGroup);
        unsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton veryUnsureRadioButton = new MacosRadioButton();
        veryUnsureRadioButton.setPrefSize(16, 16);
        veryUnsureRadioButton.setLayoutX(585);
        veryUnsureRadioButton.setLayoutY(90);
        veryUnsureRadioButton.setToggleGroup(confidenceToggleGroup);
        veryUnsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        Label veryConfidentLabel = new MacosLabel("Very Confident");
        veryConfidentLabel.setPrefSize(85, 16);
        veryConfidentLabel.setLayoutX(45);
        veryConfidentLabel.setLayoutY(115);
        veryConfidentLabel.setAlignment(Pos.TOP_CENTER);

        Label confidentLabel = new MacosLabel("Confident");
        confidentLabel.setPrefSize(70, 16);
        confidentLabel.setLayoutX(153);
        confidentLabel.setLayoutY(115);
        confidentLabel.setAlignment(Pos.TOP_CENTER);

        Label slightlyConfidentLabel = new MacosLabel("Slightly Confident");
        slightlyConfidentLabel.setPrefSize(115, 16);
        slightlyConfidentLabel.setLayoutX(233);
        slightlyConfidentLabel.setLayoutY(115);
        slightlyConfidentLabel.setAlignment(Pos.TOP_CENTER);

        Label slightlyUnsureLabel = new MacosLabel("Slightly Unsure");
        slightlyUnsureLabel.setPrefSize(100, 16);
        slightlyUnsureLabel.setLayoutX(342);
        slightlyUnsureLabel.setLayoutY(115);
        slightlyUnsureLabel.setAlignment(Pos.TOP_CENTER);

        Label unsureLabel = new MacosLabel("Unsure");
        unsureLabel.setPrefSize(70, 16);
        unsureLabel.setLayoutX(457);
        unsureLabel.setLayoutY(115);
        unsureLabel.setAlignment(Pos.TOP_CENTER);

        Label veryUnsureLabel = new MacosLabel("Very Unsure");
        veryUnsureLabel.setPrefSize(70, 16);
        veryUnsureLabel.setLayoutX(555);
        veryUnsureLabel.setLayoutY(115);
        veryUnsureLabel.setAlignment(Pos.TOP_CENTER);

        Group confidenceGroup = new Group();
        confidenceGroup.getChildren().add(hiddenRadioButton);
        confidenceGroup.getChildren().add(veryConfidentRadioButton);
        confidenceGroup.getChildren().add(confidentRadioButton);
        confidenceGroup.getChildren().add(slightlyConfidentRadioButton);
        confidenceGroup.getChildren().add(slightlyUnsureRadioButton);
        confidenceGroup.getChildren().add(unsureRadioButton);
        confidenceGroup.getChildren().add(veryUnsureRadioButton);
        confidenceGroup.getChildren().add(veryConfidentLabel);
        confidenceGroup.getChildren().add(confidentLabel);
        confidenceGroup.getChildren().add(slightlyConfidentLabel);
        confidenceGroup.getChildren().add(slightlyUnsureLabel);
        confidenceGroup.getChildren().add(unsureLabel);
        confidenceGroup.getChildren().add(veryUnsureLabel);

        Label notesLabel = new MacosLabel("Is there anything you would like to note?");
        notesLabel.setLayoutX(50);
        notesLabel.setLayoutY(160);

        TextArea notesTextArea = new TextArea();
        notesTextArea.setPrefSize(573, 385);
        notesTextArea.setLayoutX(50);
        notesTextArea.setLayoutY(180);
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(",")) {
                notesTextArea.setText(oldValue);
            }
        });

        MacosButton nextButton = new MacosButton("Continue");
        nextButton.setLayoutX(575);
        nextButton.setLayoutY(585);
        nextButton.setDisable(true);
        confidenceToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (confidenceToggleGroup.getSelectedToggle() != null) {
                if (confidenceToggleGroup.getSelectedToggle() == hiddenRadioButton) {
                    hiddenRadioButton.setSelected(false);
                } else {
                    nextButton.setDisable(false);
                }
            }
        });
        nextButton.setOnAction(e -> {
            String confidenceLevel;
            if (confidenceToggleGroup.getSelectedToggle() == veryConfidentRadioButton) {
                confidenceLevel = "Very Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == confidentRadioButton) {
                confidenceLevel = "Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == slightlyConfidentRadioButton) {
                confidenceLevel = "Slightly Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == slightlyUnsureRadioButton) {
                confidenceLevel = "Slightly Unsure";
            } else if (confidenceToggleGroup.getSelectedToggle() == unsureRadioButton) {
                confidenceLevel = "Unsure";
            } else {
                confidenceLevel = "Very Unsure";
            }

            hiddenRadioButton.setVisible(true);

            currentLevel++;
            if (currentLevel == 16) {
                currentLevel = 1;
                try {
                    gatherResponseM2I();
                    responseWriterM2I.write(confidenceLevel + "," + notesTextArea.getText() + "\n");
                    responseWriterM2I.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                confidenceToggleGroup.selectToggle(null);
                notesTextArea.clear();
                nextButton.setDisable(true);
                currentPageProperty.set(currentPageProperty.get() + 1);
                changeScene(image2MusicTutorialScene);
            } else {
                gatherResponseM2I();
                try {
                    responseWriterM2I.write(confidenceLevel + "," + notesTextArea.getText() + "\n");
                    responseWriterM2I.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                confidenceToggleGroup.selectToggle(null);
                notesTextArea.clear();
                nextButton.setDisable(true);
                try {
                    timestamp = new Date();
                    setLevelM2I(currentLevel);
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                    e1.printStackTrace();
                }
                changeScene(music2ImageScene);
            }
        });

        root.getChildren().add(titleLabel);
        root.getChildren().add(confidenceLabel);
        root.getChildren().add(confidenceGroup);
        root.getChildren().add(notesLabel);
        root.getChildren().add(notesTextArea);
        root.getChildren().add(nextButton);

        return new Scene(root);
    }

    private static Scene createImage2MusicTutorialScene(AnchorPane root) {
        root.setPrefSize(660, 625);

        ImageView background = new ImageView(new Image("file:resources/tutorialI2M/Page1.png"));
        background.setLayoutX(0);
        background.setLayoutY(0);

        ImageView gif = new ImageView(new Image("file:resources/tutorialI2M/knob.gif"));
        gif.setLayoutX(180);
        gif.setLayoutY(338.9);
        gif.setVisible(false);

        MacosButton nextButton = new MacosButton("Continue");
        nextButton.setLayoutX(575);
        nextButton.setLayoutY(585);

        MacosButton previousButton = new MacosButton("Back");
        previousButton.setLayoutX(25);
        previousButton.setLayoutY(585);
        previousButton.setVisible(false);

        nextButton.setOnAction(e -> {
            currentPageProperty.set(currentPageProperty.get() + 1);
        });
        
        previousButton.setOnAction(e -> {
            currentPageProperty.set(currentPageProperty.get() - 1);
        });

        currentPageProperty.addListener((observable, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 11:
                    background.setImage(new Image("file:resources/tutorialI2M/Page1.png"));
                    previousButton.setVisible(false);
                    break;
                case 12:
                    background.setImage(new Image("file:resources/tutorialI2M/Page2.png"));
                    previousButton.setVisible(true);
                    gif.setVisible(false);
                    break;
                case 13:
                    background.setImage(new Image("file:resources/tutorialI2M/Page3.png"));
                    gif.setImage(new Image("file:resources/tutorialI2M/knob.gif"));
                    gif.setLayoutX(180);
                    gif.setLayoutY(338.9);
                    gif.setVisible(true);
                    break;
                case 14:
                    background.setImage(new Image("file:resources/tutorialI2M/Page4.png"));
                    gif.setImage(new Image("file:resources/tutorialI2M/play.gif"));
                    gif.setLayoutX(130);
                    gif.setLayoutY(189);
                    gif.setVisible(true);
                    break;
                case 15:
                    background.setImage(new Image("file:resources/tutorialI2M/Page5.png"));
                    gif.setVisible(false);
                    break;
                case 16:
                    timestamp = new Date();
                    setLevelI2M(1);
                    changeScene(image2MusicScene);
                    break;
            }
        });

        root.getChildren().add(background);
        root.getChildren().add(gif);
        root.getChildren().add(nextButton);
        root.getChildren().add(previousButton);

        return new Scene(root);
    }

    private static Scene createImage2MusicScene(AnchorPane root) {
        root.setPrefSize(660, 625);

        titleLabelI2M.setLayoutX(102.7);
        titleLabelI2M.setLayoutY(14);
        titleLabelI2M.setStyle("-fx-font-size: 18px;");
       
        imageI2M.setLayoutX(0);
        imageI2M.setLayoutY(50);
        imageI2M.setStrokeWidth(2);
        imageI2M.setStroke(Color.BLACK);
        imageI2M.setFill(Color.WHITE);
        imageI2M.setContent("M 25 50 L 635 50 L 635 520 L 25 520 Z");

        timbreClassLabelI2M.setLayoutX(34);
        timbreClassLabelI2M.setLayoutY(598);
        timbreClassLabelI2M.setStyle("-fx-font-size: 12px;");

        modeLabelI2M.setLayoutX(121);
        modeLabelI2M.setLayoutY(598);
        modeLabelI2M.setStyle("-fx-font-size: 12px;");

        volumeLabelI2M.setLayoutX(201);
        volumeLabelI2M.setLayoutY(598);
        volumeLabelI2M.setStyle("-fx-font-size: 12px;");

        tempoLabelI2M.setLayoutX(415);
        tempoLabelI2M.setLayoutY(598);
        tempoLabelI2M.setStyle("-fx-font-size: 12px;");

        dynamicVariationLabelI2M.setLayoutX(504);
        dynamicVariationLabelI2M.setLayoutY(598);
        dynamicVariationLabelI2M.setStyle("-fx-font-size: 12px;");

        rhythmicRegularityLabelI2M.setLayoutX(580);
        rhythmicRegularityLabelI2M.setLayoutY(598);
        rhythmicRegularityLabelI2M.setStyle("-fx-font-size: 12px;");

        timbreClassKnobI2M.setLayoutX(0);
        timbreClassKnobI2M.setLayoutY(508);
        timbreClassKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            timbreClassKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        modeKnobI2M.setLayoutX(85);
        modeKnobI2M.setLayoutY(508);
        modeKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            modeKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        volumeKnobI2M.setLayoutX(170);
        volumeKnobI2M.setLayoutY(508);
        volumeKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumeKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        tempoKnobI2M.setLayoutX(386);
        tempoKnobI2M.setLayoutY(508);
        tempoKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            tempoKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        dynamicVariationKnobI2M.setLayoutX(471);
        dynamicVariationKnobI2M.setLayoutY(508);
        dynamicVariationKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            dynamicVariationKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        rhythmicRegularityKnobI2M.setLayoutX(556);
        rhythmicRegularityKnobI2M.setLayoutY(508);
        rhythmicRegularityKnobI2M.valueProperty().addListener((observable, oldValue, newValue) -> {
            rhythmicRegularityKnobI2M.setValue(Math.round(newValue.doubleValue()));
        });

        playButtonI2M.setLayoutX(280);
        playButtonI2M.setLayoutY(540);
        playButtonI2M.setPrefSize(100, 30);
        playButtonI2M.setOnAction(e -> {
            playButtonI2M.setText("Playing...");
            playButtonI2M.setDisable(true);
            submitButtonI2M.setDisable(true);
        });
        playButtonI2M.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                try {
                    mediaPlayerI2M = new RealtimePlayer();
                } catch (MidiUnavailableException e) {
                }

                // create a new thread to play the music
                Thread musicThread = new Thread(() -> {
                    for (int i = 0; i < 16; i++) {
                        // get timbre class
                        int instrument = instrumentsI2M[(int) timbreClassKnobI2M.getValue()];
                        
                        // get mode
                        int[] scale = modesI2M[(int) modeKnobI2M.getValue()];

                        // get pitch
                        int pitch = scale[pitchesI2M[i]] + keyI2M;

                        // get velocity
                        int velocity = (int) (volumeKnobI2M.getValue() + 20);
                        int dynamicVariation = getDynamicVariationModifierI2M((int) dynamicVariationKnobI2M.getValue());
                        velocity = Math.min(112, Math.max(20, velocity + 13 * dynamicVariation));

                        // get duration
                        float duration = durationsI2M[(int) rhythmicRegularityKnobI2M.getValue()].get(i).getDuration();

                        // get tempo
                        int tempo = (int) tempoKnobI2M.getValue() + 25;

                        // play note
                        mediaPlayerI2M.play("T" + tempo + " &V0,L0,I" + instrument + "," + pitch + "/" + duration + "a" + velocity);

                        // create thread to sleep for duration
                        try {
                            Thread.sleep((long) (duration * 240000 / tempo));
                        } catch (InterruptedException e1) {
                        }
                    }
                });
                
                // create thread to wait for music to finish
                Thread waitThread = new Thread(() -> {
                    try {
                        musicThread.join();
                        Platform.runLater(() -> {
                            playButtonI2M.setText("Play");
                            playButtonI2M.setDisable(false);
                            submitButtonI2M.setDisable(false);
                            mediaPlayerI2M.close();
                        });
                    } catch (InterruptedException e1) {
                    }
                });

                // start threads
                musicThread.start();
                waitThread.start();
            }
        });

        submitButtonI2M.setLayoutX(280);
        submitButtonI2M.setLayoutY(575);
        submitButtonI2M.setPrefSize(100, 30);
        submitButtonI2M.setOnAction(e -> {
            changeScene(responseConfidenceSceneI2M);
        });

        root.getChildren().add(titleLabelI2M);
        root.getChildren().add(imageI2M);
        root.getChildren().add(timbreClassLabelI2M);
        root.getChildren().add(modeLabelI2M);
        root.getChildren().add(volumeLabelI2M);
        root.getChildren().add(tempoLabelI2M);
        root.getChildren().add(dynamicVariationLabelI2M);
        root.getChildren().add(rhythmicRegularityLabelI2M);
        root.getChildren().add(timbreClassKnobI2M);
        root.getChildren().add(modeKnobI2M);
        root.getChildren().add(volumeKnobI2M);
		root.getChildren().add(tempoKnobI2M);
        root.getChildren().add(dynamicVariationKnobI2M);
        root.getChildren().add(rhythmicRegularityKnobI2M);
        root.getChildren().add(playButtonI2M);
        root.getChildren().add(submitButtonI2M);

        return new Scene(root);
    }

    private static Scene createResponseConfidenceSceneI2M(AnchorPane root) {
        root.setPrefSize(660, 625);

        Label titleLabel = new MacosLabel("Assessment of Response");
        titleLabel.setLayoutX(229);
        titleLabel.setLayoutY(14);
        titleLabel.setStyle("-fx-font-size: 18px;");

        Label confidenceLabel = new MacosLabel("How confident are you in your response?");
        confidenceLabel.setLayoutX(50);
        confidenceLabel.setLayoutY(60);

        ToggleGroup confidenceToggleGroup = new ToggleGroup();

        RadioButton hiddenRadioButton = new MacosRadioButton();
        hiddenRadioButton.setPrefSize(16, 16);
        hiddenRadioButton.setLayoutX(0);
        hiddenRadioButton.setLayoutY(0);
        hiddenRadioButton.setVisible(true);
        hiddenRadioButton.setDisable(false);
        hiddenRadioButton.setToggleGroup(confidenceToggleGroup);

        RadioButton veryConfidentRadioButton = new MacosRadioButton();
        veryConfidentRadioButton.setPrefSize(16, 16);
        veryConfidentRadioButton.setLayoutX(80);
        veryConfidentRadioButton.setLayoutY(90);
        veryConfidentRadioButton.setToggleGroup(confidenceToggleGroup);
        veryConfidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton confidentRadioButton = new MacosRadioButton();
        confidentRadioButton.setPrefSize(16, 16);
        confidentRadioButton.setLayoutX(181);
        confidentRadioButton.setLayoutY(90);
        confidentRadioButton.setToggleGroup(confidenceToggleGroup);
        confidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton slightlyConfidentRadioButton = new MacosRadioButton();
        slightlyConfidentRadioButton.setPrefSize(16, 16);
        slightlyConfidentRadioButton.setLayoutX(282);
        slightlyConfidentRadioButton.setLayoutY(90);
        slightlyConfidentRadioButton.setToggleGroup(confidenceToggleGroup);
        slightlyConfidentRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton slightlyUnsureRadioButton = new MacosRadioButton();
        slightlyUnsureRadioButton.setPrefSize(16, 16);
        slightlyUnsureRadioButton.setLayoutX(383);
        slightlyUnsureRadioButton.setLayoutY(90);
        slightlyUnsureRadioButton.setToggleGroup(confidenceToggleGroup);
        slightlyUnsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton unsureRadioButton = new MacosRadioButton();
        unsureRadioButton.setPrefSize(16, 16);
        unsureRadioButton.setLayoutX(484);
        unsureRadioButton.setLayoutY(90);
        unsureRadioButton.setToggleGroup(confidenceToggleGroup);
        unsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        RadioButton veryUnsureRadioButton = new MacosRadioButton();
        veryUnsureRadioButton.setPrefSize(16, 16);
        veryUnsureRadioButton.setLayoutX(585);
        veryUnsureRadioButton.setLayoutY(90);
        veryUnsureRadioButton.setToggleGroup(confidenceToggleGroup);
        veryUnsureRadioButton.setOnMousePressed(e -> {
            hiddenRadioButton.setVisible(false);
        });

        Label veryConfidentLabel = new MacosLabel("Very Confident");
        veryConfidentLabel.setPrefSize(85, 16);
        veryConfidentLabel.setLayoutX(45);
        veryConfidentLabel.setLayoutY(115);
        veryConfidentLabel.setAlignment(Pos.TOP_CENTER);

        Label confidentLabel = new MacosLabel("Confident");
        confidentLabel.setPrefSize(70, 16);
        confidentLabel.setLayoutX(153);
        confidentLabel.setLayoutY(115);
        confidentLabel.setAlignment(Pos.TOP_CENTER);

        Label slightlyConfidentLabel = new MacosLabel("Slightly Confident");
        slightlyConfidentLabel.setPrefSize(115, 16);
        slightlyConfidentLabel.setLayoutX(233);
        slightlyConfidentLabel.setLayoutY(115);
        slightlyConfidentLabel.setAlignment(Pos.TOP_CENTER);

        Label slightlyUnsureLabel = new MacosLabel("Slightly Unsure");
        slightlyUnsureLabel.setPrefSize(100, 16);
        slightlyUnsureLabel.setLayoutX(342);
        slightlyUnsureLabel.setLayoutY(115);
        slightlyUnsureLabel.setAlignment(Pos.TOP_CENTER);

        Label unsureLabel = new MacosLabel("Unsure");
        unsureLabel.setPrefSize(70, 16);
        unsureLabel.setLayoutX(457);
        unsureLabel.setLayoutY(115);
        unsureLabel.setAlignment(Pos.TOP_CENTER);

        Label veryUnsureLabel = new MacosLabel("Very Unsure");
        veryUnsureLabel.setPrefSize(70, 16);
        veryUnsureLabel.setLayoutX(555);
        veryUnsureLabel.setLayoutY(115);
        veryUnsureLabel.setAlignment(Pos.TOP_CENTER);

        Group confidenceGroup = new Group();
        confidenceGroup.getChildren().add(hiddenRadioButton);
        confidenceGroup.getChildren().add(veryConfidentRadioButton);
        confidenceGroup.getChildren().add(confidentRadioButton);
        confidenceGroup.getChildren().add(slightlyConfidentRadioButton);
        confidenceGroup.getChildren().add(slightlyUnsureRadioButton);
        confidenceGroup.getChildren().add(unsureRadioButton);
        confidenceGroup.getChildren().add(veryUnsureRadioButton);
        confidenceGroup.getChildren().add(veryConfidentLabel);
        confidenceGroup.getChildren().add(confidentLabel);
        confidenceGroup.getChildren().add(slightlyConfidentLabel);
        confidenceGroup.getChildren().add(slightlyUnsureLabel);
        confidenceGroup.getChildren().add(unsureLabel);
        confidenceGroup.getChildren().add(veryUnsureLabel);

        Label notesLabel = new MacosLabel("Is there anything you would like to note?");
        notesLabel.setLayoutX(50);
        notesLabel.setLayoutY(160);

        TextArea notesTextArea = new TextArea();
        notesTextArea.setPrefSize(573, 385);
        notesTextArea.setLayoutX(50);
        notesTextArea.setLayoutY(180);
        notesTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains(",")) {
                notesTextArea.setText(oldValue);
            }
        });

        MacosButton nextButton = new MacosButton("Continue");
        nextButton.setLayoutX(575);
        nextButton.setLayoutY(585);
        nextButton.setDisable(true);
        confidenceToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (confidenceToggleGroup.getSelectedToggle() != null) {
                if (confidenceToggleGroup.getSelectedToggle() == hiddenRadioButton) {
                    hiddenRadioButton.setSelected(false);
                } else {
                    nextButton.setDisable(false);
                }
            }
        });
        nextButton.setOnAction(e -> {
            String confidenceLevel;
            if (confidenceToggleGroup.getSelectedToggle() == veryConfidentRadioButton) {
                confidenceLevel = "Very Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == confidentRadioButton) {
                confidenceLevel = "Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == slightlyConfidentRadioButton) {
                confidenceLevel = "Slightly Confident";
            } else if (confidenceToggleGroup.getSelectedToggle() == slightlyUnsureRadioButton) {
                confidenceLevel = "Slightly Unsure";
            } else if (confidenceToggleGroup.getSelectedToggle() == unsureRadioButton) {
                confidenceLevel = "Unsure";
            } else {
                confidenceLevel = "Very Unsure";
            }

            hiddenRadioButton.setVisible(true);

            currentLevel++;
            if (currentLevel == 16) {
                currentLevel = 1;
                try {
                    gatherResponseI2M();
                    responseWriterI2M.write(confidenceLevel + "," + notesTextArea.getText() + "\n");
                    responseWriterI2M.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                confidenceToggleGroup.selectToggle(null);
                notesTextArea.clear();
                nextButton.setDisable(true);
                currentPageProperty.set(currentPageProperty.get() + 1);
                changeScene(epilogueScene);
            } else {
                gatherResponseI2M();
                try {
                    responseWriterI2M.write(confidenceLevel + "," + notesTextArea.getText() + "\n");
                    responseWriterI2M.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                confidenceToggleGroup.selectToggle(null);
                notesTextArea.clear();
                nextButton.setDisable(true);
                timestamp = new Date();
                setLevelI2M(currentLevel);
                changeScene(image2MusicScene);
            }
        });

        root.getChildren().add(titleLabel);
        root.getChildren().add(confidenceLabel);
        root.getChildren().add(confidenceGroup);
        root.getChildren().add(notesLabel);
        root.getChildren().add(notesTextArea);
        root.getChildren().add(nextButton);

        return new Scene(root);
    }

    private static Scene createEpilogueScene(AnchorPane root) {
        root.setPrefSize(660, 625);

        Text text = new Text("Thank you for participating\nin our study!");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        text.setFill(Color.BLACK);
        text.setLayoutX(100);
        text.setLayoutY(300);

        MacosButton button = new MacosButton("Finish");
        button.setLayoutX(575);
        button.setLayoutY(575);
        button.setOnAction(e -> {
            primaryStage.close();
            System.exit(0);
        });

        root.getChildren().add(text);
        root.getChildren().add(button);

        return new Scene(root);
    }
    
    private static void changeScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    /* Music to Image Functions and Structures */
    private static CurveInfo createCurve(Anchor startPoint, Anchor endPoint) {
        double x1 = startPoint.getCenterX(), y1 = startPoint.getCenterY(), x2 = endPoint.getCenterX(), y2 = endPoint.getCenterY();
        CubicCurve curve = new CubicCurve();
        curve.setStartX(x1);
        curve.setStartY(y1);
        curve.setControlX1(x1 + (x2 - x1) / 3);
        curve.setControlY1(y1 + (y2 - y1) / 3);
        curve.setControlX2(x2 - (x2 - x1) / 3);
        curve.setControlY2(y2 - (y2 - y1) / 3);
        curve.setEndX(x2);
        curve.setEndY(y2);
        curve.setStroke(Color.BLACK);
        curve.setStrokeWidth(4);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setFill(Color.TRANSPARENT);
        curve.startXProperty().bind(startPoint.centerXProperty());
        curve.startYProperty().bind(startPoint.centerYProperty());
        curve.endXProperty().bind(endPoint.centerXProperty());
        curve.endYProperty().bind(endPoint.centerYProperty());
        ControlLine controlLine1 = new ControlLine(curve.controlX1Property(), curve.controlY1Property(), curve.startXProperty(), curve.startYProperty());
        ControlLine controlLine2 = new ControlLine(curve.controlX2Property(), curve.controlY2Property(), curve.endXProperty(), curve.endYProperty());
        Anchor controlPoint1 = new Anchor(Color.DARKGREY, curve.controlX1Property(), curve.controlY1Property(), 3);
        Anchor controlPoint2 = new Anchor(Color.DARKGREY, curve.controlX2Property(), curve.controlY2Property(), 3);

        CurveInfo curveInfo = new CurveInfo(curve, startPoint,endPoint, controlPoint1, controlPoint2, controlLine1, controlLine2);

        startPoint.curves[1] = curveInfo;
        endPoint.curves[0] = curveInfo;

        curve.setId("" + ((int) (Math.random() * 1000)));

        curve.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                splitCurve(curve);
            }
        });

        return curveInfo; 
    }

    private static void splitCurve(CubicCurve curve) {
        // get index of curve
        int curveIndex = splineCurvesM2I.indexOf(curve);

        // get index of start and end point
        int startAnchorIndex = curveIndex;
        int endAnchorIndex = (curveIndex + 1) % splineAnchorsM2I.size();

        // determine the halfway point
        Point2d halfwayPoint = getPoint(curve, 0.5);

        // get the value of the end point
        Anchor endAnchor = splineAnchorsM2I.get(endAnchorIndex);
        
        // remove the curve
        CurveInfo removedCurveInfo = splineCurvesInfoM2I.remove(curveIndex);
        splineCurvesM2I.remove(curve);
        removedCurveInfo.removeCurveFromGroup();

        // create a curve from the start point to halfway point
        Anchor halfwayPointAnchor = new Anchor(Color.BLACK, halfwayPoint.getX(), halfwayPoint.getY(), 5);
        splineAnchorsM2I.add(startAnchorIndex + 1, halfwayPointAnchor);
        CurveInfo firstHalfCurveInfo = createCurve(splineAnchorsM2I.get(startAnchorIndex), halfwayPointAnchor);
        splineCurvesInfoM2I.add(curveIndex, firstHalfCurveInfo);
        CubicCurve firstHalfCurve = firstHalfCurveInfo.curve;
        splineCurvesM2I.add(curveIndex, firstHalfCurve);
        firstHalfCurveInfo.addCurveToGroup();

        // create a curve from the halfway point to the end point
        CurveInfo secondHalfCurveInfo = createCurve(halfwayPointAnchor, endAnchor);
        splineCurvesInfoM2I.add(curveIndex + 1, secondHalfCurveInfo);
        CubicCurve secondHalfCurve = secondHalfCurveInfo.curve;
        splineCurvesM2I.add(curveIndex + 1, secondHalfCurve);
        secondHalfCurveInfo.addCurveToGroup();

        imageM2I.setContent(getPath());
    }

    private static void joinCurves(CubicCurve curve1, CubicCurve curve2) {
        // get index of curve
        int curve1Index = splineCurvesM2I.indexOf(curve1);

        // remove the curves
        CurveInfo curve1Info = splineCurvesInfoM2I.remove(curve1Index);
        splineCurvesM2I.remove(curve1);
        curve1Info.removeCurveFromGroup();
        CurveInfo curve2Info = splineCurvesInfoM2I.remove(curve1Index % splineCurvesM2I.size());
        splineCurvesM2I.remove(curve2);
        curve2Info.removeCurveFromGroup();

        // remove the middle point
        splineAnchorsM2I.remove(curve1Info.endPoint);

        // get the start and end points
        Anchor startPoint = curve1Info.startPoint;
        Anchor endPoint = curve2Info.endPoint;

        // create a curve from the start point to the end point
        int newCurveIndex = (curve1Index > splineCurvesM2I.size()) ?  splineCurvesM2I.size() : curve1Index;
        CurveInfo newCurveInfo = createCurve(startPoint, endPoint);
        splineCurvesInfoM2I.add(newCurveIndex, newCurveInfo);
        CubicCurve newCurve = newCurveInfo.curve;
        splineCurvesM2I.add(newCurveIndex, newCurve);
        newCurveInfo.addCurveToGroup();

        imageM2I.setContent(getPath());
    }

    private static void addPoint(Point2d point) {
        splinePointsM2I.add(point);
        
        switch (splinePointsM2I.size()) {
            case 1:
                Anchor firstAnchor = new Anchor(Color.BLACK, point.getX(), point.getY(), 5);
                splineAnchorsM2I.add(firstAnchor);
                splineGroupM2I.getChildren().add(firstAnchor);
                break;
            case 2:
                // Create a curve from the first point to the second point
                Anchor secondAnchor = new Anchor(Color.BLACK, point.getX(), point.getY(), 5);
                splineAnchorsM2I.add(secondAnchor);
                CurveInfo firstCurveInfo = createCurve(splineAnchorsM2I.getFirst(), secondAnchor);
                splineCurvesInfoM2I.add(firstCurveInfo);
                CubicCurve firstCurve = firstCurveInfo.curve;
                splineCurvesM2I.add(firstCurve);
                firstCurveInfo.addCurveToGroup();
                break;
            case 3:
                // Create a curve from the second point to the third point
                Anchor thirdAnchor = new Anchor(Color.BLACK, point.getX(), point.getY(), 5);
                CurveInfo secondCurveInfo = createCurve(splineAnchorsM2I.getLast(), thirdAnchor);
                splineCurvesInfoM2I.add(secondCurveInfo);
                CubicCurve secondCurve = secondCurveInfo.curve;
                splineCurvesM2I.add(secondCurve);
                secondCurveInfo.addCurveToGroup();
                splineAnchorsM2I.add(thirdAnchor);

                // Create a curve from the third point to the first point
                CurveInfo thirdCurveInfo = createCurve(thirdAnchor, splineAnchorsM2I.getFirst());
                splineCurvesInfoM2I.add(thirdCurveInfo);
                CubicCurve thirdCurve = thirdCurveInfo.curve;
                splineCurvesM2I.add(thirdCurve);
                thirdCurveInfo.addCurveToGroup();
                break;
            default:
                // remove the last curve
                CurveInfo lastCurveInfo = splineCurvesInfoM2I.removeLast();
                splineCurvesM2I.removeLast();
                lastCurveInfo.removeCurveFromGroup();

                // Create a curve from the penultimate point to the last point
                Anchor lastAnchor = new Anchor(Color.BLACK, point.getX(), point.getY(), 5);
                CurveInfo penultimateCurveInfo = createCurve(splineAnchorsM2I.getLast(), lastAnchor);
                splineCurvesInfoM2I.add(penultimateCurveInfo);
                CubicCurve penultimateCurve = penultimateCurveInfo.curve;
                splineCurvesM2I.add(penultimateCurve);
                penultimateCurveInfo.addCurveToGroup();
                splineAnchorsM2I.add(lastAnchor);

                // Create a curve from the last point to the first point
                CurveInfo newLastCurveInfo = createCurve(lastAnchor, splineAnchorsM2I.getFirst());
                splineCurvesInfoM2I.add(newLastCurveInfo);
                CubicCurve newLastCurve = newLastCurveInfo.curve;
                splineCurvesM2I.add(newLastCurve);
                newLastCurveInfo.addCurveToGroup();
                break;
        }
        imageM2I.setContent(getPath());
    }
    
    private static Point2d getPoint(CubicCurve curve, double t) {
        double x = Math.pow(1 - t, 3) * curve.getStartX() + 3 * t * Math.pow(1 - t, 2) * curve.getControlX1() + 3 * Math.pow(t, 2) * (1 - t) * curve.getControlX2() + Math.pow(t, 3) * curve.getEndX();
        double y = Math.pow(1 - t, 3) * curve.getStartY() + 3 * t * Math.pow(1 - t, 2) * curve.getControlY1() + 3 * Math.pow(t, 2) * (1 - t) * curve.getControlY2() + Math.pow(t, 3) * curve.getEndY();
        return new Point2d(x, y);
    }

    private static void initializeCurves() {
        addPoint(new Point2d(25.0, 50.0));
        addPoint(new Point2d(635.0, 50.0));
        addPoint(new Point2d(635.0, 520.0));
        addPoint(new Point2d(25.0, 520.0));
    }

    private static void resetM2I() {
        splinePointsM2I.clear();
        splineAnchorsM2I.clear();
        splineCurvesM2I.clear();
        splineCurvesInfoM2I.clear();
        splineGroupM2I.getChildren().clear();
        initializeCurves();
        colorChooserM2I.setStyle("-fx-background-radius: 15; -fx-background-color: white; -fx-border-radius: 15; -fx-border-color: black;");
        imageColorPropertyM2I.setValue(Color.WHITE);

        playheadM2I.setMax(mediaPlayerM2I.getDurationInSeconds() * 2);
        playheadM2I.setValue(0);
        playheadTextM2I.setText("0:00");
        songDurationTextM2I.setText(mediaPlayerM2I.getSongDuration());

        // if the music is playing, stop it
        if (mediaPlayerStatusM2I == 0) {
            playStopButtonM2I.fire();
        }
    }

    private static String getPath() {
        String path;
        if (splineCurvesM2I.size() == 0) {
            path = "M " + splineAnchorsM2I.get(0).getCenterX() + " " + splineAnchorsM2I.get(0).getCenterY();
        } else {
            path = "M " + splineCurvesM2I.get(0).getStartX() + " " + splineCurvesM2I.get(0).getStartY();
            for (int i = 0; i < splineCurvesM2I.size(); i++) {
                path += " C " + splineCurvesM2I.get(i).getControlX1() + " " + splineCurvesM2I.get(i).getControlY1() + " " + splineCurvesM2I.get(i).getControlX2() + " " + splineCurvesM2I.get(i).getControlY2() + " " + splineCurvesM2I.get(i).getEndX() + " " + splineCurvesM2I.get(i).getEndY();
            }
        }
        return path + " Z";
    }

    private static void setLevelM2I(int level) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        String levelID = level + "a";
        JSONObject levelJSONObject = (JSONObject) levelsJSONObject.get(levelID);
        mediaPlayerM2I.setAudioFile(new File("assets/music/" + (String) levelJSONObject.get("song") + ".wav"));
        levelParametersM2I = currentLevel + "," + timestamp.format("yyyy-MM-dd HH:mm:ss.SSS zzz") + "," + levelJSONObject.get("timbreClass") + "," + levelJSONObject.get("mode") + "," + levelJSONObject.get("tempo") + "," + levelJSONObject.get("rhythmicRegularity") + "," + levelJSONObject.get("dynamicVariation");
        resetM2I();
    }

    private static void gatherResponseM2I() {
        try {
            Color color = imageColorPropertyM2I.getValue();
            long elapsedTime = System.currentTimeMillis() - timestamp.getMillisecondsUTC();
            responseWriterM2I = new BufferedWriter(new FileWriter("assets/data/responseM2I.csv", true));
            responseWriterM2I.write(uuid + "," + levelParametersM2I + "," + color.getHue() + "," + color.getSaturation() + "," + color.getBrightness() + "," + getPath() + "," + splineAnchorsM2I.size() + "," + elapsedTime + ",");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ControlLine extends Line {
        ControlLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
            startXProperty().bind(startX);
            startYProperty().bind(startY);
            endXProperty().bind(endX);
            endYProperty().bind(endY);
            setStrokeWidth(2);
            setStroke(Color.DARKGREY.deriveColor(0, 1, 1, 0.5));
        }
    }

    static record CurveInfo(CubicCurve curve, Anchor startPoint, Anchor endPoint, Anchor controlPoint1, Anchor controlPoint2, ControlLine controlLine1, ControlLine controlLine2) {
        public void addCurveToGroup() {
            if (!splineGroupM2I.getChildren().contains(curve)) {
                splineGroupM2I.getChildren().add(curve);
            }
            if (!splineGroupM2I.getChildren().contains(startPoint)) {
                splineGroupM2I.getChildren().add(startPoint);
            }
            if (!splineGroupM2I.getChildren().contains(endPoint)) {
                splineGroupM2I.getChildren().add(endPoint);
            }
            if (!splineGroupM2I.getChildren().contains(controlPoint1)) {
                splineGroupM2I.getChildren().add(controlPoint1);
            }
            if (!splineGroupM2I.getChildren().contains(controlPoint2)) {
                splineGroupM2I.getChildren().add(controlPoint2);
            }
            if (!splineGroupM2I.getChildren().contains(controlLine1)) {
                splineGroupM2I.getChildren().add(controlLine1);
            }
            if (!splineGroupM2I.getChildren().contains(controlLine2)) {
                splineGroupM2I.getChildren().add(controlLine2);
            }
        }

        public void removeCurveFromGroup() {
            splineGroupM2I.getChildren().removeAll(curve, startPoint, endPoint, controlPoint1, controlPoint2, controlLine1, controlLine2);
        }

        public String toString() {
            return "Curve from (" + startPoint.getCenterX() + ", " + startPoint.getCenterY() + ") to (" + endPoint.getCenterX() + ", " + endPoint.getCenterY() + ")";
        }
    }

    static class Anchor extends Circle {
        CurveInfo[] curves = new CurveInfo[2];

        Anchor(Color color, DoubleProperty x, DoubleProperty y, double radius) {
            super(x.get(), y.get(), radius);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);
            bindCoordinates(x, y);
            enableDrag();

            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    if (splineAnchorsM2I.size() > 2) {
                        joinCurves(curves[0].curve, curves[1].curve);
                    }
                }
            });
        }

        Anchor(Color color, double x, double y, double radius) {
            super(x, y, radius);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(2);
            setStrokeType(StrokeType.OUTSIDE);
            enableDrag();

            this.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    if (splineAnchorsM2I.size() > 2) {
                        joinCurves(curves[0].curve, curves[1].curve);
                    }
                }
            });
        }

        public void bindCoordinates(DoubleProperty x, DoubleProperty y) {
            x.bind(centerXProperty());
            y.bind(centerYProperty());
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = getCenterX() - mouseEvent.getX();
                dragDelta.y = getCenterY() - mouseEvent.getY();
                getScene().setCursor(Cursor.MOVE);
            });
            setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));
            setOnMouseDragged(mouseEvent -> {
                double newX = mouseEvent.getX() + dragDelta.x;
                if (newX >= 25 && newX <= 635) {
                    setCenterX(newX);
                }
                double newY = mouseEvent.getY() + dragDelta.y;
                if (newY >= 50 && newY <= 520) {
                    setCenterY(newY);
                }
                imageM2I.setContent(getPath());
            });
            setOnMouseEntered(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseExited(mouseEvent -> {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }

        // records relative x and y coordinates.
        private class Delta {
            double x, y;
        }
    }

    static class SimpleAudioPlayer {
        private long currentFrame;
        private File file;
        private Clip clip;
        private AudioInputStream audioInputStream;
    
        public SimpleAudioPlayer() {}

        public SimpleAudioPlayer(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            setAudioFile(file);
        }

        public void setAudioFile(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            this.file = file;
            resetAudioStream();
        }

        public long getDurationInSeconds() {
            return clip.getMicrosecondLength() / 1000000;
        }

        public String getSongDuration() {
            String duration = "";
            long durationInSeconds = getDurationInSeconds();
            int minutes = (int) (durationInSeconds / 60);
            int seconds = (int) (durationInSeconds % 60);

            if (seconds < 10) {
                duration = minutes + ":0" + seconds;
            } else {
                duration = minutes + ":" + seconds;
            }

            return duration;
        }
    
        public void play() {
            clip.start();
        }

        public void loop() {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
        
        public void pause() {
            this.currentFrame = this.clip.getMicrosecondPosition();
            clip.stop();
        }
        
        public void resume() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            clip.setMicrosecondPosition(currentFrame);
            this.play();
        }
        
        public void restart() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
            stop();
            resetAudioStream();
            currentFrame = 0L;
            clip.setMicrosecondPosition(0);
        }
        
        public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            currentFrame = 0L;
            clip.stop();
            clip.setMicrosecondPosition(clip.getMicrosecondLength());
            clip.close();
        }

        public void seek(long frame) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            currentFrame = frame;
        }

        public void setVolume(double volume) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue((float) (Math.log(volume) / Math.log(10.0) * 20.0));
        }
        
        public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            addChangeListener();
        }

        public boolean isFinished() {
            return clip.getMicrosecondPosition() == clip.getMicrosecondLength();
        }

        public void addChangeListener() {
            long clipLength = clip.getMicrosecondLength() / 1000000;
            long clipLengthHalfSeconds = clip.getMicrosecondLength() / 500000;

            clip.addLineListener(event -> {
                long lastSecond = 0L;
                long lastHalfSecond = 0L;
                if (event.getType() == LineEvent.Type.START) {
                    while (!isFinished()) {
                        long currentSecond = (clip.getMicrosecondPosition() / 1000000) % clipLength;
                        long currentHalfSecond = (clip.getMicrosecondPosition() / 500000) % clipLengthHalfSeconds;

                        if (currentHalfSecond != lastHalfSecond) {
                            lastHalfSecond = currentHalfSecond;
                            playheadM2I.setValue(currentHalfSecond);
                        }

                        if (currentSecond != lastSecond) {
                            lastSecond = currentSecond;
                            int minutes = (int) (currentSecond / 60);
                            int seconds = (int) (currentSecond % 60);

                            if (seconds < 10) {
                                playheadTextM2I.setText(minutes + ":0" + seconds);
                            } else {
                                playheadTextM2I.setText(minutes + ":" + seconds);
                            }
                        }
                    }
                }
            });
        }
    }

    
    /* Image to Music Functions and Structures */
    private static void resetI2M(String fileName) {
        LinkedList<Integer> pitches = SVGReader.getSong(fileName);
        pitchesI2M = new int[pitches.size()];
        for (int i = 0; i < pitches.size(); i++) {
            pitchesI2M[i] = pitches.get(i);
        }

        keyI2M = rng.nextInt(-4, 8) + 60;
        for (int i = 0; i < durationsI2M.length; i++) {
            durationsI2M[i] = getDurations(i);
        }

        imageI2M.setContent(SVGReader.getSVGPath(fileName));
        imageI2M.setFill(SVGReader.getSVGFill(fileName));
    }

    private static int getDynamicVariationModifierI2M(int level) {
        double randomValue = rng.nextDouble();
        switch (level) {
            default:
            case 0:
                return 0;
            case 1:
                if (randomValue < 0.3333) {
                    return -1;
                } else if (randomValue >= 0.3333 && randomValue < 0.6667) {
                    return 0;
                } else {
                    return 1;
                }
            case 2:
                if (randomValue < 0.2) {
                    return -2;
                } else if (randomValue >= 0.2 && randomValue < 0.4) {
                    return -1;
                } else if (randomValue >= 0.4 && randomValue < 0.6) {
                    return 0;
                } else if (randomValue >= 0.6 && randomValue < 0.8) {
                    return 1;
                } else {
                    return 2;
                }
            case 3:
                if (randomValue < 0.142857142857) {
                    return -3;
                } else if (randomValue >= 0.142857142857 && randomValue < 0.285714285714) {
                    return -2;
                } else if (randomValue >= 0.285714285714 && randomValue < 0.428571428571) {
                    return -1;
                } else if (randomValue >= 0.428571428571 && randomValue < 0.571428571429) {
                    return 0;
                } else if (randomValue >= 0.571428571429 && randomValue < 0.714285714286) {
                    return 1;
                } else if (randomValue >= 0.714285714286 && randomValue < 0.857142857143) {
                    return 2;
                } else {
                    return 3;
                }
        }

    }

    private static LinkedList<NoteDuration> getDurations(int level) {
        LinkedList<NoteDuration> durations;

        switch (level) {
            default:
            case 0:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.MINIM);
                    }
                };
                break;
            case 1:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.MINIM);
                        add(NoteDuration.MINIM);
                        add(NoteDuration.MINIM);
                    }
                };
                break;
            case 2:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.MINIM);
                        add(NoteDuration.MINIM);
                    }
                };
                break;
            case 3:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.SEMIQUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                    }
                };
                break;
            case 4:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.QUAVER);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                    }
                };
                break;
            case 5:
                durations = new LinkedList<>() {
                    {
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                        add(NoteDuration.CROTCHET);
                    }
                };
                break;
        }

        // Shuffle the list
        Random tempRNG = new Random(currentLevel);
        Collections.shuffle(durations, tempRNG);

        if (level == 0) {
            int index = rng.nextInt(0, 9);
            durations.add(index, NoteDuration.QUAVER_TRIPLET);
            durations.add(index, NoteDuration.CROCHET_TRIPLET);
            durations.add(index, NoteDuration.QUAVER_TRIPLET);
            durations.add(index, NoteDuration.CROCHET_TRIPLET);
            durations.add(index, NoteDuration.CROCHET_TRIPLET);
            durations.add(index, NoteDuration.MINIM_TRIPLET);
        } else if (level == 1) {
            int index = rng.nextInt(0, 13);
            durations.add(index, NoteDuration.CROCHET_TRIPLET);
            durations.add(index, NoteDuration.QUAVER_TRIPLET);
        }

        return durations;
    }

    private static void gatherResponseI2M() {
        try {
            long elapsedTime = System.currentTimeMillis() - timestamp.getMillisecondsUTC();
            responseWriterI2M = new BufferedWriter(new FileWriter("assets/data/responseI2M.csv", true));
            responseWriterI2M.write(uuid + "," + levelParametersI2M + "," + getTimbreNameI2M() + "," + getModeNameI2M() + "," + getDynamicNameI2M() + "," + getTempoNameI2M() + "," + getRhythmicRegularity() + "," + ((int) (33.3 * dynamicVariationKnobI2M.getValue() + 0.5) / 100.0) + ","+ elapsedTime + ",");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setLevelI2M(int level) {
        String levelID = level + "b";
        JSONObject levelJSONObject = (JSONObject) levelsJSONObject.get(levelID);

        resetI2M((String) levelJSONObject.get("image"));
        timbreClassKnobI2M.setValue((Double) levelJSONObject.get("timbreClass"));
        modeKnobI2M.setValue((Double) levelJSONObject.get("mode"));
        volumeKnobI2M.setValue((Double) levelJSONObject.get("overallVolume"));
        tempoKnobI2M.setValue((Double) levelJSONObject.get("tempo"));
        dynamicVariationKnobI2M.setValue((Double) levelJSONObject.get("dynamicVariation"));
        rhythmicRegularityKnobI2M.setValue((Double) levelJSONObject.get("rhythmicRegularity"));
        levelParametersI2M = currentLevel + "," + timestamp.format("yyyy-MM-dd HH:mm:ss.SSS zzz") + "," + levelJSONObject.get("hue") + "," + levelJSONObject.get("saturation") + "," + levelJSONObject.get("brightness") + "," + levelJSONObject.get("path");
    }

    private static String getTimbreNameI2M() {
		switch ((int) timbreClassKnobI2M.getValue()) {
			case 0:
				return "Plucked Strings";
			case 1:
				return "Bowed Strings";
			case 2:
				return "Woodwind";
			case 3:
				return "Brass";
			case 4:
				return "Chromatic Percussion";
			default:
				return null;
		}
	}

    private static String getModeNameI2M() {
        switch ((int) modeKnobI2M.getValue()) {
            case 0:
                return "Major";
            case 1:
                return "Natural Minor";
            case 2:
                return "Minor Pentatonic";
            case 3:
                return "Chromatic";
            default:
                return null;
        }
    }

    private static String getDynamicNameI2M() {
        int dynamic = (int) volumeKnobI2M.getValue() + 20;
        if (20 <= dynamic && dynamic < 50) {
            return "Quiet";
        } else if (50 <= dynamic && dynamic < 82) {
            return "Medium";
        } else if (82 <= dynamic && dynamic <= 112) {
            return "Loud";
        } else {
            return null;
        }
    }

    private static String getTempoNameI2M() {
        int tempo = (int) tempoKnobI2M.getValue() + 40;
        if (40 <= tempo && tempo < 100) {
            return "Slow";
        } else if (100 <= tempo && tempo < 160) {
            return "Moderate";
        } else if (160 <= tempo && tempo < 220) {
            return "Fast";
        } else {
            return null;
        }
    }

    private static String getRhythmicRegularity() {
        int regularity = (int) rhythmicRegularityKnobI2M.getValue();
        switch (regularity) {
            case 0:
            case 1:
                return "Syncopated";
            case 2:
            case 3:
                return "Irregular";
            case 4:
            case 5:
                return "Regular";
            default:
                return null;
        }
    }
}