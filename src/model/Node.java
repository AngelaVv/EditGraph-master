package model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Node {


    private String label;
    private String labelIn;
    private Point2D location;
    private Color colorNode;
    private ArrayList<Edge> incidentEdges= new ArrayList<Edge>();
    private boolean selected;


    public static Color colorDefault = Color.color(0.6, 0.6, 1.0);
    public static int RADIUS = 15;


    public boolean isSelected() { return selected; }

    public void setSelected(boolean state) { selected = state; }

    public void toggleSelected() { selected = !selected; }

    public String getLabel() { return label; }

    public Point2D getLocation() { return location; }

    public void setLabel(String newLabel) { label = newLabel; }

    public void setLocation(Point2D aPoint) { location = aPoint; }

    public void setLocation(double x, double y) { location = new Point2D(x, y); }

    public Color getColorNode() {
        return colorNode;
    }

    public void setColorNode(Color colorNode) {
        this.colorNode = colorNode;
    }

    public String getLabelIn() {
        return labelIn;
    }

    public void setLabelIn(String labelIn) {
        this.labelIn = labelIn;
    }

    public Node() { this("", new Point2D(0,0)); }

    public Node(String aLabel) { this(aLabel, new Point2D(0,0)); }

    public Node(Point2D aPoint) { this("", aPoint); }

    public Node(double x, double y) { this("", new Point2D(x,y)); }

    public Node(String aLabel, Point2D aPoint) {
        label = aLabel;
        labelIn = "";
        location = aPoint;
        colorNode = colorDefault;
    }

    public Node(String aLabel,String inLabel, Point2D aPoint) {
        label = aLabel;
        labelIn = inLabel;
        location = aPoint;
        colorNode = colorDefault;
    }

    public String toString() {
        return(label + "(" + location.getX() + "," + location.getY() + ")");
    }

    public ArrayList<Edge> incidentEdges() {
        return incidentEdges;
    }

    public void addIncidentEdge(Edge e) {
        incidentEdges.add(e);
    }

    public ArrayList<Node> neighbours() {
        ArrayList<Node> result = new ArrayList<Node>();
        for (Edge e: incidentEdges)
            result.add(e.otherEndFrom(this));
        return result;
    }

    public void draw(GraphicsContext aPen) {
        // Draw a blue-filled circle around the center of the node
        if (selected)
            aPen.setFill(Color.RED);
        else
            aPen.setFill(colorNode);
        aPen.fillOval(location.getX() - RADIUS, location.getY() - RADIUS,
                RADIUS*2, RADIUS*2);
        // Draw a black border around the circle
        aPen.setStroke(Color.BLACK);
        aPen.strokeOval(location.getX() - RADIUS, location.getY() - RADIUS,
                RADIUS*2, RADIUS*2);
        // Draw a label at the top right corner of the node and in node
        aPen.setFill(Color.BLACK);
        int _x = (int)location.getX() - 5;
        int _y = (int)location.getY() + 5;
        aPen.fillText(labelIn, _x, _y);
        aPen.setFont(Font.font("Arial", 14));
        aPen.fillText(label, location.getX() + RADIUS, location.getY() - RADIUS);
    }

    public void saveTo(PrintWriter aFile) {
        aFile.println(labelIn);
        aFile.println(label);
        aFile.println((int)location.getX());
        aFile.println((int)location.getY());
        aFile.println(selected);
        aFile.println(colorNode.toString());
    }

    public static Node loadFrom(BufferedReader aFile) throws IOException {
        Node aNode = new Node();
        aNode.setLabelIn(aFile.readLine());
        aNode.setLabel(aFile.readLine());
        aNode.setLocation(Integer.parseInt(aFile.readLine()),
                Integer.parseInt(aFile.readLine()));
        aNode.setSelected(Boolean.valueOf(aFile.readLine()).booleanValue());
        aNode.setColorNode(Color.valueOf(aFile.readLine()));
        return aNode;
    }
}