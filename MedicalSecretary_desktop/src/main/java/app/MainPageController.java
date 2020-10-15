package app;
/*Author: Bowei SONG*/
import controller.AppointmentPageController;
import controller.*;
import file.AutoUploadController;
import file.UploadPageController;
import util.Helper;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.Constant;
import util.HintDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML private AnchorPane topPane, contentPane;
    @FXML private ImageView clickFileIV, clickAppIV, clickDocIV, clickHosIV,clickPathIV,clickRadIV, clickUserIV;
    @FXML private ImageView fileIV, appIV, docIV, hosIV,pathIV,radIV, userIV;
    @FXML private ImageView minimizeIV, closeIV;
    @FXML private ImageView settingIV;
    @FXML private HBox settingBox;
    @FXML private TextField ipTF, portTF;
    @FXML private Button changeBT, backBT;
    @FXML private Label ipLB, portLB;
    @FXML private Button refreshBT;

    private Node[] subPageNodes;
    private Object[] subControllers;
    private int pageIndex = 0;
    private Object subController = null;
    private double xOffset = 0;
    private double yOffset = 0;
    private Stage primaryStage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subPageNodes = new Node[8];
        subControllers = new Object[8];
        clickFileIV.setVisible(true);
        ipLB.setText("Server IP: "+Constant.ip);
        portLB.setText("Port: "+Constant.port);
        ipTF.setText(Constant.ip); portTF.setText(Constant.port+"");
        initUI();
        initEvent();
    }

    public void initUI(){
        Tooltip.install(fileIV, new Tooltip("Upload data from File"));
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
            HintDialog hintDialog = new HintDialog(primaryStage);
            Button exitBt = new Button("EXIT");
            exitBt.setOnAction((e)->{
                System.exit(1);
            });
            Button minBt = new Button("MINIMIZE");
            minBt.setOnAction((e)->{
                primaryStage.hide();
                hintDialog.hide();
            });
            hintDialog.setOptionButton(new Button[]{exitBt, minBt});
            hintDialog.buildAndShow("Warning", "Close the Application",
                    "[EXIT] The application will be closed, the function of auto upload will be terminated.\n" +
                            "[MINIMIZE] The application will be hidden. Reopen via tray\n[CLOSE] Close the hint window");
        });
        minimizeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            primaryStage.hide();
        });
        refreshBT.setOnAction(e->{
            switch (pageIndex){
                case 1:
                    ((AppointmentPageController)subController).loadData();
                    break;
                case 2:
                    ((DoctorPageController)subController).loadData();
                    break;
                case 3:
                    ((HospitalPageController)subController).loadData();
                    break;
                case 4:
                    ((PathologyPageController)subController).loadData();
                    break;
                case 5:
                    ((RadiologyPageController)subController).loadData();
                    break;
                case 6:
                    ((UserPageController)subController).loadData();
                    break;
            }
        });

        fileIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickFileIV);
            openAutoUploadFilePage();
        });
        appIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickAppIV);
            openAppointmentPage();
        });
        docIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickDocIV);
            openDoctorPage();
        });
        hosIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickHosIV);
            openHospitalPage();
        });
        pathIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickPathIV);
            openPathologyPage();
        });
        radIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickRadIV);
            openRadiologyPage();
        });
        userIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            selectMenu(clickUserIV);
            openUserPage();
        });

        settingIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            settingBox.setVisible(true);
        });
        backBT.setOnAction((e)->{
            settingBox.setVisible(false);
        });
        changeBT.setOnAction((e)->{
            try {
                String ip = ipTF.getText().trim();
                int port = Integer.parseInt(portTF.getText().trim());
                if (!ip.equalsIgnoreCase(Constant.ip) || port != Constant.port){
                    HintDialog hintDialog = new HintDialog((Stage) ipLB.getScene().getWindow());
                    Button confirmBt = new Button("YES [CHANGE]");
                    confirmBt.setOnAction((event)->{
                        hintDialog.hide();
                        try {
                            util.Util.writeConfigFile("serverIP", ip);
                            util.Util.writeConfigFile("TCPPort", port + "");
                            System.exit(1);
                        }catch (Exception exception){
                            Helper.displayHintWindow((Stage) ipLB.getScene().getWindow(),"error", "Change failed",
                                    "Reason: " + exception.getMessage());
                        }

                    });
                    hintDialog.setOptionButton(new Button[]{confirmBt});
                    hintDialog.buildAndShow("warning", "Change the server IP and Port?","The application need to restart" +
                            "\nAre you sure to change the ip and port?");
                }
                else {
                    settingBox.setVisible(false);
                }
            }catch (Exception exception){
                Helper.displayHintWindow((Stage) ipLB.getScene().getWindow(),"error", "Change failed",
                    "Reason: " + exception.getMessage());
            }

        });
    }
    private MainPageController getThis(){
        return this;
    }
    public void openUploadFilePage(){
        loadContentPage("view/UploadPageFXML.fxml", 7);
        ((UploadPageController)subController).setParentController(getThis());
        pageIndex = 7;
    }
    public void openAutoUploadFilePage(){
        loadContentPage("view/AutoUploadFXML.fxml", 0);
        ((AutoUploadController)subController).setParentController(getThis());
        pageIndex = 0;
    }
    public void openAppointmentPage(){
        loadContentPage("view/AppointmentPageFXML.fxml",1);
        ((AppointmentPageController)subController).loadData();
        pageIndex = 1;
    }
    public void openDoctorPage(){
        loadContentPage("view/DoctorPageFXML.fxml",2);
        ((DoctorPageController)subController).loadData();
        pageIndex = 2;
    }
    public void openHospitalPage(){
        loadContentPage("view/HospitalPageFXML.fxml",3);
        ((HospitalPageController)subController).loadData();
        pageIndex = 3;
    }
    public void openPathologyPage(){
        loadContentPage("view/PathologyPageFXML.fxml",4);
        ((PathologyPageController)subController).loadData();
        pageIndex = 4;
    }
    public void openRadiologyPage(){
        loadContentPage("view/RadiologyPageFXML.fxml",5);
        ((RadiologyPageController)subController).loadData();
        pageIndex = 5;
    }
    public void openUserPage(){
        loadContentPage("view/UserPageFXML.fxml",6);
        ((UserPageController)subController).loadData();
        pageIndex = 6;
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
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
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
