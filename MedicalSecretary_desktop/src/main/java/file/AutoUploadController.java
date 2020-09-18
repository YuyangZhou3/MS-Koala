package file;

import app.MainPageController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import util.Constant;
import util.Util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AutoUploadController implements Initializable {

    @FXML private TextField pathTF;
    @FXML private Button changeBT;
    @FXML private Label statusLB, timeLB;
    @FXML private Button uploadBt;

    private FileListenerFactory fileListenerFactory;
    private FileAlterationMonitor monitor;
    private Thread monitorThread;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pathTF.setText(Constant.AUTO_PATH);
        fileListenerFactory = new FileListenerFactory();
        statusLB.setText("STOPPED");
        if (Constant.AUTO_PATH != null && new File(Constant.AUTO_PATH).isDirectory()){
            monitor = fileListenerFactory.getMonitor(this, Constant.AUTO_PATH);
            try {
                monitor.start();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        initEvent();
    }

    private MainPageController mainPageController;
    public void setParentController(MainPageController mainPageController){
        this.mainPageController = mainPageController;
    }

    private void initEvent(){
        uploadBt.setOnAction((e)->{
            mainPageController.openUploadFilePage();
        });

        changeBT.setOnAction((e)->{
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");
            File file = directoryChooser.showDialog(changeBT.getScene().getWindow());
            if (file != null){
                Constant.AUTO_PATH = file.getAbsolutePath();
                pathTF.setText(file.getAbsolutePath());
                try {
                    if (monitor != null){
                        monitor.stop();
                    }
                    monitor = fileListenerFactory.getMonitor(this, Constant.AUTO_PATH);
                    monitor.start();
                    Util.writeConfigFile();
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        });
    }

    public void setStatus(String status){
        statusLB.setText(status);
    }

    public void updateTime(String time){
        timeLB.setText(time);
    }
}
