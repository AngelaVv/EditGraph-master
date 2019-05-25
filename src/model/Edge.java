package model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Edge {

    // vars
    private String label;
    private Node startNode, endNode;
    private Point2D leftArrow, rightArrow;
    private Point2D centerArrow;
    private boolean tempEdge;
    private boolean selected;

    // static
    public static final int WIDTH = 7;

    // get/set/toggle
    public boolean isSelected() { return selected;}

    public void setSelected(boolean state) { selected = state;}

    public void toggleSelected() { selected = !selected;}

    public String getLabel() { return label; }

    public Node getStartNode() { return startNode; }

    public Node getEndNode() { return endNode; }

    public Point2D getLeftArrow() {return leftArrow;}

    public Point2D getRightArrow() {return rightArrow;}

    public Point2D getCenterArrow() {return centerArrow;}

    public void setLabel(String newLabel) { label = newLabel; }

    public void setStartNode(Node aNode) { startNode = aNode; }

    public void setEndNode(Node aNode) { endNode = aNode; }

    public void setLeftArrow(Point2D leftArrow) {
        this.leftArrow = leftArrow;
    }

    public void setRightArrow(Point2D rightArrow) {
        this.rightArrow = rightArrow;
    }

    public void setCenterArrow(Point2D centerArrow) {
        this.centerArrow = centerArrow;
    }

    // constr
    public Edge(Node start, Node end) { this("", start, end); }

    public Edge(String aLabel, Node start, Node end) {
        label = aLabel;
        startNode = start;
        endNode = end;
        tempEdge = false;
        createArrow();
    }

    public Edge(Node start, Node end, boolean tmp) {
        startNode = start;
        endNode = end;
        tempEdge = tmp;
        createArrow();
    }

    // methods

    public void createArrow() {
        // получить точку
        double x = startNode.getLocation().getX() - endNode.getLocation().getX();
        double y = startNode.getLocation().getY() - endNode.getLocation().getY();
        double length = Math.sqrt(x * x + y * y);
        double procents = 0;
        procents = length / 100.d;
        procents = 15 / procents;
        procents /= 100;
        if (tempEdge) {
            centerArrow = new Point2D(endNode.getLocation().getX(), endNode.getLocation().getY());
        }else {
            centerArrow = new Point2D(endNode.getLocation().getX() + (x * procents),
                    endNode.getLocation().getY() + (y * procents));
        }
        procents = tempEdge ? procents : procents * 2;
        x = endNode.getLocation().getX() + (x * procents);
        y = endNode.getLocation().getY() + (y * procents);
        // повернуть точку два раза
        leftArrow = createPartArrow(x, y, centerArrow.getX(), centerArrow.getY(), -35);
        rightArrow = createPartArrow(x, y, centerArrow.getX(), centerArrow.getY(), 35);
    }

    private double[][] createTranslationMatrix(double x, double y){
        double [][] matrix = new double[3][3];
        matrix[0][0] = matrix[1][1] = matrix[2][2] = 1;
        matrix[2][0] = x;
        matrix[2][1] = y;
        return matrix;
    }

    private double[] multipleMatrix(double x, double y, double[][] matrix){
        double _x = (x * matrix[0][0]) + (y * matrix[1][0]) + (1 * matrix[2][0]);
        double _y = (x * matrix[0][1]) + (y * matrix[1][1]) + (1 * matrix[2][1]);
        double _z = (x * matrix[0][2]) + (y * matrix[1][2]) + (1 * matrix[2][2]);
        return new double[]{_x, _y, _z};
    }

    private double[][] createRotateMatrix(double a){
        double [][] matrix = new double[3][3];
        matrix[0][0] = matrix[1][1] = matrix[2][2] = 1;
        double angle = a * (3.14159 / 180.d);
        matrix[0][0] = Math.cos(angle);
        matrix[0][1] = Math.sin(angle);
        matrix[1][0] = -Math.sin(angle);
        matrix[1][1] = Math.cos(angle);
        return matrix;
    }

    private Point2D createPartArrow(double x, double y, double cx, double cy, double a){
        double [][] translCenter = createTranslationMatrix(-cx, -cy);
        double [] vect = multipleMatrix(x, y, translCenter);
        double [][] rot = createRotateMatrix(a);
        vect = multipleMatrix(vect[0], vect[1], rot);
        translCenter = createTranslationMatrix(cx,cy);
        vect = multipleMatrix(vect[0], vect[1], translCenter);
        return new Point2D(vect[0], vect[1]);
    }

    // Edges look like this: sNode(12,43) --> eNode(67,34)
    public String toString() {
        return(startNode.toString() + " --> " + endNode.toString());
    }
    public Node otherEndFrom(Node aNode) {
        if (startNode == aNode)
            return endNode;
        else
            return startNode;
    }

    public void draw(GraphicsContext aPen) {
        // Draw black line from center of startNode to center of endNode
        createArrow();
        if (selected) {
            aPen.setStroke(Color.RED);
            double xDiff = Math.abs(startNode.getLocation().getX() -
                    endNode.getLocation().getX());
            double yDiff = Math.abs(startNode.getLocation().getY() -
                    endNode.getLocation().getY());
            for (int i= -WIDTH/2; i<=WIDTH/2; i++) {
                if (yDiff > xDiff) {
                    aPen.strokeLine(startNode.getLocation().getX() + i,
                            startNode.getLocation().getY(),
                            endNode.getLocation().getX() + i,
                            endNode.getLocation().getY());
                    aPen.strokeLine(centerArrow.getX() + i,
                            centerArrow.getY(),
                            leftArrow.getX() + i,
                            leftArrow.getY());
                    aPen.strokeLine(centerArrow.getX() + i,
                            centerArrow.getY(),
                            rightArrow.getX() + i,
                            rightArrow.getY());
                }
                else {
                    aPen.strokeLine(startNode.getLocation().getX(),
                            startNode.getLocation().getY() + i,
                            endNode.getLocation().getX(),
                            endNode.getLocation().getY() + i);
                    aPen.strokeLine(centerArrow.getX(),
                            centerArrow.getY() + i,
                            leftArrow.getX(),
                            leftArrow.getY() + i);
                    aPen.strokeLine(centerArrow.getX(),
                            centerArrow.getY() + i,
                            rightArrow.getX(),
                            rightArrow.getY() + i);
                }
            }
        }
        else {
            aPen.setStroke(Color.BLACK);
            aPen.strokeLine(startNode.getLocation().getX(),
                    startNode.getLocation().getY(),
                    endNode.getLocation().getX(),
                    endNode.getLocation().getY());
            aPen.strokeLine(centerArrow.getX(),
                    centerArrow.getY(),
                    leftArrow.getX(),
                    leftArrow.getY());
            aPen.strokeLine(centerArrow.getX(),
                    centerArrow.getY(),
                    rightArrow.getX(),
                    rightArrow.getY());
        }
        aPen.setFill(Color.BLACK);
        double xText = (startNode.getLocation().getX() + endNode.getLocation().getX()) / 2;
        double yText = (startNode.getLocation().getY() + endNode.getLocation().getY()) / 2;
        aPen.fillText(label, xText, yText);
    }

   public void saveTo(PrintWriter aFile) {
        aFile.println(label);
        aFile.println((int)startNode.getLocation().getX());
        aFile.println((int)startNode.getLocation().getY());
        aFile.println((int)endNode.getLocation().getX());
        aFile.println((int)endNode.getLocation().getY());
        aFile.println(selected);
    }

    // static methods

    public static Edge loadFrom(BufferedReader aFile) throws IOException {
        Edge anEdge;
        String aLabel = aFile.readLine();
        Node start = new Node("TEMP");
        Node end = new Node("TEMP");
        start.setLocation(Integer.parseInt(aFile.readLine()),
                Integer.parseInt(aFile.readLine()));
        end.setLocation(Integer.parseInt(aFile.readLine()),
                Integer.parseInt(aFile.readLine()));
        anEdge = new Edge(aLabel, start, end);
        anEdge.setSelected(Boolean.valueOf(aFile.readLine()).booleanValue());
        return anEdge;
    }
}
