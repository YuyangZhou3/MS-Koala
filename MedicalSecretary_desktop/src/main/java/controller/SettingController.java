package controller;

import helper.Helper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Constant;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingController implements Initializable {
    @FXML private TextField ipTF, portTF;
    @FXML private Button changeBT, backBT;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ipTF.setText(Constant.ip);
        portTF.setText(Constant.port+"");

        changeBT.setOnAction((e)->{
            try {
                String ip = ipTF.getText().trim();
                int port = Integer.parseInt(portTF.getText().trim());
                util.Util.writeConfigFile("serverIP", ip);
                util.Util.writeConfigFile("TCPPort", port + "");
                System.exit(1);
            }catch (Exception exception){
                Helper.displayHintWindow((Stage) ipTF.getScene().getWindow(),"error", "Change failed",
                        "Reason: " + exception.getMessage());
            }

        });

        backBT.setOnAction((e)->{
            System.exit(1);
        });
    }
}
