package src.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import src.view.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage = new ViewManager().getMainStage();
        primaryStage.setTitle("DrawShot ALPHA");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
