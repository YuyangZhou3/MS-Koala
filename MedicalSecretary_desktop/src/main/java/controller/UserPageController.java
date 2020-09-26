package controller;

import base.Patient;
import base.Resource;
import database.DatabaseDriver;
import util.Helper;
import interfaces.LoadDataTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.HintDialog;
import util.LoadingTask;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserPageController implements Initializable, LoadDataTask {

    @FXML private TextField firstNameTF,midNameTF,surnameTF;
    @FXML private TextField idTF, dobTF, emailTF, streetTF, suburbTF, stateTF;
    @FXML private ImageView deleteIV;
    @FXML private AnchorPane coverPane,resourcePane;
    @FXML private Button backBT;

    @FXML private ImageView resourceIV,closeResourceIV,deleteResourceIV;
    @FXML private TableView<Resource> resourceTableView;
    @FXML private TableColumn<Resource, String> resourceIDTC, resourceNameTC, resourceTC;
    @FXML private TextField resourceNameTF, resourceTF;
    @FXML private RadioButton websiteRB, pdfRB;
    @FXML private Button addResourceBT,selectFileBT;
    @FXML private Label resourceLB;
    private File pdfFile = null;
    private ObservableList<Resource> resources;

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
        initResource();
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
        resources= FXCollections.observableArrayList(patient.getResources());
        resourceTableView.setItems(resources);
    }

    private void initResource(){
        resourceTableView.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        resourceIDTC.setCellValueFactory(new PropertyValueFactory<>("id"));
        resourceIDTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        resourceNameTC.setCellValueFactory(new PropertyValueFactory<>("name"));
        resourceNameTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        resourceTC.setCellValueFactory(new PropertyValueFactory<>("resource"));
        resourceTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        ToggleGroup group = new ToggleGroup();
        websiteRB.setToggleGroup(group);
        pdfRB.setToggleGroup(group);pdfRB.setSelected(true);
        group.selectedToggleProperty().addListener( new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    if (websiteRB.isSelected()){
                        selectFileBT.setVisible(false);
                        resourceLB.setText("Resource Website");
                        resourceTF.setEditable(true);
                    }else {
                        selectFileBT.setVisible(true);
                        resourceLB.setText("Resource Filename");
                        resourceTF.setEditable(false);
                    }
                }
            }
        });
        deleteResourceIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            Resource resource = resourceTableView.getSelectionModel().getSelectedItem();
            if (resource !=null) {
                HintDialog hintDialog = new HintDialog((Stage) countLB.getScene().getWindow());
                Button confirmBt = new Button("YES [DELETE]");
                confirmBt.setOnAction((e) -> {
                    try {
                        hintDialog.hide();
                        DatabaseDriver.deleteData("Resource", resource.getId());
                        resources.remove(resource);
                    } catch (SQLException throwables) {
                        Helper.displayHintWindow((Stage) countLB.getScene().getWindow(), "error", "Delete failed",
                                "Reason: " + throwables.getMessage());
                    }
                });
                hintDialog.setOptionButton(new Button[]{confirmBt});
                hintDialog.buildAndShow("warning", "Delete the Resource information?", "The Resource information will be deleted. This operation cannot be undone!" +
                        "\nAre you sure to delete the Resource?");
            }
            else {
                Helper.displayHintWindow((Stage) countLB.getScene().getWindow(),"error","Delete Failed",
                        "Reason: MUST select a resource");
            }
        });
        addResourceBT.setOnAction((e)->{
            String name = resourceNameTF.getText().trim();
            try {
                if (name == null || name.isEmpty()){
                    throw new Exception("Resource Name must not be Empty");
                }
                if (websiteRB.isSelected()){
                    String website = resourceTF.getText().trim();
                    resourceTF.setText("");
                }
                else {
                    resourceTF.setText("");
                    pdfFile = null;
                }
                resourceNameTF.setText("");
                Helper.displayHintWindow((Stage) countLB.getScene().getWindow(),"error","ADD Failed",
                        "Reason: The Function is a test version, cannot add new resource for the user now");
            }catch (Exception exception){
                Helper.displayHintWindow((Stage) countLB.getScene().getWindow(),"error","ADD Failed",
                        "Reason: " + exception.getMessage());
            }

        });
        selectFileBT.setOnAction((e)->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Resource File", "*.pdf"));
            File file = fileChooser.showOpenDialog(countLB.getScene().getWindow());
            if (file != null){
                pdfFile = file;
                resourceTF.setText(pdfFile.getAbsolutePath());
            }
        });
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
        resourceIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            resourcePane.setVisible(!resourcePane.isVisible());
        });
        closeResourceIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            resourcePane.setVisible(false);
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
