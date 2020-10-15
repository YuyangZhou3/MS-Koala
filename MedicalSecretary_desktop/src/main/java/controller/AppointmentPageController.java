package controller;
/*Author: Bowei SONG*/
import base.Appointment;
import database.DatabaseDriver;
import util.Helper;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppointmentPageController implements Initializable, LoadDataTask {

    @FXML private Label countLB;
    @FXML private ChoiceBox<String> yearCB,monthCB,dayCB;
    @FXML private TextField searchTF;
    @FXML private TableView<Appointment> appointmentTB;
    @FXML private RadioButton appidRB, userRB;
    @FXML private AnchorPane loadPane,detailPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    @FXML private TableColumn<Appointment, String> idTC,userIdTC,doctorIdTC, titleTC, dateTC, durationTC,statusTC;

    @FXML private Label patientIdLB, doctorIdLB,idLB;
    @FXML private TextField patientTF, doctorTF,titleTF,detailTF,durationTF,reportTF;
    @FXML private TextField createDateTF, changeDateTF,appDateTF,timeTF,statusTF;
    @FXML private TextArea noteTA, userNoteTA;
    @FXML private Button closeBT;
    @FXML private ImageView deleteIV, reportIV,closeIV;

    private ObservableList<Appointment> appointments;
    private FilteredList<Appointment> filteredList;
    private Appointment appointment;
    private LoadingTask loadTask;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTask = new LoadingTask(this);
        initUI();
        closeBT.setOnAction((e)->{
            detailPane.setVisible(false);
        });
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            detailPane.setVisible(false);
        });
        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            HintDialog hintDialog = new HintDialog((Stage) countLB.getScene().getWindow());
            Button confirmBt = new Button("YES [DELETE]");
            confirmBt.setOnAction((e)->{
                try {
                    hintDialog.hide();
                    DatabaseDriver.deleteData("Appointment" , appointment.getId());
                    appointments.remove(appointment);
                    detailPane.setVisible(false);
                    appointment = null;
                } catch (SQLException throwables) {
                    Helper.displayHintWindow((Stage) countLB.getScene().getWindow(),"error", "Delete failed",
                            "Reason: " + throwables.getMessage());
                }
            });
            hintDialog.setOptionButton(new Button[]{confirmBt});
            hintDialog.buildAndShow("warning", "Delete the Appointment information?","The Appointment information will be deleted. This operation cannot be undone!" +
                    "\nAre you sure to delete the Appointment?");
        });

        reportIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            try {
                Object subController = Helper.openSubWindow(null, (Stage) countLB.getScene().getWindow(), "", "view/PDFReaderFXML.fxml");
                ((PDFReaderController)subController).open(appointment);
            }catch (Exception e){
                e.printStackTrace();
            }

        });
    }

    private void initUI(){
        ArrayList<String> yearList = new ArrayList<>();
        int year = LocalDate.now().getYear();
        yearList.add(year+1+"");
        for (int i = 0; i < 5; i++){
            yearList.add(year-i+"");
        }
        yearCB.setItems(FXCollections.observableArrayList(yearList));
        yearCB.setValue(year+"");
        monthCB.setItems(FXCollections.observableArrayList("ALL", "1","2","3","4","5","6","7","8","9","10","11","12"));
        monthCB.setValue(LocalDate.now().getMonthValue()+"");
        dayCB.setItems(FXCollections.observableArrayList(Helper.getDayOfMonth(year, LocalDate.now().getMonthValue())));
        dayCB.setValue(LocalDate.now().getDayOfMonth()+"");

        ToggleGroup group = new ToggleGroup();
        appidRB.setToggleGroup(group);
        userRB.setToggleGroup(group);appidRB.setSelected(true);
        group.selectedToggleProperty().addListener( new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    dataFilter();
                    countLB.setText(filteredList.size()+"");
                }
            }
        });

        yearCB.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                loadDataInner();
            }
        }));
        monthCB.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                if (monthCB.getValue().equalsIgnoreCase("all")){
                    ArrayList<String> days = new ArrayList<>();
                    days.add("ALL");
                    for (int i = 1; i < 32; i++){
                        days.add(String.valueOf(i));
                    }
                    dayCB.setItems(FXCollections.observableArrayList(days));
                    dayCB.setValue("ALL");
                }else {
                    int selectedYear = Integer.parseInt(yearCB.getValue());
                    int month = Integer.parseInt(monthCB.getValue());
                    dayCB.setItems(FXCollections.observableArrayList(Helper.getDayOfMonth(selectedYear, month)));
                    dayCB.setValue("ALL");
                }
            }
        }));
        dayCB.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                loadDataInner();
            }
        }));
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            dataFilter();
            countLB.setText(filteredList.size()+"");
        }));

        initTable();
    }

    private Object subController;
    private void initTable(){
        appointmentTB.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        idTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        idTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        userIdTC.setCellValueFactory(new PropertyValueFactory<>("userNameID"));
        userIdTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        doctorIdTC.setCellValueFactory(new PropertyValueFactory<>("doctorNameID"));
        doctorIdTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        titleTC.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        dateTC.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        durationTC.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        statusTC.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");

        appointmentTB.setRowFactory(tb->{
            TableRow<Appointment> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()){
                    appointment = row.getItem();
                    displayDetail();
                }
            });
            return row;
        });
    }

    private void displayDetail(){
        detailPane.setVisible(true);
        idLB.setText(appointment.getId());
        patientIdLB.setText(appointment.getUserID());
        patientTF.setText(appointment.getUserName());
        doctorIdLB.setText(appointment.getDoctorID());
        doctorTF.setText(appointment.getDoctorName());
        titleTF.setText(appointment.getTitle());
        appDateTF.setText(appointment.getDate());
        timeTF.setText(appointment.getTime());
        durationTF.setText(appointment.getDuration());
        statusTF.setText(appointment.getStatus());
        reportTF.textProperty().bind(appointment.reportProperty());
        detailTF.setText(appointment.getDetail());

        createDateTF.setText(appointment.getCreateDate());
        changeDateTF.setText(appointment.getChangeDate());
        noteTA.setText(appointment.getNote());
        userNoteTA.setText(appointment.getUserNote());
    }

    public void loadData(){

        if (!loadTask.isRunning()) {
            loadTask.start();
        }

    }
    public void loadDataInner(){
        if (!loadTask.isRunning()) {
            loadTask.start();
        }
    }

    private void dataFilter(){
        String searchLine = searchTF.getText().toLowerCase().trim();
        filteredList.setPredicate(a->{
            if (searchLine.isEmpty())return true;
            if (appidRB.isSelected() && a.getId().contains(searchLine)){
                return true;
            }else if (userRB.isSelected() && a.getUserNameID().toLowerCase().contains(searchLine)){
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
        loadProgressIndicator.progressProperty().bind(loadTask.progressProperty());
    }

    @Override
    public void doing() {
        appointments = FXCollections.observableArrayList(DatabaseDriver.getAppointmentByDate(yearCB.getValue(), monthCB.getValue(), dayCB.getValue()));
        filteredList = new FilteredList<>(appointments, p -> true);
        SortedList<Appointment> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(appointmentTB.comparatorProperty());
        appointmentTB.setItems(sortedList);
        dataFilter();
    }

    @Override
    public void done() {
        loadPane.setVisible(false);
        countLB.setText(appointments.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }

    @Override
    public void failed() {
        loadPane.setVisible(false);
        countLB.setText(appointments.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }

    @Override
    public void cancelled() {
        loadPane.setVisible(false);
        countLB.setText(appointments.size()+"");
        loadProgressIndicator.progressProperty().unbind();
    }
}
