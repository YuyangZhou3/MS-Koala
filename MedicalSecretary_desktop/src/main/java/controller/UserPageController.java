package controller;

import base.Patient;
import database.DatabaseDriver;
import helper.Helper;
import interfaces.LoadDataTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.HintDialog;
import util.LoadingTask;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserPageController implements Initializable, LoadDataTask {

    @FXML private TextField firstNameTF,midNameTF,surnameTF;
    @FXML private TextField idTF, dobTF, emailTF, streetTF, suburbTF, stateTF;
    @FXML private ImageView deleteIV;
    @FXML private AnchorPane coverPane;
    @FXML private Button backBT;

    @FXML private TableView<Patient> tableView;
    @FXML private TableColumn<Patient, String> idTC, nameTC, dobTC,stateTC;

    @FXML private TextField searchTF;
    @FXML private Label countLB;
    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    private ObservableList<Patient> patients;
    private Task<Integer> loadTask;
    private Patient patient = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTask = new LoadingTask(this);
        initTable();
        initEvent();
    }
    public void loadData(){
        if (!loadTask.isRunning()) {
            loadTask = new LoadingTask(this);
            before();
            new Thread(loadTask).start();
        }
    }

    private void initTable() {
        tableView.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        idTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        dobTC.setCellValueFactory(new PropertyValueFactory<>("dob"));
        dobTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        stateTC.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");

        tableView.setRowFactory(tb->{
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 1 && !row.isEmpty()){
                    patient = row.getItem();
                    displayDetail();
                }
            });
            return row;
        });
    }
    private void displayDetail(){
        coverPane.setVisible(false);
        midNameTF.setText(patient.getMidName());
        firstNameTF.setText(patient.getFirstName());
        surnameTF.setText( patient.getSurname());
        idTF.setText(patient.getId());
        dobTF.setText(patient.getDob());
        streetTF.setText(patient.getStreet());
        suburbTF.setText(patient.getSuburb());
        stateTF.setText(patient.getState());
        emailTF.setText(patient.getEmail());
    }

    private void initEvent(){
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            coverPane.setVisible(true);
            dataFilter();
            countLB.setText(filteredList.size()+"");
        }));
        backBT.setOnAction((e)->{
            coverPane.setVisible(true);
            patient = null;
        });
        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            HintDialog hintDialog = new HintDialog((Stage) countLB.getScene().getWindow());
            Button confirmBt = new Button("YES [DELETE]");
            confirmBt.setOnAction((e)->{
                try {
                    hintDialog.hide();
                    DatabaseDriver.deleteData("User" , patient.getId());
                    patients.remove(patient);
                    coverPane.setVisible(true);
                    patient = null;
                } catch (SQLException throwables) {
                    Helper.displayHintWindow((Stage) countLB.getScene().getWindow(),"error", "Delete failed",
                            "Reason: " + throwables.getMessage());
                }
            });
            hintDialog.setOptionButton(new Button[]{confirmBt});
            hintDialog.buildAndShow("warning", "Delete the User information?","The User information will be deleted. This operation cannot be undone!" +
                    "\nAre you sure to delete the User?");
        });
    }

    private FilteredList<Patient> filteredList;
    private void dataFilter(){
        String searchLine = searchTF.getText().toLowerCase().trim();
        filteredList.setPredicate(a->{
            if (searchLine.isEmpty())return true;
            if ( a.getName().toLowerCase().contains(searchLine)){
                return true;
            } else {
                return false;
            }
        });
    }
    private void afterLoad(){
        countLB.setText(filteredList.size()+"");
        loadProgressIndicator.progressProperty().unbind();
        loadPane.setVisible(false);
    }
    @Override
    public void before() {
        loadPane.setVisible(true);
        loadProgressIndicator.progressProperty().bind(loadTask.progressProperty());
    }

    @Override
    public void doing() throws Exception {
        patients = FXCollections.observableArrayList(DatabaseDriver.getPatients());
        filteredList = new FilteredList<>(patients, d -> true);
        SortedList<Patient> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
        dataFilter();
    }

    @Override
    public void done() {
        afterLoad();
    }

    @Override
    public void failed() {
        afterLoad();
    }

    @Override
    public void cancelled() {
        afterLoad();
    }
}
