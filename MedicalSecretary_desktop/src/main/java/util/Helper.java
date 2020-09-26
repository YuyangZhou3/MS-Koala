package util;

import file.HintWindowController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Helper {
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Object openSubWindow(Stage subStage, Stage Parent, String title, String fxml){
        if(subStage == null){
            subStage = new Stage();
            subStage.setResizable(false);
            subStage.initModality(Modality.WINDOW_MODAL);
            subStage.initStyle(StageStyle.TRANSPARENT);
            if (Parent != null) subStage.initOwner(Parent);
            return open(subStage,title, fxml);
        }
        else {
            return open(subStage, title, fxml);
        }
    }

    private static Object open(Stage subStage,  String title, String fxml){
        subStage.setTitle(title);
        try {
            Object subController = replaceSceneContent(fxml, subStage);
            subStage.show();
            return subController;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static Initializable replaceSceneContent(String fxml, Stage stage) throws Exception {
        URL url = Helper.class.getClassLoader().getResource(fxml);
        FXMLLoader loader = new FXMLLoader(url);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        AnchorPane page = loader.load();
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        if (loader.getController() == null) System.out.println("haha");
        return loader.getController();
    }

    public static void displayErrorWindow(String title, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**/
    public static void initTopBar(Stage stage, AnchorPane topPane){
        if (stage != null) {
            final double[] xOffset = {0};
            final double[] yOffset = {0};
            topPane.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset[0] = event.getSceneX();
                    yOffset[0] = event.getSceneY();
                }
            });
            topPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset[0]);
                    stage.setY(event.getScreenY() - yOffset[0]);
                }
            });
        }
    }

    public static void clearTempFiles(){
        File directory = new File("temp");
        if (directory.isDirectory()){
            File[] files = directory.listFiles();
            for (File file : files ){
                file.delete();
            }
        }
    }

    /**/
    public static void displayHintWindow(Stage stage, String status, String title, String content){
        HintWindowController controller = (HintWindowController) openSubWindow(null, stage , "", "view/HintWindowFXML.fxml");
        controller.build( status, title , content);
    }

    /*获取当月天数数组*/
    public static ArrayList<String> getDayOfMonth(int year, int month){
        ArrayList<String> days = new ArrayList<>();
        LocalDate localDate = null;
        if (month < 10){
            localDate = LocalDate.parse(year+"-0"+month+"-01", dtf);
        }
        else localDate = LocalDate.parse(year+"-"+month+"-01", dtf);

        int dd = localDate.lengthOfMonth();

        days.add("ALL");
        for (int i = 1; i <= dd; i++){
            days.add(i+"");
        }
        return days;
    }
}
