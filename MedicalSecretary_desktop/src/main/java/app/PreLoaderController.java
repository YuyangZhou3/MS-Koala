package app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class PreLoaderController implements Initializable {

    @FXML private ProgressBar progressBar;
    @FXML private Label hintLB;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getHintLB() {
        return hintLB;
    }

}
