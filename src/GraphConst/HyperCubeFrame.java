package GraphConst;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HyperCubeFrame {

    public static void createAndShowGUI(int maxDistance, int bits){
        System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());
        JFrame frame = new JFrame("HyperCube");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Panel(maxDistance, bits));
        frame.pack();
        frame.setVisible(true);
    }
}

class Panel extends JPanel {

    private Vertex vertex;

    private final Set<Vertex> seen = new HashSet<>();

    private final int maxDistance;

    // Bounds to repaint (coordinates of the farthest connected vertices)
    private int leftX, rightX, topY, bottomY;

    public Panel(int maxDistance, int bits) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        new Graph((int)Math.pow(2,bits), bits);

        this.maxDistance = maxDistance;

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int id = findVertex(e.getX(), e.getY());
                if (id != -1) {
                    vertex = Graph.vertices[id];
                    seen.add(vertex);
                    setFurthest(vertex);
                } else {
                    System.out.println("Too far from vertex");
                }
            }

            public void mouseReleased(MouseEvent e) {
                vertex = null;
                topY = bottomY = leftX = rightX = 0;
                seen.clear();
            }

        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (vertex != null)
                    moveVertex(e.getX(), e.getY());
                else
                    System.out.println("Vertex is null");
                seen.clear();
            }
        });

    }

    // Moves private Vertex vertex to cursor's location and moves vertex.neighbors to stay within maxDistance of vertex
    private void moveVertex(int x, int y) {

        if(vertex.point.x != x || vertex.point.y != y) {

            // Set new point for vertex
            vertex.point.x = x;
            vertex.point.y = y;

            // Set of seen vertices to not go back to
            Set<Vertex> seen = new HashSet<>();

            // Queue of parents to call children closer
            Queue<Vertex> parents = new LinkedList<>();

            parents.add(vertex);
            seen.add(vertex);

            while(parents.size() > 0) {

                // Parent is head of queue
                Vertex parent = parents.poll();
                seen.add(parent);

                // Move children while adding them to queue
                for (Vertex child : parent.neighbors) {
                    if(!seen.contains(child)) {
                        moveVertex(child, parent);
                        parents.add(child);
                    }
                }
                for (Vertex child : parent.backNeighbors) {
                    if(!seen.contains(child)) {
                        moveVertex(child, parent);
                        parents.add(child);
                    }
                }

            }

            repaint();
        }
    }

    // Moves toMove such that the distance between toMove and goal is <= maxDistance
    private void moveVertex(Vertex toMove, Vertex goal) {

        int distance = (int) Math.sqrt(Math.pow(toMove.point.x - goal.point.x, 2) + Math.pow(toMove.point.y - goal.point.y, 2));

        while (distance > maxDistance) {
            if (toMove.point.x < goal.point.x) toMove.point.x++;
            else if (toMove.point.x > goal.point.x) toMove.point.x--;
            if (toMove.point.y < goal.point.y) toMove.point.y++;
            else if (toMove.point.y > goal.point.y) toMove.point.y--;
            distance = (int) Math.sqrt(Math.pow(toMove.point.x - goal.point.x, 2) + Math.pow(toMove.point.y - goal.point.y, 2));
        }

    }

    // Finds vertex hovered by the mouse
    private int findVertex(int x, int y) {

        int low = Vertex.diameter;
        int id = -1;

        // Find distance from each vertex and set id for the closest
        for (int i = 0; i < Graph.vertices.length; i++) {
            Vertex v = Graph.vertices[i];
            int distance = (int) Math.sqrt(Math.pow(v.point.x - x, 2) + Math.pow(v.point.y - y, 2));
            if (distance <= low) {
                low = distance;
                id = i;
            }
        }

        return id;
    }

    // Finds furthest coordinates to repaint
    private void setFurthest(Vertex vertex) {
        leftX = leftX == 0 ? vertex.point.x : leftX;
        rightX = rightX == 0 ? vertex.point.x : rightX;
        topY = topY == 0 ? vertex.point.y : topY;
        bottomY = bottomY == 0 ? vertex.point.y : bottomY;

        for (Vertex v : vertex.neighbors) {
            if (v.point.x < leftX) leftX = v.point.x;
            else if (v.point.x > rightX) rightX = v.point.x;
            if (v.point.y < topY) topY = v.point.y;
            else if (v.point.y > bottomY) bottomY = v.point.y;
        }

    }

    public Dimension getPreferredSize() {
        return Graph.dimension;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graph.paintEdges(g);
        Graph.paintVerts(g);
    }
}
