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
    public abstract Node getLoadingPane();
    public abstract ProgressIndicator getLoadingProgress();

    public abstract void displayResultWindow(String type, String title, String message);
}
