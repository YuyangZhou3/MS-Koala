package controller;

import base.Doctor;
import database.DatabaseDriver;
import helper.Helper;
import util.Constant;
import util.HintDialog;
import util.LoadingTask;
import interfaces.LoadDataTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DoctorPageController implements Initializable, LoadDataTask {

    //information UI
    @FXML private Label countLB;
    @FXML private TextField searchTF;
    @FXML private RadioButton nameRB, expertiseRB;
    //Table UI
    @FXML private TableView<Doctor> doctorTableView;
    @FXML private TableColumn<Doctor, String> idTC, nameTC, phoneTC, expertiseTC;
    //Detail UI
    @FXML private AnchorPane coverPane;
    @FXML private Label idLB;
    @FXML private TextField nameTF, bioTF,phoneTF, addressTF, FaxTF, emailTF,websiteTF, expertiseTF;
    @FXML private Button editBT, backBT;
    @FXML private ImageView deleteIV;
    //loading ui
    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;

    private ObservableList<Doctor> doctors;
    private FilteredList<Doctor> filteredList;
    private LoadingTask loadingTask;
    private boolean isEditable;
    private Doctor selectDoctor;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadingTask = new LoadingTask(this);
        isEditable = false;
        selectDoctor = null;
        initEvent();
        initTableUI();
        //loadData();
    }

    public void loadData(){
        if (Constant.updateDoctor){
            Constant.updateDoctor = false;
            if (!loadingTask.isRunning()) {
                loadingTask.start();
            }
        }
    }

    private void initEvent(){
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            dataFilter();
            countLB.setText(filteredList.size()+"");
        }));
        backBT.setOnAction((e)->{
            if (isEditable){
                displayDetail(selectDoctor);
                setEditable(false);
            }
            else {
                coverPane.setVisible(true);
            }
        });
        editBT.setOnAction((e)->{
            if (isEditable){
                try {
                    selectDoctor.updateDataToDatabase(nameTF.getText(), emailTF.getText(), bioTF.getText(),
                            phoneTF.getText(), addressTF.getText(), FaxTF.getText(), websiteTF.getText(),
                            expertiseTF.getText());
                    setEditable(false);
                } catch (Exception throwables) {
                    Helper.displayHintWindow((Stage) idLB.getScene().getWindow(), "Error","Cannot be updated","Reason:"
                            + throwables.getMessage());
                }
            }
            else {
                setEditable(true);
            }
        });

        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            HintDialog hintDialog = new HintDialog((Stage) idLB.getScene().getWindow());
            Button confirmBt = new Button("YES [DELETE]");
            confirmBt.setOnAction((e)->{
                try {
                    hintDialog.hide();
                    DatabaseDriver.deleteDoctor(selectDoctor.getId());
                    doctors.remove(selectDoctor);
                    coverPane.setVisible(true);
                    setEditable(false);
                } catch (SQLException throwables) {
                    Helper.displayHintWindow((Stage) idLB.getScene().getWindow(),"error", "Delete failed",
                            "Reason: " + throwables.getMessage());
                }
            });
            hintDialog.setOptionButton(new Button[]{confirmBt});
            hintDialog.buildAndShow("warning", "Delete the doctor?","The doctor information will be deleted. This operation cannot be undone!" +
                    "\nAre you sure to delete the report file with the appointment?");
        });

        ToggleGroup group = new ToggleGroup();
        nameRB.setToggleGroup(group);
        expertiseRB.setToggleGroup(group);nameRB.setSelected(true);
        group.selectedToggleProperty().addListener( new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    dataFilter();
                    countLB.setText(filteredList.size()+"");
                }
            }
        });
    }
    private void initTableUI(){
        doctorTableView.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        idTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        phoneTC.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        expertiseTC.setCellValueFactory(new PropertyValueFactory<>("expertise"));
        expertiseTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");

        doctorTableView.setRowFactory(tb->{
            TableRow<Doctor> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 1 && !row.isEmpty()){
                    Doctor doctor = row.getItem();
                    selectDoctor = doctor;
                    setEditable(false);
                    displayDetail(doctor);
                }
            });
            return row;
        });
    }

    private void setEditable(boolean isEditable){
        this.isEditable = isEditable;
        nameTF.setEditable(isEditable);
        bioTF.setEditable(isEditable);
        phoneTF.setEditable(isEditable);
        addressTF.setEditable(isEditable);
        FaxTF.setEditable(isEditable);
        websiteTF.setEditable(isEditable);
        expertiseTF.setEditable(isEditable);
        emailTF.setEditable(isEditable);
        if (isEditable){
            editBT.setText("SAVE");
            backBT.setText("CANCEL");
        }else {
            editBT.setText("EDIT");
            backBT.setText("CLOSE");
        }
    }
    private void displayDetail(Doctor doctor){
        coverPane.setVisible(false);
        idLB.setText(doctor.getId());
        nameTF.setText(doctor.getName());
        bioTF.setText(doctor.getBio());
        phoneTF.setText(doctor.getPhone());
        addressTF.setText(doctor.getAddress());
        FaxTF.setText(doctor.getFax());
        websiteTF.setText(doctor.getWebsite());
        expertiseTF.setText(doctor.getExpertise());
        emailTF.setText(doctor.getEmail());
    }

    private void dataFilter(){
        coverPane.setVisible(true);
        String searchLine = searchTF.getText().toLowerCase().trim();
        filteredList.setPredicate(a->{
            if (searchLine.isEmpty())return true;
            if (nameRB.isSelected() && (a.getId().contains(searchLine) || a.getName().toLowerCase().contains(searchLine))){
                return true;
            }else if (expertiseRB.isSelected() && a.getExpertise() != null && a.getExpertise().toLowerCase().contains(searchLine)){
                return true;
            }
            else {
                return false;
            }
        });
    }

    @Override
    public void before() {
        loadPane.setVisible(true);
        //loadProgressIndicator.progressProperty().bind(loadingTask.progressProperty());
    }

    @Override
    public void doing() throws Exception{
        doctors = FXCollections.observableArrayList(DatabaseDriver.getDoctors());
        filteredList = new FilteredList<>(doctors, d -> true);
        SortedList<Doctor> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(doctorTableView.comparatorProperty());
        doctorTableView.setItems(sortedList);
        dataFilter();
    }

    @Override
    public void done() {
        loadPane.setVisible(false);
        countLB.setText(doctors.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }

    @Override
    public void failed() {
        loadPane.setVisible(false);
        countLB.setText(doctors.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }

    @Override
    public void cancelled() {
        loadPane.setVisible(false);
        countLB.setText(doctors.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }
}
