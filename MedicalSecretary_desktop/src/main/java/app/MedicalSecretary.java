package app;
/*Author: Bowei SONG - 2020*/
import database.DatabaseDriver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MedicalSecretary extends Application {
    public static String ip = "52.63.150.111";
    public static int port = 11111;
    public static ArrayList<String> appointmentStatus = new ArrayList<>(Arrays.asList("UNCONFIRMED","CONFIRMED","CANCELLED"));

    private Stage loaderStage = null;
    @Override
    public void start(Stage primaryStage) throws Exception{

        initPreLoader();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainPageFXML.fxml"));
        primaryStage.setScene(new Scene((Parent) loader.load(), 1210, 660));
        MainPageController mainPageController = loader.getController();
        mainPageController.setStage(primaryStage);
        longStart(mainPageController);
        ready.addListener(new ChangeListener<Boolean>(){
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1){
                if(Boolean.TRUE.equals(t1)){
                    Platform.runLater(new Runnable(){
                        public void run(){
                            loaderStage.hide();
                            primaryStage.setResizable(false);
                            primaryStage.initStyle(StageStyle.TRANSPARENT);
                            primaryStage.getScene().setFill(Color.TRANSPARENT);
                            primaryStage.show();
                        }
                    });
                }
            }
        });
    }

    private void initPreLoader() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/PreLoaderFXML.fxml"));
        loaderStage = new Stage();
        loaderStage.setScene(new Scene((Parent) loader.load(), 473, 252));
        PreLoaderController controller = loader.getController();
        ProgressBar loadBar = controller.getProgressBar();
        loadBar.progressProperty().bind(loadProgress);
        Label loadLB = controller.getHintLB();
        loadLB.textProperty().bind(loadString);

        loaderStage.setResizable(false);
        loaderStage.initStyle(StageStyle.TRANSPARENT);
        loaderStage.getScene().setFill(Color.TRANSPARENT);
        loaderStage.show();
    }

    private BooleanProperty ready = new SimpleBooleanProperty(false);
    public StringProperty loadString = new SimpleStringProperty("Start loading");
    private DoubleProperty loadProgress = new SimpleDoubleProperty(0);
    private void longStart(MainPageController mainPageController){
        Task task=new Task<Void>(){
            protected  Void call()throws Exception{
                Util.loadConfigFile();
                Platform.runLater(()->{ loadString.set("Connecting the server"); });
                DatabaseDriver.connection();

                mainPageController.openUploadFilePage();
                Platform.runLater(()->{ loadString.set("Initializing Upload File"); });
                loadProgress.setValue(10);

                mainPageController.openAppointmentPage();
                Platform.runLater(()->{ loadString.set("Loading Appointment data"); });
                loadProgress.setValue(30);

                mainPageController.openDoctorPage();
                Platform.runLater(()->{ loadString.set("Loading Doctor data"); });
                loadProgress.setValue(60);
                mainPageController.openPathologyPage();
                Platform.runLater(()->{ loadString.set("Loading Pathology data"); });
                loadProgress.setValue(70);

                mainPageController.openRadiologyPage();
                Platform.runLater(()->{ loadString.set("Loading Radiology data"); });
                loadProgress.setValue(80);

                mainPageController.openAutoUploadFilePage();
                Platform.runLater(()->{ loadString.set("Initializing Auto Upload Function");});
                loadProgress.setValue(90);

                ready.setValue(Boolean.TRUE);
                loadProgress.setValue(100);
                return null;
            }
            @Override
            protected void failed() {
                System.out.println(getException().fillInStackTrace());
            }
        };
        new Thread(task).start();
    }

  /*  public static void main(String[] args) {
        launch(args);
    }*/
}
