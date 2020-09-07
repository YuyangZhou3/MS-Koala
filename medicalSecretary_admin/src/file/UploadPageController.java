package file;

import helper.Helper;
import interfaces.UploadFile;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UploadPageController implements Initializable, UploadFile {
    @FXML private HBox fileBox;
    @FXML private ImageView addIconIV,submitIV;
    @FXML private VBox loadingPane;
    @FXML private ProgressIndicator loadingProgress;

    private List<File> fileList;
    private boolean incorrectFileType, incorrectFileName;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        incorrectFileType = false;
        incorrectFileName = false;
        fileList = new ArrayList<>();
        initEvent();

        submitIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            if (fileList.size() == 0){
                displayResultWindow("warning", "Not found File", "");
            }else {
                loadingPane.setVisible(true);
                submit();
            }

        });
    }

    public void submit(){
        try {
            TCPClient tcpClient = new TCPClient(this);
            loadingProgress.progressProperty().bind(tcpClient.progressProperty());
            Thread tcpThread = new Thread(tcpClient);
            tcpThread.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void succeeded(){
        fileBox.getChildren().clear();
        fileList.clear();
        addIconIV.setVisible(true);
    }
    public List<File> getFileList(){
        return fileList;
    }

    public HBox getFileBox(){
        return fileBox;
    }

    public ProgressIndicator getLoadingProgress() {
        return loadingProgress;
    }

    public VBox getLoadingPane() {
        return loadingPane;
    }

    public void initEvent(){
        fileBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != fileBox
                        && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        fileBox.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    List<File> files = db.getFiles();
                    for (File file : files){
                        String filename = file.getName();
                        try {
                            QueryCommand type = QueryCommand.getCommandName(file.getName());
                            if (type == null) throw new Exception();
                            String ext = filename.substring(filename.lastIndexOf(".") + 1);
                            if (ext.equalsIgnoreCase("html") ){
                                addFile(file, filename, "images/HTML.png");
                                fileList.add(file);
                            }else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")){
                                addFile(file, filename, "images/xlsx.png");
                                fileList.add(file);
                            }else if(ext.equalsIgnoreCase("pdf")){
                                addFile(file, filename, "images/pdf.png");
                                fileList.add(file);
                            }else{
                                incorrectFileType = true;
                            }
                        }catch (Exception e){
                            incorrectFileName = true;
                        }
                    }
                    if (fileList.size()>0){
                        addIconIV.setVisible(false);
                    }else addIconIV.setVisible(true);

                    if (incorrectFileName || incorrectFileType){
                        String content = "";
                        if (incorrectFileName) content += "Please upload the file named with one of the QueryCommand: Appointment', 'Patient', 'Doctor', 'Hospital', 'Pathology', 'Radiology','Resource', or 'File'\n";
                        if (incorrectFileType) content += "Please upload the file with '.html', '.xls' extension for uploading the Genie data or with '.pdf' extension named with 'File' for uploading reports to users.";
                        displayResultWindow("warning", "Invalid files in the current selection", content);
                    }
                    incorrectFileType = incorrectFileName = false;
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();


            }
        });
    }

    private void addFile(File file, String filename, String url){
        VBox fileCard = new VBox(5);
        fileCard.setCursor(Cursor.HAND);
        fileCard.setAlignment(Pos.CENTER);
        ImageView picture = new ImageView(url);
        picture.setFitWidth(150); picture.setFitHeight(137);
        Label label = new Label(filename);
        fileCard.getChildren().addAll(picture, label);
        fileBox.getChildren().addAll(fileCard);

        fileCard.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            fileBox.getChildren().remove(fileCard);
            fileList.remove(file);
            if (fileList.size() == 0){
                addIconIV.setVisible(true);
            }
        });
    }

    public void displayResultWindow(String status, String title, String content){
        HintWindowController controller = (HintWindowController) Helper.openSubWindow(null, (Stage) fileBox.getScene().getWindow() , "", "../file/HintWindowFXML.fxml");
        controller.build( status, title , content);
    }


}
