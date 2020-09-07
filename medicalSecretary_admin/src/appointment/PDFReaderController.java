package appointment;

import base.Appointment;
import database.DatabaseDriver;
import file.HintWindowController;
import file.TCPClient;
import helper.HintDialog;
import interfaces.UploadFile;
import helper.Helper;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.MedicalSecretary;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PDFReaderController implements Initializable, UploadFile {

    @FXML private Label nameLB;
    @FXML private ImageView fileIV, deleteIV,pdfIV, closeIV;
    @FXML private Button updateBT, cancelBT;
    @FXML private HBox fileBox;
    @FXML private ProgressIndicator loadPD;
    @FXML private AnchorPane loadPane,topPane;

    private Appointment appointment;
    private Stage stage;
    private File pdfFile;
    private HintDialog hintDialog;
    private Button deleteBt;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pdfFile = null;
        initEvent();
        initTask();
    }

    public void open(Appointment appointment) throws IOException {
        this.appointment = appointment;
        stage =(Stage) nameLB.getScene().getWindow();
        Helper.initTopBar(stage, topPane);
        if (appointment.getFileID() == null){
            pdfIV.setVisible(false);
        }else {
            nameLB.setText(appointment.getReport());
        }
    }

    private void downLoadFile() throws IOException {
        String url = "http://" + MedicalSecretary.ip + ":8080/medsec/api/" + "file/link/" + appointment.getFileID();
        System.out.println(url);
        URL requestURL = new URL(url);
        System.out.println(requestURL.getAuthority());
        HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
        urlConnection.addRequestProperty("Authorization","Bearer eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiUEFUSUVOVCIsImp0aSI6Ijg3YWFicGJjdDBhaDlwdWZxdG0zYjJ0ajI2IiwiZXhwIjoxNTk5NDc4MDMyLCJpYXQiOjE1OTkzOTE2MzIsInN1YiI6IjEifQ.4H9FcX0ado7-WBq4AFmGEjl3siXHEm5iQEPvuRX1MCdv_fGrUWy5ho7YI7gftTqEs7H4UhhuT6xJx1vM-kWcQQ");
        urlConnection.connect();
        if(200==urlConnection.getResponseCode())
        {
            InputStream ins = urlConnection.getInputStream();

            pdfFile = new File("temp/"+appointment.getReport());
            File parentFile = new File("temp");
            if (!parentFile.exists()) parentFile.mkdir();
            //pdfFile.mkdir();
            OutputStream os = new FileOutputStream(pdfFile);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
            urlConnection.disconnect();
        }else {
            int code = urlConnection.getResponseCode();
            final String message = urlConnection.getResponseMessage();
            throw new IOException(code + " - " + message);
        }
    }


    private Task<Integer> loadTask;
    private void initTask(){
        loadTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                downLoadFile();
                return 0;
            }
            @Override
            protected void succeeded() {
                Runtime.getRuntime().gc();
                openPDF();
                loadPane.setVisible(false);
                loadPD.progressProperty().unbind();
            }
            @Override
            protected void cancelled() {
                loadPane.setVisible(false);
                loadPD.progressProperty().unbind();
            }
            @Override
            protected void failed() {
                Helper.displayHintWindow(stage, "error","Download Failed","The report file cannot be downloaded.\n"
                        +"\nReason: " + getException().getMessage());
                loadPane.setVisible(false);
                loadPD.progressProperty().unbind();

            }
        };
    }
    private void openPDF(){
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("rundll32 url.dll FileProtocolHandler " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            Helper.displayHintWindow(stage, "error","loading file Failed","The report file cannot be opened.\n"
                    +"\nReason: " + e.getMessage());
        }
    }

    public void initEvent() {
        //select a file from computer
        fileIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF File", "*.pdf"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null){
                pdfIV.setVisible(true);
                nameLB.setText(file.getName());
                pdfFile = file;
            }
        });
        //click the pdf image to open the file
        pdfIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            if (pdfFile == null){
                loadPane.setVisible(true);
                loadPD.progressProperty().bind(loadTask.progressProperty());
                loadTask.run();
            }else {
                openPDF();
            }
        });
        //close the windows and remove the temp files
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            if (loadTask.isRunning()){
                loadTask.cancel();
            }Helper.clearTempFiles();
            stage.hide();

        });
        //close the windows
        cancelBT.setOnAction((e)->{
            Helper.clearTempFiles();
            stage.hide();
        });
        //upload the new file
        updateBT.setOnAction((e)->{
            try {
                if (pdfFile != null) {
                    pdfFile = copy(pdfFile.getAbsolutePath(), "temp/File-report-"+appointment.getId()+".pdf", 8192);
                    loadPane.setVisible(true);
                    TCPClient tcpClient = new TCPClient(this);
                    loadPD.progressProperty().bind(tcpClient.progressProperty());
                    Thread tcpThread = new Thread(tcpClient);
                    tcpThread.start();
                }
            } catch (IOException e1) {
                Helper.displayHintWindow(stage, "Error", "Upload Fail" , "Reason: " + e1.getMessage());
                e1.printStackTrace();
            }
        });
        //delete the file
        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            if (hintDialog == null){
                hintDialog = new HintDialog(getStage());
            }
            if (deleteBt == null){
                deleteBt = new Button("Yes [DELETE]");
                deleteBt.setOnAction((e)->{
                    removeFile();
                });
            }
            hintDialog.setOptionButton(new Button[]{deleteBt});
            hintDialog.buildAndShow("warning", "Delete the report file",
                    "The report file will be deleted. This operation cannot be undone!" +
                            "\nAre you sure to delete the report file with the appointment?");
        });

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
                    File file = db.getFiles().get(0);
                    String filename = file.getName();
                    String ext = filename.substring(filename.lastIndexOf(".") + 1);
                    if (ext.equalsIgnoreCase("pdf")) {
                        pdfIV.setVisible(true);
                        nameLB.setText(filename);
                        pdfFile = file;
                    } else {
                        Helper.displayHintWindow(stage, "warning", "Invalid files in the current selection",
                                "The extension of report file must be PDF.\n");
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    private void removeFile(){
        hintDialog.hide();
        try {
            DatabaseDriver.deleteAppoint(appointment.getFileID(), appointment.getId());
            pdfIV.setVisible(false);
            nameLB.setText("None");
            pdfFile = null;
            appointment.setReport("None");
            appointment.setFileLink(null);
            appointment.setFileID(null);
        } catch (SQLException e) {
            Helper.displayHintWindow(stage, "Error", "Operation Failed",
                    "The report file did not removed.\n\nReason: " + e.getMessage());
        }
    }

    @Override
    public List<File> getFileList() {
        List<File> files = new ArrayList<>();
        if (pdfFile!= null) files.add(pdfFile);
        return files;
    }

    @Override
    public void succeeded() {
        appointment.setReport(pdfFile.getName());
        Helper.clearTempFiles();
    }

    @Override
    public Node getLoadingPane() {
        return loadPane;
    }

    @Override
    public ProgressIndicator getLoadingProgress() {
        return loadPD;
    }

    @Override
    public void displayResultWindow(String type, String title, String message) {
        HintWindowController controller = (HintWindowController) Helper.openSubWindow(null, (Stage) fileBox.getScene().getWindow() , "", "../file/HintWindowFXML.fxml");
        controller.build( type, title , message);
    }

    public File copy(String source, String dest, int bufferSize) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        File outFile = new File(dest);
        try {
            in = new FileInputStream(new File(source));
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[bufferSize];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }finally {
            in.close();
            out.close();
            Runtime.getRuntime().gc();
            return outFile;
        }
    }

    public Stage getStage() {
        return stage;
    }
}
