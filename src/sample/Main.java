package sample;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import java.io.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane p = fxmlLoader.load(getClass().getResource("sample.fxml").openStream());
        primaryStage.setTitle("Graph Editor");
        primaryStage.setScene(new Scene(p, 700, 520));
        // get controller
        Controller controller = fxmlLoader.getController();
        controller.setParentStage(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
