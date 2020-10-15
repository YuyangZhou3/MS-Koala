package file;

import app.MainPageController;
import interfaces.LoadDataTask;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import util.Constant;
import util.Helper;
import util.LoadingTask;
import util.Util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AutoUploadController implements Initializable, LoadDataTask {

    @FXML private TextField pathTF;
    @FXML private Button changeBT;
    @FXML private Label statusLB, timeLB;
    @FXML private Button uploadBt,runBT;

    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    private FileListenerFactory fileListenerFactory;
    private FileAlterationMonitor monitor;
    private Task<Integer> loadTask;
    private File dir = null;
    private boolean isRunning;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTask = new LoadingTask(this);
        pathTF.setText(Constant.AUTO_PATH);
        fileListenerFactory = new FileListenerFactory();
        statusLB.setText("STOPPED");
        if (Constant.AUTO_PATH != null && new File(Constant.AUTO_PATH).isDirectory()){
            monitor = fileListenerFactory.getMonitor(this, Constant.AUTO_PATH);
            try {
                monitor.start();
                setRunning(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }else {
            setRunning(false);
        }
        initEvent();
    }

    private void setRunning(boolean isRunning){
        this.isRunning = isRunning;
        if (isRunning){
            statusLB.setText("RUNNING");
            runBT.setText("STOP");
            runBT.setStyle("-fx-background-color: #d3685c; -fx-border-color: #d3685c; -fx-background-radius:10; -fx-border-radius:10");
        }else {
            statusLB.setText("STOPPED");
            runBT.setText("RUN");
            runBT.setStyle("-fx-background-color: #57da6f; -fx-border-color: #57da6f; -fx-background-radius:10; -fx-border-radius:10");
        }
    }

    private MainPageController mainPageController;
    public void setParentController(MainPageController mainPageController){
        this.mainPageController = mainPageController;
    }

    private void initEvent(){
        uploadBt.setOnAction((e)->{
            mainPageController.openUploadFilePage();
        });
        runBT.setOnAction(e->{
            try {
                if (Constant.AUTO_PATH == null || !new File(Constant.AUTO_PATH).isDirectory()){
                    throw new Exception("Does not select the folder");
                }
                setRunning(!isRunning);
                System.out.println(isRunning);
                if (!loadTask.isRunning()) {
                    loadTask = new LoadingTask(this);
                    before();
                    new Thread(loadTask).start();
                }
            } catch (Exception exception) {
                Helper.displayHintWindow((Stage) statusLB.getScene().getWindow(),"Error","Start or Stop Error", exception.getMessage());
            }
        });
        changeBT.setOnAction((e)->{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");
            File file = directoryChooser.showDialog(changeBT.getScene().getWindow());
            if (file != null){
                dir = file;
                Constant.AUTO_PATH = file.getAbsolutePath();
                pathTF.setText(file.getAbsolutePath());
            }
        });
    }

    public void setStatus(String status){
        statusLB.setText(status);
    }

    public void updateTime(String time){
        timeLB.setText(time);
    }

    @Override
    public void before() {
        loadPane.setVisible(true);
        loadProgressIndicator.progressProperty().bind(loadTask.progressProperty());
    }

    @Override
    public void doing() throws Exception {
        if (monitor != null && !isRunning){
            monitor.stop(100);
        }
        if (isRunning) {
            monitor = fileListenerFactory.getMonitor(this, Constant.AUTO_PATH);
            monitor.start();
            Util.writeConfigFile("uploadPath", Constant.AUTO_PATH);
        }
    }

    @Override
    public void done() {
        if (isRunning && dir != null) {
            pathTF.setText(dir.getAbsolutePath());
            Constant.AUTO_PATH = dir.getAbsolutePath();
        }
        loadProgressIndicator.progressProperty().unbind();
        loadPane.setVisible(false);
    }

    @Override
    public void failed() {
        loadProgressIndicator.progressProperty().unbind();
        loadPane.setVisible(false);
        setRunning(!isRunning);

    }

    @Override
    public void cancelled() {
        loadProgressIndicator.progressProperty().unbind();
        loadPane.setVisible(false);
        setRunning(!isRunning);
    }
}
