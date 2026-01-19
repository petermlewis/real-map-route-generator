import java.awt.*;
import java.util.ArrayList;

public class RouteCanvas extends BaseCanvas {
    private static ArrayList<Integer> generatedRoute;

    public RouteCanvas(ArrayList<Integer> generatedRoute, ArrayList<Node> allNodes, String AREA_NAME){
        super(allNodes,AREA_NAME);
        this.generatedRoute = generatedRoute;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set line color and thickness
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(4));

        //draw the route assuming lines are connected
        int startY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(0)).getX()) * X_SCALE_FACTOR);
        int startX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(0)).getY()) * Y_SCALE_FACTOR);

        int endY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(generatedRoute.size()-1)).getX()) * X_SCALE_FACTOR);
        int endX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(generatedRoute.size()-1)).getY()) * Y_SCALE_FACTOR);

        g2d.drawString("Start", startX,650- startY);
        g2d.drawString("End", endX, 650-endY);

        for (int i = 1; i < generatedRoute.size()-1; i++){
            startY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(i)).getX()) * X_SCALE_FACTOR);
            startX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(i)).getY()) * Y_SCALE_FACTOR);

            endY = (int) ((X_OFFSET + allNodes.get(generatedRoute.get(i+1)).getX()) * X_SCALE_FACTOR);
            endX = (int) ((Y_OFFSET + allNodes.get(generatedRoute.get(i+1)).getY()) * Y_SCALE_FACTOR);

            //System.out.print(allNodes.get(generatedRoute.get(i)).getRoadName()+ " to ");

            g2d.drawLine(startX, 650-startY, endX, 650-endY);
        }

    }

    public void setRoute(ArrayList<Integer> route){
        generatedRoute = route;
    }

    public ArrayList<Integer> getRoute(){
        return generatedRoute;
    }


}

