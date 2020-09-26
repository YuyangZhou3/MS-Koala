package controller;

import base.Hospital;
import database.DatabaseDriver;
import util.Helper;
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

public class HospitalPageController implements Initializable, LoadDataTask {

    @FXML private TextField nameTF, addressTF, emergencyTF, aftPhoneTF;
    @FXML private TextField phoneTF, faxTF, emailTF, websiteTF;
    @FXML private Button backBT;
    @FXML private ImageView deleteIV, closeIV;
    @FXML private Label idLB;
    @FXML private AnchorPane detailPane;

    @FXML private TableView<Hospital> tableView;
    @FXML private TableColumn<Hospital,String> idTC, nameTC, addressTC, emergencyTC, phoneTC;

    @FXML private TextField searchTF;
    @FXML private Label countLB;

    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;

    private ObservableList<Hospital> hospitals;
    private Task<Integer> loadTask;
    private Hospital hospital;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTask = new LoadingTask(this);
        initTable();
        initEvent();
    }

    private void initTable() {
        tableView.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        idTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        phoneTC.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        addressTC.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        emergencyTC.setCellValueFactory(new PropertyValueFactory<>("emergencyDept"));
        emergencyTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");

        tableView.setRowFactory(tb->{
            TableRow<Hospital> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()){
                    hospital = row.getItem();
                    displayDetail();
                }
            });
            return row;
        });
    }

    private void initEvent(){
        backBT.setOnAction((e)->{
            detailPane.setVisible(false);
        });
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            detailPane.setVisible(false);
        });
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            dataFilter();
            countLB.setText(filteredList.size()+"");
        }));
        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            HintDialog hintDialog = new HintDialog((Stage) idLB.getScene().getWindow());
            Button confirmBt = new Button("YES [DELETE]");
            confirmBt.setOnAction((e)->{
                try {
                    hintDialog.hide();
                    DatabaseDriver.deleteData("Hospital" , hospital.getId());
                    hospitals.remove(hospital);
                    detailPane.setVisible(false);
                    hospital = null;
                } catch (SQLException throwables) {
                    Helper.displayHintWindow((Stage) idLB.getScene().getWindow(),"error", "Delete failed",
                            "Reason: " + throwables.getMessage());
                }
            });
            hintDialog.setOptionButton(new Button[]{confirmBt});
            hintDialog.buildAndShow("warning", "Delete the Hospital information?","The Hospital information will be deleted. This operation cannot be undone!" +
                    "\nAre you sure to delete the Hospital?");
        });
    }
    private void afterLoad(){
        countLB.setText(filteredList.size()+"");
        loadProgressIndicator.progressProperty().unbind();
        loadPane.setVisible(false);
    }

    private void displayDetail(){
        detailPane.setVisible(true);
        idLB.setText("ID: " + hospital.getId());
        nameTF.setText(hospital.getName());
        addressTF.setText(hospital.getAddress());
        emergencyTF.setText(hospital.getEmergencyDept());
        aftPhoneTF.setText(hospital.getAftPhone());
        phoneTF.setText(hospital.getPhone());
        faxTF.setText(hospital.getFax());
        emailTF.setText(hospital.getEmail());
        websiteTF.setText(hospital.getWebsite());
    }

    private FilteredList<Hospital> filteredList;
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

    public void loadData(){
        if (!loadTask.isRunning()) {
            loadTask = new LoadingTask(this);
            before();
            new Thread(loadTask).start();
        }
    }

    @Override
    public void before() {
        loadPane.setVisible(true);
        loadProgressIndicator.progressProperty().bind(loadTask.progressProperty());
    }
    @Override
    public void doing() throws Exception{
        hospitals = FXCollections.observableArrayList(DatabaseDriver.getHospitals());
        filteredList = new FilteredList<>(hospitals, d -> true);
        SortedList<Hospital> sortedList = new SortedList<>(filteredList);
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
