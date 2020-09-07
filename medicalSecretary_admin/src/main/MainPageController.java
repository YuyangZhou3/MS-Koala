package main;
/*Author: Bowei SONG*/
import database.DatabaseDriver;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML private AnchorPane topPane, contentPane;
    @FXML private ImageView clickFileIV, clickAppIV, clickDocIV, clickHosIV,clickPathIV,clickRadIV, clickUserIV;
    @FXML private ImageView fileIV, appIV, docIV, hosIV,pathIV,radIV, userIV;
    @FXML private ImageView minimizeIV, closeIV;

    private Node[] subPageNodes;
    private Object[] subControllers;
    private Object subController = null;
    private double xOffset = 0;
    private double yOffset = 0;
    private Stage primaryStage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subPageNodes = new Node[7];
        subControllers = new Object[7];

        clickFileIV.setVisible(true);
        initUI();
        loadContentPage("../file/UploadPageFXML.fxml", 0);
        initEvent();

        DatabaseDriver.connection();
    }

    public void initUI(){
        Tooltip.install(fileIV, new Tooltip("Upload dat from File"));
        Tooltip.install(appIV, new Tooltip("Appointment"));
        Tooltip.install(docIV, new Tooltip("Doctor"));
        Tooltip.install(hosIV, new Tooltip("Hospital"));
        Tooltip.install(pathIV, new Tooltip("Pathology"));
        Tooltip.install(radIV, new Tooltip("Radiology"));
        Tooltip.install(userIV, new Tooltip("Patient"));
        Tooltip.install(minimizeIV, new Tooltip("Minimize"));
        Tooltip.install(closeIV, new Tooltip("Exit"));
    }

    public void initEvent(){
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            System.exit(1);
        });
        minimizeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            primaryStage.setIconified(true);
        });

        fileIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickFileIV);
            loadContentPage("../file/UploadPageFXML.fxml", 0);
        });

        appIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickAppIV);
            loadContentPage("../appointment/AppointmentPageFXML.fxml",1);
        });
    }

    private void selectMenu(ImageView imageView){
        clickFileIV.setVisible(false);clickAppIV.setVisible(false);
        clickDocIV.setVisible(false);clickHosIV.setVisible(false);
        clickPathIV.setVisible(false);clickRadIV.setVisible(false);
        clickUserIV.setVisible(false);
        imageView.setVisible(true);
    }

    /*Dynamic load the subpage to mid-container*/
    private void loadContentPage(String fxml, int index){

        try {
            for (int i = 0; i < contentPane.getChildren().size(); i++){
                contentPane.getChildren().remove(i);
            }
            if (subPageNodes[index] == null){
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                subPageNodes[index] = loader.load();
                subControllers[index] = loader.getController();
            }
            contentPane.getChildren().add(subPageNodes[index] );
            subController = subControllers[index];
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /*drags the top bar to move the window*/
    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
        topPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        topPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
    }

}
