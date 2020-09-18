package file;

import interfaces.UploadFile;
import javafx.application.Platform;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import util.Constant;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileChangeListener extends FileAlterationListenerAdaptor implements UploadFile {
    private File file = null;
    private Object controller;
    public FileChangeListener(Object controllerObject){
        super();
        controller = controllerObject;
    }
    @Override
    public void onStart(FileAlterationObserver observer) {
        if (controller instanceof AutoUploadController c){
            Platform.runLater(()->{
                c.setStatus("RUNNING");
            });
        }
    }
    @Override
    public void onFileCreate(File file) {
        this.file = file;
        if (controller instanceof AutoUploadController c){
            Platform.runLater(()->{
                c.updateTime("Upload time: " + LocalDateTime.now().format(Constant.DATE_TIME));
            });
        }
        uploadFile(file);
        System.out.println("Create " + file);
    }

    @Override
    public void onFileChange(File file) {
        this.file = file;
        if (controller instanceof AutoUploadController c){
            Platform.runLater(()->{
                c.updateTime("Upload time: "+LocalDateTime.now().format(Constant.DATE_TIME));
            });
        }
        uploadFile(file);
        System.out.println("Changed" + file);
    }

    public void uploadFile(File file){
        TCPClient tcpClient = null;
        try {
            tcpClient = new TCPClient(this);
            Thread tcpThread = new Thread(tcpClient);
            tcpThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<File> getFileList() {
        List<File> files = new ArrayList<>();
        files.add(file);
        return files;
    }

    @Override
    public void succeeded() { }

    @Override
    public void cancel() { }

    @Override
    public void fail() { }

    @Override
    public void displayResultWindow(String type, String title, String message) {
        System.out.println(message);
    }
}
