import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        /*
        * These .csv files contain real road data from sidcup to bexleyheath,
        * Meaning they are actually useful. They are in a slightly different format
        * so have to work out how to parse them precisely.
        * */
        String NODE_DATA_FILE_NAME = "bex_node_data.csv";
        String ADJACENCY_MATRIX = "bex_adjacency_matrix.csv";

        // In order to deal with scaling for different data sets
        String AREA_NAME = NODE_DATA_FILE_NAME.substring(0,3);

        ArrayList<Node> allNodes = FileHandler.readNodesFromFile(NODE_DATA_FILE_NAME);
        int[][] adjacencyMatrix = FileHandler.getMatrixData(ADJACENCY_MATRIX);
        String[] allNodeNames = FileHandler.getNodeNameArray(NODE_DATA_FILE_NAME);

        GUI graphics = new GUI(allNodes, adjacencyMatrix, allNodeNames, AREA_NAME);

    }
}