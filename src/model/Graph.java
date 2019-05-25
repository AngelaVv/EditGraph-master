package model;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Graph {

    //vars
    private String label;
    private ArrayList<Node> nodes;

    // get/set/toggle
    public ArrayList<Node> getNodes() { return nodes; }

    public String getLabel() { return label; }

    public void setLabel(String newLabel) { label = newLabel; }

    // Graphs look like this: label(6 nodes, 15 edges)
    public String toString() {
        return(label + "(" + nodes.size() + " nodes, " +
                getEdges().size() + " edges)");
    }

    public ArrayList<Edge> getEdges() {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for (Node n: nodes) {
            for (Edge e: n.incidentEdges()) {
                if (!edges.contains(e)) //so that it is not added twice
                    edges.add(e);
            }
        }
        return edges;
    }

    // constr
    public Graph() { this("", new ArrayList<Node>()); }

    public Graph(String aLabel) { this(aLabel, new ArrayList<Node>()); }

    public Graph(String aLabel, ArrayList<Node> initialNodes) {
        label = aLabel;
        nodes = initialNodes;
    }

    // methods
    public void addNode(Node aNode) {
        nodes.add(aNode);
    }

    public void addEdge(String name, Node start, Node end) {
        // First make the edge
        Edge anEdge = new Edge(name, start, end);
        // Now tell the nodes about the edge
        start.addIncidentEdge(anEdge);
        end.addIncidentEdge(anEdge);
    }

    public void addEdge(Node start, Node end) {
        // First make the edge
        Edge anEdge = new Edge(start, end);
        // Now tell the nodes about the edge
        boolean result = false;
        Node [] nodes = new Node[]{start, end};
        for(Node n: nodes) {//проверка стрелки на существование
            for (Edge e : n.incidentEdges()) {
                if ((e.getStartNode().getLocation().equals(start.getLocation()) &&
                        e.getEndNode().getLocation().equals(end.getLocation())) ||
                        (e.getEndNode().getLocation().equals(start.getLocation()) &&
                                e.getStartNode().getLocation().equals(end.getLocation()))) {
                    result = true;
                    break;
                }
            }
        }
        if(!result) {
            start.addIncidentEdge(anEdge);
            end.addIncidentEdge(anEdge);
        }else{
            //TODO: alert?
        }
    }

    public void deleteEdge(Edge anEdge) {
    // Just ask the nodes to remove it
        anEdge.getStartNode().incidentEdges().remove(anEdge);
        anEdge.getEndNode().incidentEdges().remove(anEdge);
    }

    public void deleteNode(Node aNode) {
    // Remove the opposite node's incident edges
        for (Edge e: aNode.incidentEdges())
            e.otherEndFrom(aNode).incidentEdges().remove(e);
        nodes.remove(aNode); // Remove the node now
    }

    public static Graph example() {
        Graph myMap = new Graph("Ontario and Quebec");
        Node ottawa, toronto, kingston, montreal;
        myMap.addNode(ottawa = new Node("Ottawa", "A", new Point2D(450,100)));
        myMap.addNode(toronto = new Node("Toronto", "B", new Point2D(100,170)));
        myMap.addNode(kingston = new Node("Kingston", new Point2D(280,260)));
        myMap.addNode(montreal = new Node("Montreal", "Hello World", new Point2D(125,50)));
        myMap.addEdge("test1", ottawa, toronto);
        myMap.addEdge("test2", ottawa, montreal);
        myMap.addEdge("test2", ottawa, kingston);
        myMap.addEdge(kingston, toronto);
        return myMap;
    }

    public Node nodeNamed(String aLabel) {
        for (Node n: nodes)
            if (n.getLabel().equals(aLabel))
                return n;
        return null; // If we don't find one
    }

    public void addEdge(String startLabel, String endLabel) {
        Node start = nodeNamed(startLabel);
        Node end = nodeNamed(endLabel);
        if ((start != null) && (end != null))
            addEdge(start, end);
    }

    public void draw(GraphicsContext aPen) {
        ArrayList<Edge> edges = getEdges();
        for (Edge e: edges) // Draw the edges first
            e.draw(aPen);
        for (Node n: nodes) // Draw the nodes second
            n.draw(aPen);
    }

    public Node nodeAt(double x, double y) {
        for (int i=nodes.size()-1; i>=0; i--) {
            Node n = nodes.get(i);
            Point2D c = n.getLocation();
            double d = (x - c.getX()) * (x - c.getX()) +
                    (y - c.getY()) * (y - c.getY());
            if (d <= (Node.RADIUS*Node.RADIUS))
                return n;
        }
        return null;
    }

    public ArrayList<Node> selectedNodes() {
        ArrayList<Node> selected = new ArrayList<Node>();
        for (Node n: nodes)
            if (n.isSelected())
                selected.add(n);
        return selected;
    }

    public Edge edgeAt(double x, double y) {
        for (Edge e: getEdges()) {
            Node n1 = e.getStartNode();
            Node n2 = e.getEndNode();
            if(rayPickOnLine(x, y, n1.getLocation().getX(), n1.getLocation().getY(), n2.getLocation().getX(), n2.getLocation().getY()) ||
                    rayPickOnLine(x, y, e.getLeftArrow().getX(), e.getLeftArrow().getY(), e.getCenterArrow().getX(), e.getCenterArrow().getY()) ||
                    rayPickOnLine(x, y, e.getRightArrow().getX(), e.getRightArrow().getY(), e.getCenterArrow().getX(), e.getCenterArrow().getY())){
                return e;
            }
        }
        return null;
    }

    public boolean rayPickOnLine(double x, double y, double x1, double y1, double x2, double y2){
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        double distance = Math.abs(xDiff*(y2 - y) -
                (x2 - x)*yDiff) /
                Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        if (distance <= 5) {
            if (Math.abs(xDiff) > Math.abs(yDiff)) {
                if (((x < x1) && (x > x2)) || ((x > x1) && (x < x2))) {
                    return true;
                }
            }
            else if (((y < y1) && (y > y2)) || ((y > y1) && (y < y2))) {
                return true;
            }
        }
        return false;
    }


    public ArrayList<Edge> selectedEdges() {
        ArrayList<Edge> selected = new ArrayList<Edge>();
        for (Edge e: getEdges())
            if (e.isSelected())
                selected.add(e);
        return selected;
    }

    public void saveTo(PrintWriter aFile) {
        aFile.println(label);
        // Output the nodes
        aFile.println(nodes.size());
        for (Node n: nodes)
            n.saveTo(aFile);
        // Output the edges
        ArrayList<Edge> edges = getEdges();
        aFile.println(edges.size());
        for (Edge e: edges)
            e.saveTo(aFile);
    }

    public static Graph loadFrom(BufferedReader aFile) throws IOException {
        // Read the label from the file and make the graph
        Graph aGraph = new Graph(aFile.readLine());
        // Get the nodes and edges
        int numNodes = Integer.parseInt(aFile.readLine());
        for (int i=0; i<numNodes; i++)
            aGraph.addNode(Node.loadFrom(aFile));
        // Now connect them with new edges
        int numEdges = Integer.parseInt(aFile.readLine());
        for (int i=0; i<numEdges; i++) {
            Edge tempEdge = Edge.loadFrom(aFile);
            Node start = aGraph.nodeAt(
                    tempEdge.getStartNode().getLocation().getX(),
                    tempEdge.getStartNode().getLocation().getY());
            Node end = aGraph.nodeAt(tempEdge.getEndNode().getLocation().getX(),
                    tempEdge.getEndNode().getLocation().getY());
            aGraph.addEdge(start, end);
        }
        return aGraph;
    }

}

