import eu.hansolo.applefx.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;

public class CustomColorPicker extends VBox {
    private final ObjectProperty<Color> currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);

    private Pane colorRect;
    private final Pane colorBar;
    private final Pane colorRectOverlayOne;
    private final Pane colorRectOverlayTwo;
    private Region colorRectIndicator;
    private final Region colorBarIndicator;
    private Button selectButton;
    private ContextMenu contextMenu;

    private DoubleProperty hue = new SimpleDoubleProperty(-1);
    private DoubleProperty sat = new SimpleDoubleProperty(-1);
    private DoubleProperty bright = new SimpleDoubleProperty(-1);

    private DoubleProperty alpha = new SimpleDoubleProperty(100) {
        @Override
        protected void invalidated() {
            setCustomColor(new Color(getCustomColor().getRed(), getCustomColor().getGreen(),
                    getCustomColor().getBlue(), clamp(alpha.get() / 100)));
        }
    };

    public CustomColorPicker() {

        getStyleClass().add("my-custom-color");

        VBox box = new VBox();

        box.getStyleClass().add("color-rect-pane");
        customColorProperty().addListener((ov, t, t1) -> colorChanged());

        colorRectIndicator = new Region();
        colorRectIndicator.setId("color-rect-indicator");
        colorRectIndicator.setManaged(false);
        colorRectIndicator.setMouseTransparent(true);
        colorRectIndicator.setCache(true);

        final Pane colorRectOpacityContainer = new StackPane();

        colorRect = new StackPane();
        colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

        Pane colorRectHue = new Pane();
        colorRectHue.backgroundProperty().bind(new ObjectBinding<Background>() {

            {
                bind(hue);
            }

            @Override
            protected Background computeValue() {
                return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));

            }
        });

        colorRectOverlayOne = new Pane();
        colorRectOverlayOne.getStyleClass().add("color-rect");
        colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(255, 255, 255, 1)),
                        new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        EventHandler<MouseEvent> rectMouseHandler = event -> {
            final double x = event.getX();
            final double y = event.getY();
            sat.set(clamp(x / colorRect.getWidth()) * 100);
            bright.set(100 - (clamp(y / colorRect.getHeight()) * 100));
            updateHSBColor();
        };

        colorRectOverlayTwo = new Pane();
        colorRectOverlayTwo.getStyleClass().addAll("color-rect");
        colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
        colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

        Pane colorRectBlackBorder = new Pane();
        colorRectBlackBorder.setMouseTransparent(true);
        colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

        colorBar = new Pane();
        colorBar.getStyleClass().add("color-bar");
        colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));

        colorBarIndicator = new Region();
        colorBarIndicator.setId("color-bar-indicator");
        colorBarIndicator.setMouseTransparent(true);
        colorBarIndicator.setCache(true);

        colorRectIndicator.layoutXProperty().bind(
                sat.divide(100).multiply(colorRect.widthProperty()));
        colorRectIndicator.layoutYProperty().bind(
                Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
        colorBarIndicator.layoutXProperty().bind(
                hue.divide(360).multiply(colorBar.widthProperty()));
        colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

        EventHandler<MouseEvent> barMouseHandler = event -> {
            final double x = event.getX();
            hue.set(clamp(x / colorRect.getWidth()) * 360);
            updateHSBColor();
        };

        colorBar.setOnMouseDragged(barMouseHandler);
        colorBar.setOnMousePressed(barMouseHandler);

        selectButton = new MacosButton("Select");
        selectButton.setMaxWidth(Double.MAX_VALUE);
        selectButton.setAlignment(Pos.CENTER);
        selectButton.setOnAction(event -> {
            currentColorProperty.set(customColorProperty.get());
            contextMenu.hide();
        });

        colorBar.getChildren().setAll(colorBarIndicator);
        colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
        colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
        VBox.setVgrow(colorRect, Priority.SOMETIMES);
        box.getChildren().addAll(colorBar, colorRect, selectButton);

        getChildren().add(box);

        if (currentColorProperty.get() == null) {
            currentColorProperty.set(Color.TRANSPARENT);
        }
        updateValues();

    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }
    
    private void updateValues() {
        hue.set(getCurrentColor().getHue());
        sat.set(getCurrentColor().getSaturation() * 100);
        bright.set(getCurrentColor().getBrightness() * 100);
        alpha.set(getCurrentColor().getOpacity() * 100);
        setCustomColor(Color.hsb(hue.get(), clamp(sat.get() / 100),
                clamp(bright.get() / 100), clamp(alpha.get() / 100)));
    }

    private void colorChanged() {
        hue.set(getCustomColor().getHue());
        sat.set(getCustomColor().getSaturation() * 100);
        bright.set(getCustomColor().getBrightness() * 100);
    }

    private void updateHSBColor() {
        Color newColor = Color.hsb(hue.get(), clamp(sat.get() / 100),
                clamp(bright.get() / 100), clamp(alpha.get() / 100));
        setCustomColor(newColor);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        colorRectIndicator.autosize();
    }

    static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = (double) ((1.0 / 255) * x);
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColorProperty.set(currentColor);
        updateValues();
    }

    Color getCurrentColor() {
        return currentColorProperty.get();
    }

    final ObjectProperty<Color> customColorProperty() {
        return customColorProperty;
    }

    void setCustomColor(Color color) {
        customColorProperty.set(color);
    }

    Color getCustomColor() {
        return customColorProperty.get();
    }
}