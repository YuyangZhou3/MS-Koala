package app;
/*Author: Bowei SONG - 2020*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class MedicalSecretary extends Application {
    public static String ip = "52.63.150.111";
    public static int port = 11111;
    public static ArrayList<String> appointmentStatus = new ArrayList<>(Arrays.asList("UNCONFIRMED","CONFIRMED","CANCELLED"));


    @Override
    public void start(Stage primaryStage) throws Exception{

        Util.loadConfigFile();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainPageFXML.fxml"));

        primaryStage.setScene(new Scene((Parent) loader.load(), 1210, 660));
        MainPageController mainPageController = loader.getController();
        mainPageController.setStage(primaryStage);

        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.getScene().setFill(Color.TRANSPARENT);
        primaryStage.show();

    }




    public static void main(String[] args) {
        launch(args);
    }
}
