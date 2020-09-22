package app;
/*Author: Bowei SONG - 2020*/
import controller.SettingController;
import database.DatabaseDriver;
import helper.Helper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MedicalSecretary extends Application {
    private Stage loaderStage = null;
    private Stage mainStage = null;
    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;
        initPreLoader();
        Platform.setImplicitExit(false);
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
                            setTray();
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


    /*
    * Pre-Loader
    * */
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
                boolean connect = DatabaseDriver.connection();
                if (!connect){
                    throw new Exception("1");
                }

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

                mainPageController.openUserPage();
                Platform.runLater(()->{ loadString.set("Loading User data"); });
                loadProgress.setValue(85);

                mainPageController.openAutoUploadFilePage();
                Platform.runLater(()->{ loadString.set("Initializing Auto Upload Function");});
                loadProgress.setValue(90);

                ready.setValue(Boolean.TRUE);
                loadProgress.setValue(100);
                return null;
            }
            @Override
            protected void failed() {
                if (getException().getMessage().equals("1")){
                    FXMLLoader loader = new FXMLLoader(DatabaseDriver.class.getClassLoader().getResource("view/SettingFXML.fxml"));
                    Stage stage = new Stage();
                    try {
                        stage.setScene(new Scene((Parent) loader.load(), 631, 194));
                        SettingController controller = loader.getController();
                        stage.initOwner(loaderStage);
                        stage.setResizable(false);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.getScene().setFill(Color.TRANSPARENT);
                        stage.show();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }else {
                    Helper.displayHintWindow(loaderStage,"error","Load Failed","Reason: " + getException().getMessage());
                }
            }
        };
        new Thread(task).start();
    }
    /*
        Tray function
     */

    public void setTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/logo.png"));
            String text = "Medical Secretary";
            PopupMenu popMenu = new PopupMenu();
            MenuItem itmOpen = new MenuItem("Open Dashboard");
            itmOpen.addActionListener((ActionEvent e) -> {
                Platform.runLater(() -> {
                    mainStage.show();
                });
            });
            MenuItem itmExit = new MenuItem("Close Application");
            itmExit.addActionListener((ActionEvent e) -> {
                System.exit(1);
            });
            popMenu.add(itmOpen);
            popMenu.add(itmExit);

            TrayIcon trayIcon = new TrayIcon(image, text, popMenu);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
        }
    }
  /*  public static void main(String[] args) {
        launch(args);
    }*/
}
