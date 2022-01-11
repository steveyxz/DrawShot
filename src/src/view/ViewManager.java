package src.view;

import src.enums.Tools;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class ViewManager {

    private static final double WIDTH = 640;
    private static final double HEIGHT = WIDTH / 12 * 9;
    private Stage mainStage;
    private Scene mainScene;
    private AnchorPane mainPane;

    private Group mainCanvas;
    private Group drawCanvas;

    private double oldMouseX;
    private double newMouseX;

    private double oldMouseY;
    private double newMouseY;

    private double canvasWidth;
    private double canvasHeight;

    private final LinkedList<Node> currentSubMenuObjs = new LinkedList<>();

    private double preCanvasX;
    private double nowCanvasX;
    private double preCanvasY;
    private double nowCanvasY;

    private final double basisX0 = 40;
    private final double basisY0 = 40;

    private Tools tools = Tools.Brush;

    private MenuBar mainMenu;

    private Group currentTools;

    private final LinkedList<Rectangle> pixelNodes = new LinkedList<>();
    private Group pixelGroup;

    public static boolean hasColorScene = false;
    public static boolean hasSaveScene = false;

    public static String filepathToSave = "C:/";
    public static String fileTypeToSave = "png";

    public static Color currentColor = Color.BLACK;
    public static Color currentFillColor = Color.BLACK;

    public static double maxTimes = 0;
    public static double currentTimes = 0;

    public boolean isSaving = false;

    final Task<Void> task = new Task<>() {
        final int N_ITERATIONS = 100;

        @Override
        protected Void call() throws Exception {
            isSaving = true;

            /*
            int[][] pixels = new int[(int) (canvasWidth * canvasHeight)][3];

            for (int i = 0; i < pixelNodes.size(); i++) {
                int[] rgb = new int[3];
                Color color = (Color) pixelNodes.get(i).getFill();
                rgb[0] = (int) (color.getRed() * 255);
                rgb[1] = (int) (color.getGreen() * 255);
                rgb[2] = (int) (color.getBlue() * 255);
                pixels[i] = rgb;
                currentTimes++;
                updateProgress(currentTimes, maxTimes);
            }

            BufferedImage savedImage = new BufferedImage((int) canvasWidth, (int) canvasHeight, BufferedImage.TYPE_INT_RGB);
            int[] realPixels = new int[(int) (canvasWidth * canvasHeight)];

            for (int i = 0; i < pixels.length; i++) {
                realPixels[i] = colourToNumber(pixels[i][0], pixels[i][1], pixels[i][2]);
            }

            savedImage.getRaster().setDataElements(0, 0, (int) canvasWidth, (int) canvasHeight, realPixels);

            ImageSaver imageSaver = new ImageSaver((int) canvasWidth, (int) canvasHeight, fileTypeToSave);

            imageSaver.writeFile(filepathToSave, savedImage);
             */

            isSaving = false;

            Task<Void> endTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {

                    int removed = 0;

                    for (int i = 0; i < mainPane.getChildren().size(); i++) {
                        if (mainPane.getChildren().get(i).getId().equals("progressBar")) {
                            removed = i;
                        }
                    }

                    mainPane.getChildren().remove(removed);

                    return null;
                }
            };

            Thread endThread = new Thread(endTask, "end-task");
            endThread.setDaemon(true);
            endThread.start();

            return null;
        }
    };

    public ViewManager() {
        init();
        createBorders();
        createMenus();
        createListeners();
        addCanvas(40, 40, 400, 300);
        moveCanvasToCentre();
        createPixels();
        changeSubMenus();
    }


    private void createPixels() {
        for (int i = 0; i < canvasHeight; i++) {
            for (int j = 0; j < canvasWidth; j++) {
                Rectangle rect = new Rectangle();
                rect.setHeight(1D);
                rect.setWidth(1D);
                rect.setX(j + basisX0);
                rect.setY(i + basisY0);
                rect.setFill(Color.rgb(255, 255, 255, 0));
                pixelGroup.getChildren().add(rect);
                pixelNodes.add(rect);
            }
        }
    }

    private void addCanvas(double x, double y, double width, double height) {

        nowCanvasX = x;
        nowCanvasY = y;
        canvasWidth = width;
        canvasHeight = height;

        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(Color.WHITE);

        rectangle.setId("canvas");

        drawCanvas.getChildren().add(rectangle);
    }

    private void moveCanvasToCentre() {
        preCanvasX = nowCanvasX;
        preCanvasY = nowCanvasY;
        nowCanvasX = (mainStage.getWidth() / 2) - canvasWidth / 2;
        nowCanvasY = (mainStage.getHeight() / 2) - canvasHeight / 2 - 23;
        for (int i = 0; i < drawCanvas.getChildren().size(); i++) {
            Node currentObj = drawCanvas.getChildren().get(i);
            currentObj.setTranslateX(currentObj.getTranslateX() + nowCanvasX - preCanvasX);
            currentObj.setTranslateY(currentObj.getTranslateY() + nowCanvasY - preCanvasY);
        }
        drawCanvas.toFront();
        pixelGroup.toFront();
        currentTools.toFront();
        mainStage.setMinHeight(canvasHeight + 200);
        mainStage.setMinWidth(canvasWidth + 200);
    }

    private void createListeners() {

        createMouseListeners();
        createWindowListeners();

    }

    private void createWindowListeners() {

        mainScene.widthProperty().addListener(
                (observableValue, oldSceneWidth, newSceneWidth) -> {
                    createBorders();
                    moveCanvasToCentre();
                    for (int i = 0; i < mainCanvas.getChildren().size(); i++) {
                        if (mainCanvas.getChildren().get(i) instanceof MenuBar) {
                            mainCanvas.getChildren().get(i).toFront();
                        }
                    }
                    mainMenu.setPrefWidth(mainStage.getWidth());
                    currentTools.toFront();
                });
        mainScene.heightProperty().addListener(
                (observableValue, oldSceneHeight, newSceneHeight) ->
                {
                    createBorders();
                    moveCanvasToCentre();
                    for (int i = 0; i < mainCanvas.getChildren().size(); i++) {
                        if (mainCanvas.getChildren().get(i) instanceof MenuBar) {
                            mainCanvas.getChildren().get(i).toFront();
                        }
                    }
                    mainMenu.setPrefWidth(mainStage.getWidth());
                    currentTools.toFront();
                });

    }

    public LinkedList<LinkedList<Integer>> findLine(int x0, int y0, int x1, int y1) {

        LinkedList<LinkedList<Integer>> values = new LinkedList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while (true) {
            LinkedList<Integer> thing = new LinkedList<>();

            thing.add(x0);
            thing.add(y0);

            values.add(thing);

            if (x0 == x1 && y0 == y1)
                break;

            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }

            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        return values;
    }

    private void createMouseListeners() {
        mainScene.setOnMouseDragged(mouseEvent -> {
            if (isSaving) {
                return;
            }
            try {
                oldMouseX = newMouseX;
                oldMouseY = newMouseY;
                newMouseX = mouseEvent.getX();
                newMouseY = mouseEvent.getY();
                int indexOld = (int) ((((int) (oldMouseY - nowCanvasY)) * canvasWidth) + oldMouseX - (nowCanvasX));
                int indexNew = (int) ((((int) (newMouseY - nowCanvasY)) * canvasWidth) + newMouseX - (nowCanvasX));
                double[] xyOld = calcXY(indexOld);
                double[] xyNew = calcXY(indexNew);
                Node sizeFinder = new Rectangle();
                for (int i = 0; i < currentTools.getChildren().size(); i++) {
                    if (currentTools.getChildren().get(i).getId().equals("brushSubmenuSize") && tools == Tools.Brush) {
                        sizeFinder = currentTools.getChildren().get(i);
                    }
                    if (currentTools.getChildren().get(i).getId().equals("eraserSubmenuSize") && tools == Tools.Eraser) {
                        sizeFinder = currentTools.getChildren().get(i);
                    }
                }
                int toggle = 0;
                int addOn = 0;
                int dir = 0;
                if (Math.abs(oldMouseX - newMouseX) > Math.abs(oldMouseY - newMouseY)) {
                    dir = 1;
                }
                for (int j = 0; j < (int) ((ComboBox<?>) sizeFinder).getValue(); j++) {
                    if (j % 2 == 0) {
                        toggle = 1;
                    } else {
                        toggle = -1;
                    }
                    addOn = toggle * (j / 2);
                    LinkedList<LinkedList<Integer>> plotVals;
                    if (dir == 0) {
                        plotVals = findLine((int) xyOld[0] + addOn, (int) xyOld[1], (int) xyNew[0] + addOn, (int) xyNew[1]);
                    } else {
                        plotVals = findLine((int) xyOld[0], (int) xyOld[1] + addOn, (int) xyNew[0], (int) xyNew[1] + addOn);
                    }
                    if (oldMouseX > (nowCanvasX + 0.1) && oldMouseY > (nowCanvasY + 0.1) && oldMouseX < (nowCanvasX + canvasWidth - 0.1) && oldMouseY < (nowCanvasY + canvasHeight - 0.1)) {
                        if (newMouseX > (nowCanvasX + 0.1) && newMouseY > (0.1 + nowCanvasY) && newMouseX < (nowCanvasX + canvasWidth - 0.1) && newMouseY < (nowCanvasY + canvasHeight - 0.1)) {
                            for (int i = 0; i < plotVals.size(); i++) {
                                double[] coordinates = {0, 0};
                                coordinates[1] = plotVals.get(i).get(0);
                                coordinates[0] = plotVals.get(i).get(1);
                                if (tools == Tools.Brush) {
                                    pixelNodes.get((int) calcIndex(coordinates)).setFill(currentColor);
                                } else if (tools == Tools.Eraser) {
                                    pixelNodes.get((int) calcIndex(coordinates)).setFill(Color.rgb(255, 255, 255, 0));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        });

        mainScene.setOnMouseClicked(mouseEvent -> {

            if (isSaving) {
                return;
            }

            try {
                oldMouseX = newMouseX;
                oldMouseY = newMouseY;
                newMouseX = mouseEvent.getX();
                newMouseY = mouseEvent.getY();
                int index = (int) (((int) (newMouseY - nowCanvasY)) * canvasWidth + (int) newMouseX - (int) nowCanvasX);
                if (tools != Tools.Fill) {
                    Node sizeFinder = new Rectangle();
                    for (int i = 0; i < currentTools.getChildren().size(); i++) {
                        if (currentTools.getChildren().get(i).getId().equals("brushSubmenuSize")) {
                            sizeFinder = currentTools.getChildren().get(i);
                        }
                    }
                    for (int i = 0; i < Math.pow((int) ((ComboBox<?>) sizeFinder).getValue(), 2); i++) {
                        if (oldMouseX > nowCanvasX && oldMouseY > nowCanvasY && oldMouseX < (nowCanvasX + canvasWidth) && oldMouseY < (nowCanvasY + canvasHeight)) {
                            if (newMouseX > nowCanvasX && newMouseY > nowCanvasY && newMouseX < (nowCanvasX + canvasWidth) && newMouseY < (nowCanvasY + canvasHeight)) {
                                if (tools == Tools.Brush) {
                                    pixelNodes.get(index).setFill(currentColor);
                                } else if (tools == Tools.Eraser) {
                                    pixelNodes.get(index).setFill(Color.rgb(255, 255, 255, 0));
                                }
                            }
                        }
                    }
                } else {
                    if (currentFillColor != pixelNodes.get(index).getFill()) {
                        double startY = calcXY(index)[1];
                        double startX = calcXY(index)[0];
                        floodFillScanlineStack((int) startX, (int) startY, currentFillColor, (Color) pixelNodes.get(index).getFill());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
            }
        });

        mainScene.setOnMouseMoved(mouseEvent -> {
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            newMouseX = mouseEvent.getX();
            newMouseY = mouseEvent.getY();
            currentTools.toFront();
        });
        mainScene.setOnMouseDragOver(mouseDragEvent -> {
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            newMouseX = mouseDragEvent.getX();
            newMouseY = mouseDragEvent.getY();
            currentTools.toFront();
        });
        mainScene.setOnMouseDragEntered(mouseDragEvent -> {
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            newMouseX = mouseDragEvent.getX();
            newMouseY = mouseDragEvent.getY();
            currentTools.toFront();
        });
        mainScene.setOnMouseDragExited(mouseDragEvent -> {
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            newMouseX = mouseDragEvent.getX();
            newMouseY = mouseDragEvent.getY();
            currentTools.toFront();
        });
        mainScene.setOnMouseDragReleased(mouseDragEvent -> {
            oldMouseX = newMouseX;
            oldMouseY = newMouseY;
            newMouseX = mouseDragEvent.getX();
            newMouseY = mouseDragEvent.getY();
            currentTools.toFront();
        });

    }

    public boolean isSafe(int m, int n, int x, int y, Color target)
    {
        if ((x >= 0 && x < n && y >= 0 && y < m)) {
            return pixelNodes.get((int) calcIndex(new double[] {y, x})).getFill().equals(target);
        }
        return false;
    }

    public void floodFill(int x, int y, Color replacement)
    {
        int[] row = {-1, 0, 0, 1};
        int[] col = {0, -1, 1, 0};

        int m = (int) canvasHeight;
        int n = (int) canvasWidth;

        Queue<Integer> q = new ArrayDeque<>();
        q.add((int) calcIndex(new double[] {y, x}));

        Color target = (Color) pixelNodes.get((int) calcIndex(new double[] {y, x})).getFill();

        if (target == replacement) {
            return;
        }

        Rectangle newNode;

        while (!q.isEmpty())
        {

            Integer currentIndex = q.poll();

            int currentX = (int) calcXY(currentIndex)[0];
            int currentY = (int) calcXY(currentIndex)[1];

            newNode = pixelNodes.get(currentIndex);
            newNode.setFill(replacement);

            pixelNodes.set(currentIndex, newNode);
            pixelGroup.getChildren().set(currentIndex, newNode);

            for (int i = 0; i < row.length; i++) {
                if (isSafe(m, n, currentX + row[i], currentY + col[i], target)) {
                    q.add((int) calcIndex(new double[] {currentY + col[i], currentX + row[i]}));
                }
            }
        }
    }



    private void fill(int index) {

        int topIndexAt = index;
        int bottomIndexAt = index;

        LinkedList<Integer> yaxValues = new LinkedList<>();

        Color color = (Color) pixelNodes.get(index).getFill();

        boolean isAtTop = false;
        boolean isAtBottom = false;
        boolean isAtLeft = false;
        boolean isAtRight = false;

        do {
            yaxValues.add(topIndexAt);
            topIndexAt = (int) (topIndexAt - canvasWidth);
            if (topIndexAt < 0) {
                break;
            }
        } while (pixelNodes.get(topIndexAt).getFill().equals(color));

        do {
            yaxValues.add(bottomIndexAt);
            bottomIndexAt = (int) (bottomIndexAt + canvasWidth);
            if (bottomIndexAt > canvasWidth * canvasHeight) {
                break;
            }
        } while (pixelNodes.get(bottomIndexAt).getFill().equals(color));

        LinkedList<Integer> xaxValues = new LinkedList<>();

        for (int i = 0; i < yaxValues.size(); i++) {
            int rightIndexAt = yaxValues.get(i);
            int leftIndexAt = rightIndexAt;
            do {
                pixelNodes.get(rightIndexAt).setFill(currentColor);
                xaxValues.add(rightIndexAt);
                rightIndexAt = rightIndexAt + 1;
                if (rightIndexAt < 0) {
                    break;
                }
                if (rightIndexAt >= canvasWidth * canvasHeight) {
                    break;
                }
            } while (pixelNodes.get(rightIndexAt).getFill().equals(color));

            do {
                pixelNodes.get(leftIndexAt).setFill(currentColor);
                xaxValues.add(leftIndexAt);
                leftIndexAt = leftIndexAt - 1;
                if (leftIndexAt < 0) {
                    break;
                }
                if (leftIndexAt > canvasWidth * canvasHeight) {
                    break;
                }

            } while (pixelNodes.get(leftIndexAt).getFill().equals(color));
        }


    }

    private void floodFillScanlineStack(int x, int y, Color newColor, Color oldColor)
    {
        if (oldColor == newColor) return;

        LinkedList<Integer> seeds = new LinkedList<>();

        seeds.add((int) calcIndex(new double[] {y, x}));

        while (!seeds.isEmpty()) {

            Integer currentSeed = seeds.poll();

            LinkedList<Integer> lineNodes = new LinkedList<>();

            lineNodes.add(currentSeed);

            boolean canPlantSeedAbove = true;
            boolean canPlantSeedBelow = true;

            while (!lineNodes.isEmpty()) {

                Integer currentNode = lineNodes.poll();

                //Plant seeds above
                if (getColorAtIndex(Math.abs(currentNode - canvasWidth)).equals(oldColor)) {
                    if (canPlantSeedAbove) {
                        seeds.add((int) (currentNode - canvasWidth));
                        canPlantSeedAbove = false;
                    }
                } else {
                    canPlantSeedAbove = true;
                }

                //Plant seeds below
                if (getColorAtIndex(Math.abs(currentNode + canvasWidth)).equals(oldColor)) {
                    if (canPlantSeedBelow) {
                        seeds.add((int) (currentNode + canvasWidth));
                        canPlantSeedBelow = false;
                    }
                } else {
                    canPlantSeedBelow = true;
                }

                pixelNodes.get(currentNode).setFill(newColor);
                pixelGroup.getChildren().set(currentNode, pixelNodes.get(currentNode));

                if (getColorAtIndex(currentNode + 1).equals(oldColor)) {
                    lineNodes.add(currentNode + 1);
                }
                if (getColorAtIndex(currentNode - 1).equals(oldColor)) {
                    lineNodes.add(currentNode - 1);
                }

            }

        }
    }

    private Rectangle getRectAtIndex(int index) {
        return pixelNodes.get(index);
    }

    private Color getColorAtIndex(double index) {
        return (Color) pixelNodes.get((int) index).getFill();
    }

    private void init() {
        mainStage = new Stage();
        mainCanvas = new Group();
        mainPane = new AnchorPane();
        currentTools = new Group();
        pixelGroup = new Group();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainCanvas.setId("MAIN_CANVAS");
        mainPane.getChildren().add(mainCanvas);
        mainStage.setScene(mainScene);
        mainStage.setWidth(WIDTH);
        mainStage.setHeight(HEIGHT);
        drawCanvas = new Group();
        drawCanvas.setId("DRAWING_CANVAS");
        mainCanvas.getChildren().add(drawCanvas);
        currentTools.setId("TOOLBOX");
        mainPane.getChildren().add(currentTools);
        pixelGroup.setId("PIXEL_GROUP");
        drawCanvas.getChildren().add(pixelGroup);
    }

    public double[] calcXY(double index) {
        double x = (index) % (int) canvasWidth;
        double y = (int) index / canvasWidth;
        return new double[]{x, y};
    }

    public double calcIndex(double[] xy) {
        if ((xy[0] * canvasWidth - 1) + xy[1] != -1) {
            return (xy[0] * canvasWidth - 1) + xy[1];
        }
        return 0;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void createBorders() {
        LinkedList<Node> rectangles = new LinkedList<>();
        for (int i = 0; i < mainCanvas.getChildren().size(); i++) {
            if (mainCanvas.getChildren().get(i) instanceof Rectangle) {
                rectangles.add(mainCanvas.getChildren().get(i));
            }
        }
        for (int i = 0; i < rectangles.size(); i++) {
            mainCanvas.getChildren().remove(rectangles.get(i));
        }
        addRectangle(0, 0, 30, mainStage.getHeight(), Color.GRAY, "LeftBorder");
        addRectangle(0, mainStage.getHeight() - 60, mainStage.getWidth(), 30, Color.GRAY, "BottomBorder");
        addRectangle(mainStage.getWidth() - 40, 0, 30, mainStage.getHeight(), Color.GRAY, "RightBorder");
        addRectangle(30, 0, mainStage.getWidth() - 70, mainStage.getHeight() - 60, Color.ANTIQUEWHITE, "MainCanvasBackground");
        addRectangle(0, 0, mainStage.getWidth(), 60, Color.DODGERBLUE, "SUBMENU_HOLDER");
    }

    private void addRectangle(double x, double y, double width, double height, Paint color, String ID) {
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(color);
        rectangle.setId(ID);
        mainCanvas.getChildren().add(rectangle);
    }

    private void createMenus() {
        MenuBar menuBar = new MenuBar();
        mainMenu = menuBar;
        menuBar.setPrefWidth(mainStage.getWidth());
        menuBar.setId("menuBar");
        mainPane.getChildren().add(menuBar);
        addMenu(menuBar, "file");
        addMenu(menuBar, "tools");
        createSubMenus();
    }

    private void createSubMenus() {

        //Brush Submenus
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(1, 2, 4, 6, 10, 20, 32, 74);
        comboBox.setVisible(false);
        comboBox.setDisable(false);
        comboBox.setEditable(true);
        comboBox.setId("brushSubmenuSize");
        comboBox.setVisibleRowCount(7);
        comboBox.setTranslateX(25);
        comboBox.setTranslateY(29);
        comboBox.setMaxWidth(80);
        comboBox.setMaxHeight(20);
        comboBox.setValue(1);
        comboBox.setOnAction(actionEvent -> {
            comboBox.setValue(Integer.parseInt(String.valueOf(comboBox.getValue())));
        });
        comboBox.setStyle("-fx-height: 30; -fx-background-color: white; -fx-color: green; -fx-border-radius: 5px; -fx-display: inline-block;");

        ColorPicker brushColorPicker = new ColorPicker();
        brushColorPicker.setLayoutX(140);
        brushColorPicker.setLayoutY(30);
        brushColorPicker.setEditable(true);
        brushColorPicker.setId("brushSubmenuColor");
        brushColorPicker.setValue(Color.BLACK);

        brushColorPicker.setOnAction(actionEvent -> {
            ViewManager.currentColor = brushColorPicker.getValue();
        });

        //Eraser Submenus

        ComboBox<Integer> eraserCombo = new ComboBox<>();
        eraserCombo.getItems().addAll(1, 2, 4, 6, 10, 20, 32, 74);
        eraserCombo.setVisible(false);
        eraserCombo.setDisable(false);
        eraserCombo.setEditable(true);
        eraserCombo.setId("eraserSubmenuSize");
        eraserCombo.setVisibleRowCount(7);
        eraserCombo.setTranslateX(25);
        eraserCombo.setTranslateY(29);
        eraserCombo.setMaxWidth(80);
        eraserCombo.setMaxHeight(20);
        eraserCombo.setValue(1);
        eraserCombo.setOnAction(actionEvent -> {
            eraserCombo.setValue(Integer.parseInt(String.valueOf(eraserCombo.getValue())));
        });
        eraserCombo.setStyle("-fx-height: 30; -fx-background-color: white; -fx-color: green; -fx-border-radius: 5px; -fx-display: inline-block;");

        //Fill Submenus

        ColorPicker fillColorPicker = new ColorPicker();
        fillColorPicker.setLayoutX(50);
        fillColorPicker.setLayoutY(30);
        fillColorPicker.setEditable(true);
        fillColorPicker.setId("fillSubmenuColor");
        fillColorPicker.setValue(Color.BLACK);

        fillColorPicker.setOnAction(actionEvent -> {
            ViewManager.currentFillColor = fillColorPicker.getValue();
        });
        
        currentTools.getChildren().addAll(fillColorPicker, brushColorPicker, eraserCombo, comboBox);
    }

    private void addMenu(MenuBar target, String ID) {
        String menuStyleInactive = "-fx-background-color: lightgray; -fx-color: pink; -fx-width: 15px;";
        String menuStyleActive = "-fx-background-color: blue; -fx-color: white; -fx-width: 15px;";
        if (ID.equals("file")) {
            Menu fileMenu = new Menu();
            fileMenu.setText("File");
            fileMenu.setStyle(menuStyleInactive);
            fileMenu.setOnShown(event -> fileMenu.setStyle(menuStyleActive));
            fileMenu.setOnHidden(event -> fileMenu.setStyle(menuStyleInactive));
            MenuItem menuItem = new MenuItem("New");
            menuItem.setId("newMenuItem");
            fileMenu.getItems().add(menuItem);
            setMenuActions(menuItem);
            menuItem = new MenuItem("Save");
            menuItem.setId("saveMenuItem");
            fileMenu.getItems().add(menuItem);
            setMenuActions(menuItem);
            menuItem = new MenuItem("Exit");
            menuItem.setId("exitMenuItem");
            fileMenu.getItems().add(menuItem);
            setMenuActions(menuItem);
            target.getMenus().add(fileMenu);
        } else if (ID.equals("tools")) {
            Menu fileMenu = new Menu();
            fileMenu.setText("Tools");
            fileMenu.setStyle(menuStyleInactive);
            fileMenu.setOnShown(event -> fileMenu.setStyle(menuStyleActive));
            fileMenu.setOnHidden(event -> fileMenu.setStyle(menuStyleInactive));
            MenuItem menuItem = new MenuItem("Brush");
            menuItem.setId("brushMenuItem");
            setMenuActions(menuItem);
            fileMenu.getItems().add(menuItem);
            menuItem = new MenuItem("Eraser");
            menuItem.setId("eraserMenuItem");
            setMenuActions(menuItem);
            fileMenu.getItems().add(menuItem);
            menuItem = new MenuItem("Fill");
            menuItem.setId("fillMenuItem");
            setMenuActions(menuItem);
            fileMenu.getItems().add(menuItem);
            target.getMenus().add(fileMenu);
        }
    }

    private void setMenuActions(MenuItem item) {
        item.setOnAction(actionEvent -> {
            if (item.getId().equals("newMenuItem")) {
                System.out.println("NEW");
            } else if (item.getId().equals("saveMenuItem")) {
                if (!hasSaveScene) {
                    hasSaveScene = true;
                    new SaveViewManager(this);
                }
            } else if (item.getId().equals("exitMenuItem")) {
                mainStage.close();
            } else if (item.getId().equals("brushMenuItem")) {
                tools = Tools.Brush;
                changeSubMenus();
            } else if (item.getId().equals("eraserMenuItem")) {
                tools = Tools.Eraser;
                changeSubMenus();
            } else if (item.getId().equals("fillMenuItem")) {
                tools = Tools.Fill;
                changeSubMenus();
            }
        });
    }

    public void saveCurrentImage() {

        maxTimes = canvasHeight * canvasWidth;
        currentTimes = 0;

        ProgressBar progressBar = new ProgressBar(0);

        progressBar.setLayoutX(mainStage.getWidth() / 12);
        progressBar.setLayoutY(mainStage.getHeight() / 2 - 20);
        progressBar.setPrefHeight(50 / (mainStage.getHeight() / 400));
        progressBar.setPrefWidth(mainStage.getWidth() / 1.2);
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setId("progressBar");

        progressBar.toFront();

        mainPane.getChildren().add(progressBar);

        final Thread thread = new Thread(task, "task-thread");
        thread.setDaemon(true);
        thread.start();


    }

    private int colourToNumber(int r, int g, int b) {
        return (r << 16) + (g << 8) + (b);
    }

    private int[] numberToColor(int C) {
        int B = C % 256;
        int G = ((C-B)/256) % 256;
        int R = (int) (((C-B)/Math.pow(256, 2)) - G/256);
        return new int[] {R, G, B};
    }

    private void changeSubMenus() {
        for (int i = 0; i < currentTools.getChildren().size(); i++) {
            currentTools.getChildren().get(i).setVisible(false);
        }
        currentSubMenuObjs.clear();
        if (tools == Tools.Brush) {
            for (int i = 0; i < currentTools.getChildren().size(); i++) {
                if (currentTools.getChildren().get(i).getId().contains("brushSubmenu")) {
                    currentSubMenuObjs.add(currentTools.getChildren().get(i));
                }
            }
            for (int i = 0; i < currentSubMenuObjs.size(); i++) {
                currentSubMenuObjs.get(i).setVisible(true);
                currentSubMenuObjs.get(i).setDisable(false);
                currentSubMenuObjs.get(i).toFront();
            }
        }
        if (tools == Tools.Eraser) {
            for (int i = 0; i < currentTools.getChildren().size(); i++) {
                if (currentTools.getChildren().get(i).getId().contains("eraserSubmenu")) {
                    currentSubMenuObjs.add(currentTools.getChildren().get(i));
                }
            }
            for (int i = 0; i < currentSubMenuObjs.size(); i++) {
                currentSubMenuObjs.get(i).setVisible(true);
                currentSubMenuObjs.get(i).setDisable(false);
                currentSubMenuObjs.get(i).toFront();
            }
        }
        if (tools == Tools.Fill) {
            for (int i = 0; i < currentTools.getChildren().size(); i++) {
                if (currentTools.getChildren().get(i).getId().contains("fillSubmenu")) {
                    currentSubMenuObjs.add(currentTools.getChildren().get(i));
                }
            }
            for (int i = 0; i < currentSubMenuObjs.size(); i++) {
                currentSubMenuObjs.get(i).setVisible(true);
                currentSubMenuObjs.get(i).setDisable(false);
                currentSubMenuObjs.get(i).toFront();
            }
        }
    }

}
