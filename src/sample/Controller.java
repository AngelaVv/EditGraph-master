package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import sample.dialogs.DialogColorChooser;
import sample.dialogs.DialogWriteController;

import java.io.*;
import java.util.ArrayList;

public class Controller {

    // stages
    private Stage primaryStage;
    private Stage textStage;
    private Stage colorChooserStage;
    private DialogWriteController textStageController;
    private DialogColorChooser colorChooserController;

    // gui
    @FXML
    private ScrollPane rootPane;
    @FXML
    private Canvas canvasPane;
    @FXML
    private MenuItem loadMenu;
    @FXML
    private MenuItem saveMenu;

    // graph
    public int width;
    public int height;
    private Graph graph;  // The model (i.e. the graph)
    private Node dragNode;
    private Point2D elasticEndLocation;
    private Edge dragEdge;
    private Point2D dragPoint;
    private GraphicsContext aPen;
    private ContextMenu menuEdge;
    private ContextMenu menuNode;
    private ContextMenu menuGeneral;

    @FXML
    public void initialize(){
        initSystem();
    }

    private void initSystem(){
        //выз в конктрк форме и пр загрузка всех ресурсов
        initVariables();
        loadModalWindow();//загр диалог окна
        createMainMenu();
        createContextMenu();
        createEvents();//созд всех собитый
        createClearGraph();//чистый граф по ум
        //createDefaultGraph();
    }

    private void initVariables(){//все переменные
        primaryStage = null;
        textStage = null;
        colorChooserStage = null;
        textStageController = null;
        colorChooserController = null;
        width = 0;
        height = 0;
        graph = null;
        dragNode = null;
        elasticEndLocation = null;
        dragEdge = null;
        dragPoint = null;
        aPen = null;
        menuEdge = null;
        menuNode = null;
    }

    private void loadModalWindow(){
        String [] path = new String[]{"dialogs/dialog-write.fxml", "dialogs/dialog-color-chooser.fxml"};
        String[] title = new String[]{"Редактирование атрибута", "Выбор цвета"};
        for(int i = 0; i < 2;i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path[i]));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage st = new Stage();
            st.setResizable(false);
            st.setTitle(title[i]);
            st.setScene(new Scene(root1, Color.TRANSPARENT));
            st.initOwner(primaryStage);
            st.initModality(Modality.APPLICATION_MODAL);
            if(i == 0){
                textStage = st;
                textStageController = fxmlLoader.getController();
                textStageController.setPrimaryStage(textStage);
            }else{
                colorChooserStage = st;
                colorChooserController = fxmlLoader.getController();
                colorChooserController.setDefaultColor(Node.colorDefault);
                colorChooserController.setPrimaryStage(colorChooserStage);
            }
        }

    }

    private void createDefaultGraph(){
        canvasPane.requestFocus();
        graph = Graph.example();
        aPen = canvasPane.getGraphicsContext2D();
        update();
    }

    private void createClearGraph() {
        canvasPane.requestFocus();
        graph = new Graph();
        aPen = canvasPane.getGraphicsContext2D();
        update();
    }

    private void createMainMenu(){
        loadMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
        saveMenu.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        // Set up the event handlers for the File menu
        loadMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File("C:\\"));
                chooser.setTitle("Load Graph");
                File f = chooser.showOpenDialog(primaryStage);
                if (f != null) {
                    try {
                        BufferedReader file = new BufferedReader(
                                new FileReader(f.getAbsolutePath()));
                        graph = Graph.loadFrom(file);
                        file.close();
                        update();
                    }
                    catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error !");
                        alert.setHeaderText(null);
                        alert.setContentText("Error Loading Graph From File !");
                        alert.showAndWait();
                    }
                }
            }
        });

        saveMenu.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File("C:\\"));
                chooser.setTitle("Save Graph");
                File f = chooser.showSaveDialog(primaryStage);
                if (f != null) {
                    try {
                        PrintWriter file = new PrintWriter(
                                new FileWriter(f.getAbsolutePath()));
                        graph.saveTo(file);
                        file.close();
                    }
                    catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error !");
                        alert.setHeaderText(null);
                        alert.setContentText("Error Saving Graph To File !");
                        alert.showAndWait();
                    }
                }
            }
        });
    }

    private void createContextMenu(){
        menuGeneral = new ContextMenu();

        menuEdge = new ContextMenu();
        MenuItem renameEdgeLine = new MenuItem("Изменить ребро");
        renameEdgeLine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textStageController.setHeader("Введите новое значение ребра:");
                textStageController.setTextOut("");
                textStage.showAndWait();
                if(textStageController.isStatus()) {
                    graph.selectedEdges().get(0).setLabel(textStageController.getTextOut());
                    update();
                }
            }
        });
        menuEdge.getItems().addAll(renameEdgeLine);
        menuNode = new ContextMenu();
        MenuItem renameNodeIn = new MenuItem("Изменить вершину");
        renameNodeIn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textStageController.setHeader("Введите новое значение вершины:");
                textStageController.setTextOut("");
                textStage.showAndWait();
                if(textStageController.isStatus()) {
                    graph.selectedNodes().get(0).setLabelIn(textStageController.getTextOut());
                    update();
                }
            }
        });
        MenuItem renameNodeUp = new MenuItem("Изменить фактор");
        renameNodeUp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textStageController.setHeader("Введите новое значение фактора:");
                textStageController.setTextOut("");
                textStage.showAndWait();
                if(textStageController.isStatus()) {
                    graph.selectedNodes().get(0).setLabel(textStageController.getTextOut());
                    update();
                }
            }
        });
        MenuItem editColor = new MenuItem("Изменить цвет вершины");
        editColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                colorChooserStage.showAndWait();
                if(colorChooserController.isStatus()) {
                    graph.selectedNodes().get(0).setColorNode(colorChooserController.getDefaultColor());
                    update();
                }
            }
        });
        menuNode.getItems().addAll(renameNodeIn, renameNodeUp, editColor);

        ArrayList<MenuItem> deleter = new ArrayList<>();
        for(int i = 0;i<3;i++) {
            MenuItem delMenu = new MenuItem("Удалить");
            delMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    deleteElements();
                }
            });
            deleter.add(delMenu);
        }
        menuEdge.getItems().add(deleter.get(0));
        menuNode.getItems().add(deleter.get(1));
        menuGeneral.getItems().add(deleter.get(2));
    }

    private void createEvents(){
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            //изм размер окна
            if(width <= rootPane.getWidth()){
                canvasPane.setWidth(rootPane.getWidth());
                width = (int) canvasPane.getWidth();
                update();
                System.out.println("Resize width canvas: " + canvasPane.getWidth());
            }
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {//высота
            if(height <= rootPane.getHeight()) {
                canvasPane.setHeight(rootPane.getHeight());
                height = (int) canvasPane.getHeight();
                update();
                System.out.println("Resize height canvas: " + canvasPane.getHeight());
            }
        });

        canvasPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            //нажатие мышки
            public void handle(MouseEvent ev) {
                dragPoint = new Point2D(ev.getX(), ev.getY());
                Node currentSelectionNode = graph.nodeAt(ev.getX(), ev.getY());
                if(ev.isSecondaryButtonDown()){ // нажали ПКМ
                    if(graph.selectedEdges().size() == 1 && graph.selectedNodes().size() == 1) {
                        menuGeneral.show(canvasPane, ev.getScreenX(), ev.getScreenY());
                    }
                    else if (graph.selectedEdges().size() == 1) { // проверяем, что выделено одно ребро
                        menuEdge.show(canvasPane, ev.getScreenX(), ev.getScreenY());
                    } else if (graph.selectedNodes().size() == 1) { // проверяем, что выделен один узел
                        menuNode.show(canvasPane, ev.getScreenX(), ev.getScreenY());
                    } else {
                        menuGeneral.show(canvasPane, ev.getScreenX(), ev.getScreenY());
                    }
                    return;
                }
                else if (ev.getClickCount() == 2) {
                    if (currentSelectionNode == null) {
                        Edge anEdge = graph.edgeAt(ev.getX(), ev.getY());
                        if (anEdge == null)
                            graph.addNode(new Node(ev.getX(), ev.getY()));
                        else
                            anEdge.toggleSelected();
                    }
                    else
                        currentSelectionNode.toggleSelected();
                    update();
                }
                else {
                    if (currentSelectionNode != null) {
                        dragNode = currentSelectionNode;
                        dragEdge = null;
                    }
                    else
                        dragEdge = graph.edgeAt(ev.getX(), ev.getY());
                    dragPoint = new Point2D(ev.getX(), ev.getY());
                }
                menuEdge.hide();
                menuNode.hide();
                menuGeneral.hide();
            }

        });

        canvasPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            //отпускание мышки
            public void handle(MouseEvent ev) {
                Node aNode = graph.nodeAt(ev.getX(), ev.getY());
                // Check to see if we have let go on a node
                if ((dragNode != null) && (aNode != null) && (aNode != dragNode)) {
                    // Change the model, by adding a new Edge
                    graph.addEdge(dragNode, aNode);
                }
                // Update the view, by redrawing the Graph
                dragNode = null; // No need to remember this anymore
                update();
            }
        });

        canvasPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            //премещение с наж кнопкой
            public void handle(MouseEvent ev) {
                elasticEndLocation = new Point2D(ev.getX(), ev.getY());
                if (dragNode != null) {
                    if (dragNode.isSelected()) {
                        for (Node n: graph.selectedNodes()) {
                            n.setLocation(n.getLocation().getX()+ev.getX()-dragPoint.getX(),
                                    n.getLocation().getY()+ev.getY()-dragPoint.getY());
                        }
                        dragPoint = new Point2D(ev.getX(), ev.getY());
                    }
                }

                if (dragEdge != null) {
                    if (dragEdge.isSelected()) {
                        dragEdge.getStartNode().setLocation(
                                dragEdge.getStartNode().getLocation().getX() +
                                        ev.getX() - dragPoint.getX(),
                                dragEdge.getStartNode().getLocation().getY() +
                                        ev.getY() - dragPoint.getY());
                        dragEdge.getEndNode().setLocation(
                                dragEdge.getEndNode().getLocation().getX() +
                                        ev.getX() - dragPoint.getX(),
                                dragEdge.getEndNode().getLocation().getY() +
                                        ev.getY() - dragPoint.getY());
                        dragEdge.setCenterArrow(new Point2D(dragEdge.getCenterArrow().getX() +
                                ev.getX() - dragPoint.getX(),
                                dragEdge.getCenterArrow().getY() +
                                        ev.getY() - dragPoint.getY()));
                        dragEdge.setLeftArrow(new Point2D(dragEdge.getLeftArrow().getX() +
                                ev.getX() - dragPoint.getX(),
                                dragEdge.getLeftArrow().getY() +
                                        ev.getY() - dragPoint.getY()));
                        dragEdge.setRightArrow(new Point2D(dragEdge.getRightArrow().getX() +
                                ev.getX() - dragPoint.getX(),
                                dragEdge.getRightArrow().getY() +
                                        ev.getY() - dragPoint.getY()));
                        dragPoint = new Point2D(ev.getX(), ev.getY());
                    }
                }
                update();
            }
        });

        rootPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            //отпускание клавиши делит
            public void handle(KeyEvent ev) {
                if (ev.getCode() == KeyCode.DELETE) {
                    deleteElements();
                }
            }
        });
    }

    private void deleteElements() {//все удаление перебирает элементы
        // Delete all selected Edges
        for (Edge e : graph.selectedEdges())
            graph.deleteEdge(e);
        // Delete all selected Nodes
        for (Node n : graph.selectedNodes())
            graph.deleteNode(n);
        // Update the view, by redrawing the Graph
        update();
    }

    private void update() {
        //обновление формы после удаления
        aPen.setFill(Color.WHITE);
        aPen.fillRect(0, 0, width, height);
        graph.draw(aPen);
        // Draw the elastic band
        if (dragNode != null) {
            if (!dragNode.isSelected()) {
                /*aPen.strokeLine(dragNode.getLocation().getX(),
                        dragNode.getLocation().getY(),
                        elasticEndLocation.getX(),
                        elasticEndLocation.getY());*/
                Edge tmpEdge = new Edge(new Node(dragNode.getLocation().getX(),
                        dragNode.getLocation().getY()), new Node(elasticEndLocation.getX(),
                        elasticEndLocation.getY()), true);
                tmpEdge.draw(aPen);
            }
        }
    }

    public void setParentStage(Stage st){primaryStage = st;}
    //сеттер закидывания формы
}

