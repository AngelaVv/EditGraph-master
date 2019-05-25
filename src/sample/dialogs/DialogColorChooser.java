package sample.dialogs;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DialogColorChooser {

    // gui
    @FXML
    private Slider rSlider;
    @FXML
    private Slider gSlider;
    @FXML
    private Slider bSlider;
    @FXML
    private Canvas colorPreview;

    // vars
    private Color defaultColor;
    private boolean status;
    private GraphicsContext graphicsContextCanvas;
    private Stage _this;

    @FXML
    public void initialize(){
        graphicsContextCanvas = colorPreview.getGraphicsContext2D();
    }

    public void setPrimaryStage(Stage _this) {
        this._this = _this;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        editColorPreview(defaultColor);
        rSlider.setValue(getColor255(defaultColor.getRed()));
        gSlider.setValue(getColor255(defaultColor.getGreen()));
        bSlider.setValue(getColor255(defaultColor.getBlue()));
    }

    public boolean isStatus() {
        return status;
    }

    public void btnOkClick(MouseEvent mouseEvent) {
        status = true;
        defaultColor = Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue());
        _this.close();
    }


    public void btnCancelClick(MouseEvent mouseEvent) {
        status = false;
        _this.close();
    }

    public void dragSlider(MouseEvent mouseEvent) {
        editColorPreview(Color.rgb((int)rSlider.getValue(), (int)gSlider.getValue(), (int)bSlider.getValue()));
    }

    private void editColorPreview(Color cl){
        graphicsContextCanvas.setFill(cl);
        graphicsContextCanvas.fillRect(0,0,colorPreview.getWidth(), colorPreview.getHeight());
    }

    private double getColor255(double cannel){
        double pr = 1 / 100.d;
        double proc = cannel / pr;
        return Math.round(255 / 100 * proc);
    }
}
