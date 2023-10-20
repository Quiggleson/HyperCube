package GraphConst;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
public class Vertex {

    public Point point;

    public static final int diameter = 20;

    public List<Vertex> neighbors;
    public List<Vertex> backNeighbors;

    public int id;

    public Vertex(int id) {

        neighbors = new ArrayList<>();
        backNeighbors = new ArrayList<>();
        this.id = id;

    }

    public String toString() {
        StringBuilder str = new StringBuilder("[" + id);
        if (id < 10) str.append(" ");
        for (Vertex v : neighbors) {
            str.append(",").append(v.id);
            if (v.id < 10) str.append(" ");
        }
        str.append("]");
        return str.toString();
    }

}
