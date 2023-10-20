package GraphConst;

// Draw graph from graph class
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {

    static Vertex[] vertices;

    static Dimension dimension;

    public Graph(int vertexCount, int bits) {

        vertices = new Vertex[vertexCount];

        // Initialize vertices
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vertex(i);
        }

        // Set neighbors in vertices after all vertices are initialized
        setVertices(bits);

        setPoints(dimension, bits);
    }

    // Populate the array of vertices
    // Time complexity: O(2^N) where N is number of bits
    private void setVertices(int bits) {

        // Get number of edges
        // 2^bits is number of vertices
        // bits/2 is edges per vertex since it's one way
        int edgeCount = (int) Math.pow(2,bits) * bits / 2;

        int toAdd = 1;

        // round: tracks appropriate behavior to expand the (+1,+2,-2,-1) pattern
        int round = 1;

        // Populate neighbor list in vertices
        // i will never be >= vertices.length since vertices.length is declared based on vertexCount which is set based on bits (in the Panel constructor)
        // O(2^N) where N is number of bits
        for (int i = 0; edgeCount > 0; i++) {

            vertices[i].neighbors.add(vertices[i + toAdd]);

            // set toAdd to the next value according to the (+1,+2,-2,-1) pattern and current position within the pattern
            if (i % (4 * round) == round - 1) toAdd *= 2;
            else if (i % (4 * round) == 2 * round - 1 || i % (4 * round) == 4 * round - 1) toAdd *= -1;
            else if (i % (4 * round) == 3 * round - 1) toAdd /= 2;
            edgeCount--;

            // Reset the loop, extending toAdd and round
            if(i == vertices.length - 1) {
                i = -1;
                toAdd *= 4;
                round *= 4;
            }

        }

        // Populate backNeighbors of Vertex vi in Vertex v.neighbors
        // O(M * N) where M is number of vertices, N is number of bits
        // O(M log M) where M is number of vertices
        // O(2^N * N) where N is number of bits
        for (Vertex v : vertices) {
            for (Vertex vi : v.neighbors) {
                vi.backNeighbors.add(v);
            }
        }
    }

    public String toString() {

        StringBuilder str = new StringBuilder("[" + vertices[0]);

        for (int i = 1; i < vertices.length; i++) {
            str.append(", \n ").append(vertices[i]);
        }

        str.append("]");

        return str.toString();
    }

    public static void setDimension(Dimension d) {
        dimension = d;
    }

    public void setPoints(Dimension d, int bits) {

        int xdiv, ydiv, x, y, row = 1, col = 1;

        // If it's not a perfect square, put the long side of the rectangle on x axis
        if (bits % 2 == 1) {
            ydiv = (int) Math.sqrt(Math.pow(2, bits - 1));
            xdiv = ydiv * 2;
        } else {
            ydiv = (int) Math.sqrt(Math.pow(2, bits));
            xdiv = ydiv;
        }

        for (int i = 0; i < vertices.length; i++) {

            x = (int) Math.ceil( (double) d.width / (xdiv + 1));
            y = (int) Math.ceil( (double) d.height / (ydiv + 1));

            while (y < d.height) {

                while (x < d.width && i < vertices.length) {
                    vertices[i++].point = new Point(x,y + (int) Math.ceil( (double) d.width / (xdiv + 1))/2 * (col++ % 2));
                    x += (int) Math.ceil( (double) d.width / (xdiv + 1));
                }

                y += (int) Math.ceil( (double) d.height / (ydiv + 1));
                x = (int) Math.ceil( (double) d.width / (xdiv + 1)) + (int) Math.ceil( (double) d.width / (xdiv + 1))/2 * (row++ % 2);

            }

        }
    }

    public static void paintVerts(Graphics g) {

        g.setColor(Color.BLACK);
        Color c = new Color(200,5,200);

        for (Vertex v : vertices) {
            g.drawOval(v.point.x - Vertex.diameter/2, v.point.y - Vertex.diameter/2, Vertex.diameter, Vertex.diameter);
            c = g.getColor();
            g.setColor(Color.WHITE);
            g.fillOval(v.point.x - Vertex.diameter/2 + 1, v.point.y - Vertex.diameter/2 + 1, Vertex.diameter - 2, Vertex.diameter - 2);
            g.setColor(Color.BLACK);
            g.drawString("" + v.id, v.point.x - Vertex.diameter/4, v.point.y + Vertex.diameter/4);
            g.setColor(c);
        }
    }

    public static void paintEdges(Graphics g) {

        g.setColor(Color.BLACK);

        for (Vertex v : vertices) {
            for (Vertex n : v.neighbors) {
                g.drawLine(v.point.x, v.point.y, n.point.x, n.point.y);
            }
        }
    }
}
