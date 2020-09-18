package util;

import helper.Helper;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HintDialog {
    private Stage stage;
    private Stage parentStage;
    private HintDialogController controller;
    public HintDialog(Stage parent){
        this.parentStage = parent;
        buildStage();
    }

    public void build(String type, String title, String message){
        controller.build(type, title,message);
    }

    public void setOptionButton(Button[] buttons){
        controller.setOptionButton(buttons);
    }

    private void buildStage(){
        stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        if (parentStage != null) stage.initOwner(parentStage);
        try {
            controller = (HintDialogController) Helper.replaceSceneContent("view/HintDialogFXML.fxml", stage);
            stage.getScene().setFill(Color.TRANSPARENT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buildAndShow(String type, String title, String message){
        build(type,title,message);
        show();
    }

    public void show(){
        if (stage != null)
            stage.show();
    }

    public void hide(){
        stage.hide();
    }
}
