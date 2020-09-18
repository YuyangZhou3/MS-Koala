package interfaces;

import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.List;

public interface UploadFile {
    public abstract List<File> getFileList();
    public abstract void succeeded();
    public abstract void cancel();
    public abstract void fail();

    public abstract void displayResultWindow(String type, String title, String message);
}
