package controller;

import base.Pathology;
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

public class PathologyPageController implements Initializable, LoadDataTask {
    @FXML private TextField nameTF, addressTF, phoneTF, websiteTF;
    @FXML private TextArea hoursTA;
    @FXML private Button backBT;
    @FXML private ImageView deleteIV, closeIV;
    @FXML private Label idLB;
    @FXML private AnchorPane detailPane;

    @FXML private TableView<Pathology> tableView;
    @FXML private TableColumn<Pathology,String> idTC, nameTC, addressTC, hoursTC, phoneTC;
    @FXML private TextField searchTF;
    @FXML private Label countLB;
    @FXML private AnchorPane loadPane;
    @FXML private ProgressIndicator loadProgressIndicator;
    private ObservableList<Pathology> pathologies;
    private Task<Integer> loadTask;
    private Pathology pathology = null;
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
        phoneTC.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        addressTC.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");
        hoursTC.setCellValueFactory(new PropertyValueFactory<>("hours"));
        hoursTC.setStyle("-fx-alignment: center;-fx-font-family: 'Microsoft YaHei UI'");

        tableView.setRowFactory(tb->{
            TableRow<Pathology> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent->{
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()){
                    pathology = row.getItem();
                    displayDetail();
                }
            });
            return row;
        });
    }
    private void displayDetail(){
        detailPane.setVisible(true);
        idLB.setText("ID: " + pathology.getId());
        nameTF.setText(pathology.getName());
        addressTF.setText(pathology.getAddress());
        phoneTF.setText(pathology.getPhone());
        websiteTF.setText(pathology.getWebsite());
        hoursTA.setText(pathology.getHours());
    }

    private void initEvent(){
        searchTF.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            dataFilter();
            countLB.setText(filteredList.size()+"");
        }));
        backBT.setOnAction((e)->{
            detailPane.setVisible(false);
        });
        closeIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            detailPane.setVisible(false);
        });
        deleteIV.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event)->{
            HintDialog hintDialog = new HintDialog((Stage) idLB.getScene().getWindow());
            Button confirmBt = new Button("YES [DELETE]");
            confirmBt.setOnAction((e)->{
                try {
                    hintDialog.hide();
                    DatabaseDriver.deleteData("Pathology" , pathology.getId());
                    pathologies.remove(pathology);
                    detailPane.setVisible(false);
                    pathology = null;
                } catch (SQLException throwables) {
                    Helper.displayHintWindow((Stage) idLB.getScene().getWindow(),"error", "Delete failed",
                            "Reason: " + throwables.getMessage());
                }
            });
            hintDialog.setOptionButton(new Button[]{confirmBt});
            hintDialog.buildAndShow("warning", "Delete the Pathology information?","The Pathology information will be deleted. This operation cannot be undone!" +
                    "\nAre you sure to delete the Pathology?");
        });
    }

    private FilteredList<Pathology> filteredList;
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
        pathologies = FXCollections.observableArrayList(DatabaseDriver.getPathologies());
        filteredList = new FilteredList<>(pathologies, d -> true);
        SortedList<Pathology> sortedList = new SortedList<>(filteredList);
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
