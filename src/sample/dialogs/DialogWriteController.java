package sample.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class DialogWriteController {

    // gui
    @FXML
    private Label header;
    @FXML
    private TextField textOut;

    // vars
    private Stage _this;
    private boolean status;

    public String getHeader() {
        return header.getText();
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

    public String getTextOut() {
        return textOut.getText();
    }

    public void setTextOut(String st) {
        textOut.setText(st);
    }

    public boolean isStatus() {
        return status;
    }

    public void setPrimaryStage(Stage st){
        _this = st;
    }

    public void btnCancelClick(javafx.scene.input.MouseEvent mouseEvent) {
        status = false;
        _this.close();
    }

    public void btnOkClick(javafx.scene.input.MouseEvent mouseEvent) {
        status = true;
        _this.close();
    }
}
