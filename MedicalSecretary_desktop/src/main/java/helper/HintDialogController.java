package helper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HintDialogController implements Initializable {
    @FXML private Label titleLB;
    @FXML private TextArea contentTA;
    @FXML private HBox buttonBox;
    @FXML private Button closeBT;
    @FXML private ImageView closeIV, statusIV;
    @FXML private AnchorPane pane;

    private String buttonStyle;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonStyle = "-fx-background-color:  #249FE4; -fx-background-radius: 20; -fx-font-size: 13; -fx-text-fill: white;";
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
           titleLB.getScene().getWindow().hide();
        });
        closeBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            titleLB.getScene().getWindow().hide();
        });
    }

    public void build(String type, String title, String message){
        titleLB.setText(title);
        contentTA.setText(message);
        changeStatusIcon(type);
        Helper.initTopBar((Stage) titleLB.getScene().getWindow(), pane);
    }

    public void setOptionButton(Button[] buttons){
        for (Button button: buttons){
            button.setStyle(buttonStyle);
            button.setPrefHeight(35);
            button.setPrefWidth(149);
            button.setCursor(Cursor.HAND);
            buttonBox.getChildren().add(button);
        }
        buttonBox.getChildren().remove(closeBT);
        buttonBox.getChildren().add(closeBT);
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
