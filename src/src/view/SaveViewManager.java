package src.view;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SaveViewManager {

    private ViewManager mainStage;
    private Stage saveStage;
    private Scene saveScene;
    private AnchorPane savePane;
    private String selectedPath = "C:/";
    private ProgressBar progressBar = new ProgressBar();

    public SaveViewManager(ViewManager viewManager) {
        this.mainStage = viewManager;

        init();
        makeSaveStuff();
    }

    private void makeSaveStuff() {

        Button button = new Button("Select file");

        button.setPrefHeight(30);
        button.setPrefWidth(150);
        button.setLayoutX(50);
        button.setLayoutY(20);

        button.setOnMouseClicked(mouseEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            selectedPath = directoryChooser.showDialog(saveStage).getAbsolutePath();
        });

        TextField name = new TextField("New Image");

        name.setLayoutX(50);
        name.setLayoutY(60);
        name.setPrefHeight(30);
        name.setPrefWidth(150);

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton radioButton = new RadioButton();
        radioButton.setText("PNG");
        radioButton.setId("png");
        radioButton.setToggleGroup(toggleGroup);
        radioButton.setLayoutX(50);
        radioButton.setLayoutY(100);
        radioButton.setSelected(true);

        RadioButton radioButton2 = new RadioButton();
        radioButton2.setText("JPG");
        radioButton2.setId("jpg");
        radioButton2.setToggleGroup(toggleGroup);
        radioButton2.setLayoutX(100);
        radioButton2.setLayoutY(100);

        RadioButton radioButton3 = new RadioButton();
        radioButton3.setText("JPEG");
        radioButton3.setId("jpeg");
        radioButton3.setToggleGroup(toggleGroup);
        radioButton3.setLayoutX(150);
        radioButton3.setLayoutY(100);

        Button finish = new Button("Save");

        finish.setPrefWidth(60);
        finish.setPrefHeight(30);
        finish.setLayoutY(150);
        finish.setLayoutX(105);
        finish.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        finish.setTextFill(Color.LIGHTBLUE);

        finish.setOnMouseClicked(mouseEvent -> {
            ViewManager.fileTypeToSave = ((RadioButton) toggleGroup.getSelectedToggle()).getId();
            selectedPath = selectedPath + "\\" + name.getText() + "." + ViewManager.fileTypeToSave;
            System.out.println(selectedPath);
            ViewManager.filepathToSave = selectedPath;
            saveStage.close();
            mainStage.saveCurrentImage();
        });

        savePane.getChildren().addAll(button, finish, name, radioButton, radioButton2, radioButton3);

    }

    private void init() {

        saveStage = new Stage();

        saveStage.setWidth(250);
        saveStage.setHeight(250);
        saveStage.setResizable(false);
        savePane = new AnchorPane();
        saveScene = new Scene(savePane, 250, 250);
        saveStage.setScene(saveScene);

        saveStage.show();

        saveStage.setOnCloseRequest(windowEvent -> ViewManager.hasColorScene = false);

    }

}
