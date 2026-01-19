import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Canvas extends BaseCanvas {
    private final int[][] adjMatrix;

    public Canvas(ArrayList<Node> allNodes, int[][] adjMatrix, String AREA_NAME){
        super(allNodes,AREA_NAME);
        this.adjMatrix = adjMatrix;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(3)); // Line thickness = 2

        //just drawing dots, so start and end X and Y will be the same
        for (Node allNode : allNodes) {

            int startY = (int) ((X_OFFSET + allNode.getX()) * X_SCALE_FACTOR);
            int startX = (int) ((Y_OFFSET + allNode.getY()) * Y_SCALE_FACTOR);

            g.drawLine(startX, 650 - startY, startX, 650 - startY);
        }

        //draws lines based on an adjacency matrix
        Stroke roadStroke = new BasicStroke(2);

        //this traverses only the part above the line of symmetry
        for (int i = 0; i < adjMatrix.length; i++){
            for (int j = i; j < adjMatrix[0].length; j++){
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setStroke(roadStroke);

                //if the row and column share an edge
                if (adjMatrix[i][j] >= 1){
                    int startY = (int) ((X_OFFSET + allNodes.get(i).getX()) * X_SCALE_FACTOR);
                    int startX = (int) ((Y_OFFSET + allNodes.get(i).getY()) * Y_SCALE_FACTOR);

                    int endY = (int) ((X_OFFSET + allNodes.get(j).getX()) * X_SCALE_FACTOR);
                    int endX = (int) ((Y_OFFSET + allNodes.get(j).getY()) * Y_SCALE_FACTOR);

                    g.drawLine(startX, 650-startY, endX, 650-endY);
                }
            }
        }
    }
}

