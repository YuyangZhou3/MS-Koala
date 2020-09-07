package appointment;
/*Author: Bowei SONG*/
import base.Appointment;
import database.DatabaseDriver;
import helper.Helper;
import helper.LoadingTask;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppointmentPageController implements Initializable, LoadDataTask {

    @FXML private Label countLB;
    @FXML private ChoiceBox<String> yearCB,monthCB,dayCB;
    @FXML private TextField searchTF;
    @FXML private ImageView addIV;
    @FXML private TableView<Appointment> appointmentTB;
    @FXML private RadioButton appidRB, userRB;
    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    @FXML private TableColumn<Appointment, String> idTC,userIdTC,doctorIdTC, titleTC, dateTC, durationTC,statusTC;

    private ObservableList<Appointment> appointments;
    private FilteredList<Appointment> filteredList;
    private LoadingTask loadTask;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTask = new LoadingTask(this);
        initUI();
        loadData();
        addIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            subController = Helper.openSubWindow(null, (Stage) addIV.getParent().getScene().getWindow(),"","../appointment/AppointmentDetailXML.fxml");
            ((AppointmentDetailController)subController).open(new Appointment());
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
                loadData();
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
                loadData();
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
                    Appointment appointment = row.getItem();
                    subController = Helper.openSubWindow(null, (Stage) searchTF.getScene().getWindow(),"", "../appointment/AppointmentDetailXML.fxml");
                    ((AppointmentDetailController)subController).open(appointment);
                }
            });
            return row;
        });
    }

    private void loadData(){
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
        System.out.println("doing");
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
