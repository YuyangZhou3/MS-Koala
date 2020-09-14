package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HospitalPageController implements Initializable {

    @FXML private TextField nameTF, addressTF, emergencyTF, aftPhoneTF;
    @FXML private TextField phoneTF, faxTF, emailTF, websiteTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
