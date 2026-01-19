import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public class Node {
    private final double[] coords;
    private final String roadName;

    public Node(double[] coords, String roadName){
        this.coords = coords;
        this.roadName = roadName;
    }

    public double[] getCoords(){
        return coords;
    }

    public double getX(){
        return coords[0];
    }

    public double getY(){
        return coords[1];
    }

    public String toString(){
        double x = getX();
        double y = getY();

        String strX = Double.toString(x);
        String strY = Double.toString(y);

        return strX + "," + strY + ", "+getRoadName();
    }

    //returns the distance to another node
    public double distanceTo(Node matilda){
        double distance;

        double changeInX = Math.abs(coords[0] - matilda.getCoords()[0]);
        double changeInY = Math.abs(coords[1] - matilda.getCoords()[1]);

        //use pythagorean theorem to work out the distance diagonally
        // distances are small enough to approximate to a flat area
        distance = Math.sqrt(Math.pow(changeInX,2) + Math.pow(changeInY,2));

        return distance;
    }

    public double distanceInMetres(Node otherNode){
        double RADIUS = 6371;

        double lat1Rad = Math.toRadians(getX());
        double lat2Rad = Math.toRadians(getY());
        double lon1Rad = Math.toRadians(otherNode.getX());
        double lon2Rad = Math.toRadians(otherNode.getY());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * RADIUS;

        return distance;
    }

    /*public double distanceInMetres(Node otherNode){

            //double startLat, double startLong, double endLat, double endLong) {
        double RADIUS = 6371;

        double startLat = getX();
        double startLong = getY();
        double endLat = otherNode.getX();
        double endLong = otherNode.getY();

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * c;
    }

    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }*/

    public String getRoadName(){
        return roadName;
    }


}
