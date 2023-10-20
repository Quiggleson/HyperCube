package EndUser;

import GraphConst.*;
import java.awt.Dimension;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Graph.setDimension(new Dimension(1000, 1000));
            //Graph g = new Graph(16, 4);
            HyperCubeFrame.createAndShowGUI(100, 6);
            //System.out.println(g);
        });
    }
}
