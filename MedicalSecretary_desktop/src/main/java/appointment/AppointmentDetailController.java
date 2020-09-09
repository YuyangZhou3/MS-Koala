package appointment;

import base.Appointment;
import base.Patient;
import base.Person;
import database.DatabaseDriver;
import helper.Helper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import app.MedicalSecretary;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AppointmentDetailController implements Initializable {
    @FXML private Label patientIdLB, doctorIdLB,idLB;
    @FXML private TextField patientTF, doctorTF,titleTF,detailTF,durationTF,reportTF;
    @FXML private TextField createDateTF, changeDateTF,appDateTF,timeTF,statusTF;
    @FXML private ChoiceBox<String> statusCB;
    @FXML private TextArea noteTA, userNoteTA;
    @FXML private Button editBT,closeBT;
    @FXML private AnchorPane deletePane,topPane,loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    @FXML private ImageView deleteIV, reportIV;
    @FXML private DatePicker datePicker;

    // Add Appointment UI
    @FXML private AnchorPane selectPatientAP, selectDoctorAP, searchPane;
    @FXML private Label patientHintLB, doctorHintLB,searchNameLB;
    @FXML private TextField searchTF;
    @FXML private ImageView closeSearchIV;
    @FXML TableView<Person> searchTV;
    @FXML TableColumn<Person, String> idTC, nameTC, emailTC;
    private ObservableList<Person> people;

    private Appointment appointment;
    private Task<Integer> loadTask = null;
    private boolean isEditable;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isEditable = false;
        statusCB.setItems(FXCollections.observableArrayList(MedicalSecretary.appointmentStatus));
        initEvent();
    }

    public void open(Appointment appointment){
        this.appointment = appointment;
        if (appointment.getId() != null){
            isEditable = false;
            patientIdLB.setText(appointment.getUserID());
            patientTF.setText(appointment.getUserName());
            doctorIdLB.setText(appointment.getDoctorID());
            doctorTF.setText(appointment.getDoctorName());
            titleTF.setText(appointment.getTitle());
            appDateTF.setText(appointment.getDate());
            timeTF.setText(appointment.getTime());
            durationTF.setText(appointment.getDuration());
            statusCB.setValue(appointment.getStatus());
            statusTF.setText(appointment.getStatus());
            reportTF.textProperty().bind(appointment.reportProperty());
            saveORLoadData();
        }else {
            setEditable(true);
            deletePane.setVisible(false);
            editBT.setText("ADD");
            initTable();

            if (appointment.getDoctorID() == null) {
                selectDoctorAP.setCursor(Cursor.HAND);
                doctorHintLB.setText("SELECT");
                selectDoctorAP.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    openSearchPane("Doctor");
                });
            }
            if (appointment.getUserName() == null) {
                selectPatientAP.setCursor(Cursor.HAND);
                patientHintLB.setText("SELECT");
                selectPatientAP.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    openSearchPane("Patient");
                });
            }
        }

        Stage stage = (Stage) idLB.getScene().getWindow();
        Helper.initTopBar(stage, topPane);
    }

    private void initEvent(){
        closeBT.setOnAction((e)->{
            if (appointment.getId() != null) {
                if (isEditable) {
                    closeBT.setText("CLOSE");
                    editBT.setText("EDIT");
                    setEditable(false);
                    reloadDataToUI();
                } else idLB.getScene().getWindow().hide();
            }
            else idLB.getScene().getWindow().hide();
        });

        editBT.setOnAction((e)->{
            if (appointment.getId() != null) {
                if (isEditable) {
                    if (checkTimeFormat(timeTF.getText().trim())) {
                        saveORLoadData();
                        editBT.setText("EDIT");
                    } else {
                        Helper.displayHintWindow((Stage) idLB.getScene().getWindow(), "error", "Time Format is invalid!",
                                "The Time Format " + timeTF.getText().trim() + " is invalid.\n\n"
                                        + "\nValid Format: HH:mm:ss\nFor example: 04:05:06");
                    }
                } else {
                    editBT.setText("SAVE");
                    setEditable(true);
                    closeBT.setText("CANCEL");
                }
            }else {
                try {
                    appointment.createNew(titleTF.getText().trim(), detailTF.getText().trim(), datePicker.getValue(),
                            timeTF.getText().trim(),durationTF.getText().trim(),statusCB.getValue(),noteTA.getText(),userNoteTA.getText());
                    DatabaseDriver.insertAppointment(appointment);
                    idLB.getScene().getWindow().hide();
                }catch (Exception ex){
                    Helper.displayHintWindow((Stage) idLB.getScene().getWindow(), "error", "Create Failed",
                            "Reason: " + ex.getMessage());
                }

            }
        });

        reportIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            try {
                Object subController = Helper.openSubWindow(null, (Stage) idLB.getScene().getWindow(), "", "view/PDFReaderFXML.fxml");
                ((PDFReaderController)subController).open(appointment);
            }catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    private void setEditable(boolean isEditable){
        this.isEditable = isEditable;
        titleTF.setEditable(isEditable);
        detailTF.setEditable(isEditable);
        appDateTF.setVisible(!isEditable);
        timeTF.setEditable(isEditable);
        durationTF.setEditable(isEditable);
        statusCB.setDisable(!isEditable);
        noteTA.setEditable(isEditable);
        userNoteTA.setEditable(isEditable);
        if (appointment.getId() == null){
            datePicker.setValue(LocalDate.now());
        }
        else datePicker.setValue(LocalDate.parse(appointment.getDate(), Helper.dtf));
        deletePane.setVisible(isEditable);
        statusTF.setVisible(!isEditable);
    }

    private void reloadDataToUI(){
        patientIdLB.setText(appointment.getUserID());
        patientTF.setText(appointment.getUserName());
        doctorIdLB.setText(appointment.getDoctorID());
        doctorTF.setText(appointment.getDoctorName());
        titleTF.setText(appointment.getTitle());
        appDateTF.setText(appointment.getDate());
        timeTF.setText(appointment.getTime());
        durationTF.setText(appointment.getDuration());
        statusCB.setValue(appointment.getStatus());
        detailTF.setText(appointment.getDetail());
        //reportTF.setText(appointment.getReport());
        createDateTF.setText(appointment.getCreateDate());
        changeDateTF.setText(appointment.getChangeDate());
        noteTA.setText(appointment.getNote());
        userNoteTA.setText(appointment.getUserNote());
        statusTF.setText(appointment.getStatus());
    }


    private void saveORLoadData(){
        if(loadTask ==null || !loadTask.isRunning()){
            loadPane.setVisible(true);
            loadTask = new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    if (!isEditable) {
                        DatabaseDriver.getAppointment(appointment);
                        DatabaseDriver.getAppointmentReport(appointment);
                    }
                    else {
                        DatabaseDriver.updateAppointment(appointment, titleTF.getText().trim(), detailTF.getText().trim(),
                                datePicker.getValue().format(Helper.dtf)+" "+ timeTF.getText(), durationTF.getText().trim(),
                                statusCB.getValue(), noteTA.getText(), userNoteTA.getText());
                    }
                    return 0;
                }
                @Override
                protected void succeeded() {
                    if (!isEditable){
                        detailTF.setText(appointment.getDetail());
                        createDateTF.setText(appointment.getCreateDate());
                        changeDateTF.setText(appointment.getChangeDate());
                        noteTA.setText(appointment.getNote());
                        userNoteTA.setText(appointment.getUserNote());
                    }
                    else {
                        reloadDataToUI();
                        setEditable(false);
                    }
                    loadPane.setVisible(false);
                    loadProgressIndicator.progressProperty().unbind();
                }
                @Override
                protected void cancelled() {
                    loadPane.setVisible(false);
                    loadProgressIndicator.progressProperty().unbind();
                }
                @Override
                protected void failed() {
                    if (!isEditable) {
                        Helper.displayHintWindow((Stage) idLB.getScene().getWindow(), "error", "Load Failed!",
                                "Loading the Appointment with ID '" + appointment.getId() + "' occurs a ERROR, the appointment cannot be loaded.\n"
                                        + "\nReason: " + getException().getMessage());
                    }else  {
                        Helper.displayHintWindow((Stage) idLB.getScene().getWindow(), "error", "Update Failed!",
                                "Updating the Appointment with ID '" + appointment.getId() + "' occurs a ERROR, the appointment cannot be updated.\n"
                                        + "\nReason: " + getException().getMessage());
                    }
                    loadPane.setVisible(false);
                    loadProgressIndicator.progressProperty().unbind();

                }
            };
            loadProgressIndicator.progressProperty().bind(loadTask.progressProperty());
            new Thread(loadTask).start();
        }
    }

    /* Add Appointment Function*/

    private void initTable(){
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        idTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        nameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        emailTC.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        searchTV.setRowFactory(tb->{
            TableRow<Person> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()){
                    Person person = row.getItem();
                    if (person instanceof Patient) {
                        patientIdLB.setText(person.getId());
                        patientTF.setText(person.getName());
                        appointment.setUserID(person.getId());
                        appointment.setUserName(person.getName());
                    }else {
                        doctorIdLB.setText(person.getId());
                        doctorTF.setText(person.getName());
                        appointment.setDoctorID(person.getId());
                        appointment.setDoctorName(person.getName());
                    }
                    searchPane.setVisible(false);
                }
            });
            return row;
        });
    }

    private FilteredList<Person> filteredList;
    private void openSearchPane(String name){
        searchPane.setVisible(true);
        searchNameLB.setText(name);
        try {
            people = FXCollections.observableArrayList(DatabaseDriver.getPersonForSelect(name));

        }catch (SQLException e){
            people = FXCollections.observableArrayList();
            Helper.displayHintWindow((Stage) idLB.getScene().getWindow(),"Error", "Load Failed",
                    "It occurs a ERROR when it loaded the data '"+name+"' from the Server\n\nReason: " + e.getMessage());
        }
        filteredList = new FilteredList<>(people, p -> true);
        SortedList<Person> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(searchTV.comparatorProperty());
        searchTV.setItems(sortedList);
    }

    private void dataFilter(){
        String searchLine = searchTF.getText().toLowerCase().trim();
        filteredList.setPredicate(p->{
            if (searchLine.isEmpty() )return true;
            if ( p.getName().toLowerCase().contains(searchLine)){
                return true;
            }
            else {
                return false;
            }
        });
    }

    private boolean checkTimeFormat(String time){
        if (time.length() != 8){
            return false;
        }
        String[] times = time.split(":");
        if (times.length != 3){
            return false;
        }
        String hh = times[0];
        String mm = times[1];
        String ss = times[2];
        if (hh.length() != 2 || mm.length() != 2 || ss.length() != 2 ){
            return false;
        }
        try {
            int h = Integer.parseInt(hh);
            int m = Integer.parseInt(mm);
            int s = Integer.parseInt(ss);
            System.out.println(h);
            if ( (h < 0 || h >= 24) || (m < 0 || m > 60) || (s < 0 || s >60)) return false;
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
