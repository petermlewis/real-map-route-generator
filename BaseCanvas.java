import javax.swing.*;
import java.util.ArrayList;

public class BaseCanvas extends JComponent {
    ArrayList<Node> allNodes;
    double X_OFFSET;
    double Y_OFFSET;
    double X_SCALE_FACTOR;
    double Y_SCALE_FACTOR;

    public BaseCanvas(ArrayList<Node> allNodes, String AREA_NAME){
        this.allNodes = allNodes;

        if (AREA_NAME.equals("sid")) {
            X_SCALE_FACTOR = 31000;
            Y_SCALE_FACTOR = 22000;

            Y_OFFSET = -0.076;
            X_OFFSET = -51.421;
        } else if(AREA_NAME.equals("bex")){
            X_SCALE_FACTOR = 34000;
            Y_SCALE_FACTOR = 35000;

            Y_OFFSET = -0.122;
            X_OFFSET = -51.447;
        }

    }
}
