package file;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HintWindowController implements Initializable {

    @FXML private Label titleLB;
    @FXML private TextArea contentTA;
    @FXML private ImageView statusIV;
    @FXML private Button doneBT;
    @FXML private AnchorPane pane;

    private double xOffset = 0, yOffset = 0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doneBT.setOnAction((e)->{
            ((Node)e.getSource()).getScene().getWindow().hide();
        });
    }


    public void build( String status, String title, String content){
        changeStatusIcon(status);
        titleLB.setText(title);
        contentTA.setText(content);
        Stage stage = (Stage) titleLB.getScene().getWindow();
        if (stage != null) {
            pane.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
        }
    }

    public void changeStatusIcon(String status){
        if (status.equalsIgnoreCase("error")){
            statusIV.setImage(new Image("images/error.png"));
        }else if(status.equalsIgnoreCase("warning")){
            statusIV.setImage(new Image("images/warning.png"));
        }else if(status.equalsIgnoreCase("done")){
            statusIV.setImage(new Image("images/done_icon.png"));
        }
    }


}
