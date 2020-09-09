package base;

import file.QueryCommand;
import javafx.scene.layout.AnchorPane;

import java.io.File;

public class UploadFile {
    private File file;
    private String extension;
    private AnchorPane filePane;
    private QueryCommand type;

    public UploadFile(File file, String extension, AnchorPane filePane, QueryCommand type) {
        this.file = file;
        this.extension = extension;
        this.filePane = filePane;
        this.type = type;
    }

    public String getPath(){
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public AnchorPane getFilePane() {
        return filePane;
    }

    public void setFilePane(AnchorPane filePane) {
        this.filePane = filePane;
    }

    public QueryCommand getType() {
        return type;
    }

    public void setType(QueryCommand type) {
        this.type = type;
    }
}
