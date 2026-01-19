import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    //adjList faster than adjMat for neighbour lookup
    private final ArrayList<Integer>[] adjList;
    private final double[][] costMat;

    private final ArrayList<Node> allNodes;
    private final int numOfNodes;

    private final double MAX_DISTANCE = 1.0e9;

    public GeneticAlgorithm(ArrayList<Node> allNodes, int[][] adjMat) {
        this.allNodes = allNodes;
        this.numOfNodes = allNodes.size();

        this.adjList = new ArrayList[numOfNodes];
        this.costMat = new double[numOfNodes][numOfNodes];

        for (int i = 0; i < numOfNodes; i++) {
            adjList[i] = new ArrayList<>();
            for (int j = 0; j < numOfNodes; j++) {
                if (adjMat[i][j] == 1) {
                    // distances all calculated at start, saving time
                    double distance = allNodes.get(i).distanceTo(allNodes.get(j));
                    costMat[i][j] = distance;

                    // adds a value to the arraylist for each row
                    adjList[i].add(j);
                } else {
                    // add very high cost to unconnected edges
                    // to deal with rare bug where non-true edges are forced to connect
                    costMat[i][j] = MAX_DISTANCE;
                }
            }
        }
    }

    //uses costMat to lookup distances
    public double evaluateFitness(ArrayList<Integer> route) {
        double totalDistance = 0;

        for (int i = 0; i < route.size() - 1; i++) {
            int nodeA = route.get(i);
            int nodeB = route.get(i+1);

            // this is where lots of time is saved, no calculating on the spot
            double distance = costMat[nodeA][nodeB];

            // Count the errors
            if (distance < MAX_DISTANCE) {
                totalDistance += distance;
            }
        }

        return totalDistance;
    }

    //crosses over multiple routes where they are closest
    public ArrayList<Integer> crossOver3(ArrayList<Integer> route1, ArrayList<Integer> route2) {
        Random random = new Random();

        ArrayList<Integer> finalRoute = new ArrayList<>();
        double shortestDistance = MAX_DISTANCE;
        int currentClosestRoute1 = 0;
        int currentClosestRoute2 = 0;

        // don't check between the entire routes, only the middle parts, as that is where the most distance can be saved
        //int searchWindow = 7;
        int endI = route1.size() - 2;
        int endJ = route2.size() - 2;

        //take the roughly 25 shortest and choose one at random
        ArrayList<double[]> candidateCuts = new ArrayList<>();
        double shortestDistanceThreshold = 5; // check all shortish routes

        // find closest nodes between routes apart from endpoints
        for (int i = 2; i < endI; i++) {
            for (int j = 2; j < endJ; j++) {
                Node node1 = allNodes.get(route1.get(i));
                Node node2 = allNodes.get(route2.get(j));
                double testDistance = node1.distanceTo(node2);

                if (testDistance < shortestDistance && testDistance > 0) {
                    shortestDistance = testDistance;
                    currentClosestRoute1 = i;
                    currentClosestRoute2 = j;
                }

                // add all points that are pretty short, and randomly choose one
                // to ensure some randomness
                if (testDistance <= shortestDistance + shortestDistanceThreshold) {
                    candidateCuts.add(new double[] {testDistance, (double)i, (double)j});
                }

                if (!candidateCuts.isEmpty()) {
                    double[] chosenCut = candidateCuts.get(random.nextInt(candidateCuts.size()));
                    currentClosestRoute1 = (int)chosenCut[1];
                    currentClosestRoute2 = (int)chosenCut[2];
                }
            }
        }

        List<Integer> finalRoutePart1 = route1.subList(0, currentClosestRoute1);
        List<Integer> finalRoutePart2 = route2.subList(currentClosestRoute2 + 1, route2.size());

        int nodeA = route1.get(currentClosestRoute1);
        int nodeB = route2.get(currentClosestRoute2);

        //random walk in between the routes
        List<Integer> path = createRandomRoute(nodeA, nodeB);

        // adds all component parts of the route
        finalRoute.addAll(finalRoutePart1);
        finalRoute.addAll(path);
        finalRoute.addAll(finalRoutePart2);

        return finalRoute;
    }

    //randomly changes a route
    public ArrayList<Integer> mutate(ArrayList<Integer> route) {
        Random rand = new Random();

        // picks 2 different random nodes along the route
        int a = rand.nextInt(route.size());
        int b = rand.nextInt(route.size());
        while (a == b) {
            b = rand.nextInt(route.size());
        }

        int startIndex = Math.min(a, b);
        int endIndex = Math.max(a, b);

        int nodeA = route.get(startIndex);
        int nodeB = route.get(endIndex);

        /*MUTATION:
         - pick 2 random nodes along a route
         - randomly walk between them
         - new route is the same route but the random walk between a and b is now included
        * */
        List<Integer> routeBefore = route.subList(0, startIndex);
        ArrayList<Integer> finalRoute = new ArrayList<>(routeBefore);

        ArrayList<Integer> mutatedInbetweenRoute = createRandomRoute(nodeA, nodeB);
        finalRoute.addAll(mutatedInbetweenRoute);

        //if the inbetween route doesnt connect to the last node
        if (endIndex + 1 < route.size()) {
            List<Integer> routeAfter = route.subList(endIndex + 1, route.size());
            finalRoute.addAll(routeAfter);
        }

        return finalRoute;
    }

    public ArrayList<Integer> removeAllRedundantLoops(ArrayList<Integer> route) {
    Stack routeAsStack = new Stack();

        // has a faster .contains method
        // which makes HashSet more appropriate
    HashSet<Integer> pathSet = new HashSet<>();

    for (int currentNode : route) {
            // not been seen yet
        if (!pathSet.contains(currentNode)) {
            routeAsStack.push(currentNode);
            pathSet.add(currentNode);
        } else { // a loop has been found
            int poppedNode = -1;

                //remove the node at the top of the stack until it reaches the duplicate
            while (poppedNode != currentNode){
                    // removes the nodes in the redundant loop
                poppedNode = routeAsStack.pop();
                pathSet.remove(poppedNode);
            }

                // re adds the node that made up either side of the loop
            routeAsStack.add(currentNode);
            pathSet.add(currentNode);
        }
    }

        return routeAsStack;
    }


    public ArrayList<Integer> createRandomRoute(int nodeA, int nodeB) {
        ArrayList<Integer> route = new ArrayList<>();

        //use hashset again for fast .contains
        HashSet<Integer> visited = new HashSet<>();
        Random rand = new Random();

        int current = nodeA;
        route.add(current);
        visited.add(current);

        int maxSteps = numOfNodes * 10;
        int steps = 0;

        //while it hasnt reached the end yet
        while (current != nodeB && steps < maxSteps) {
            ArrayList<Integer> neighbours = adjList[current];

            // uses some aspects of A* to speed up random route generation
            double bestScore = Double.POSITIVE_INFINITY;
            int bestNext = -1;

            for (int neigh : neighbours) {
                // distance from goal is the A* addition, closer to goal the better that node is
                double distFromGoal = costMat[neigh][nodeB];

                // disincentivise visiting already visited nodes
                double revisitCost = 0;

                if (visited.contains(neigh)){
                    revisitCost = 0.001;
                }

                // randomness for more exploring
                double randomness = rand.nextDouble() * 15;

                double score = distFromGoal + revisitCost + randomness;

                if (score < bestScore) {
                    bestScore = score;
                    bestNext = neigh;
                }
            }

            // moves to next node
            current = bestNext;
            route.add(current);
            visited.add(current);

            steps++;
        }

        return route;
    }

    public double calcOverallTrueDistance(ArrayList<Integer> route){
        double distance = 0;

        for (int i=0; i < route.size()-1; i++){
            distance = distance + allNodes.get(route.get(i)).distanceInMetres(allNodes.get(route.get(i)));
        }

        return distance;
    }
}