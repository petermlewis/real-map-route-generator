import java.util.*;

public class MainLoopOfGA {
    private ArrayList<Integer> shortestRouteOverall;
    private double shortestRouteLength;

    public MainLoopOfGA(int START_INDEX, int END_INDEX, ArrayList<Node> allNodes, int[][] adjMat) {
        GeneticAlgorithm ga = new GeneticAlgorithm(allNodes, adjMat);

        final int ROUTES_PER_GEN = 200;
        final int BEST_N_OF_GEN = 40;
        final int N_OF_GENS = 50;

        ArrayList<ArrayList<Integer>> allRoutesInThisGeneration = new ArrayList<>();

        //initialises routes, without any loops
        int added = 0;
        while (added < ROUTES_PER_GEN){
            ArrayList<Integer> randomRoute = ga.createRandomRoute(START_INDEX, END_INDEX);
            ArrayList<Integer> routeWithNoLoops = (ArrayList<Integer>) ga.removeAllRedundantLoops(randomRoute);

            //add only valid routes
            if (routeWithNoLoops.getLast() == END_INDEX){
                allRoutesInThisGeneration.add(routeWithNoLoops);
                added++;
            }
        }

        //shortest routes by distance
        allRoutesInThisGeneration.sort(Comparator.comparingDouble(ga::evaluateFitness));

        //shortest N of the generation, for initial generation
        ArrayList<ArrayList<Integer>> bestRoutesInGeneration = sliceArrayListInteger(0, BEST_N_OF_GEN, allRoutesInThisGeneration);

        /*            work out fitness score (higher is better) by getting the longest route this generation
            and taking each other score away from the longest route length, +1, such that the longest
            route has a 1 in however many chance of being chosen. and the shortest route has the
            greatest chance of being chosen
        */

        Random rand = new Random();

        //initialise this value
        shortestRouteOverall = allRoutesInThisGeneration.getFirst();
        ArrayList<Integer> bestRouteOfGen = new ArrayList<>();

        //Main loop of the Genetic Algorithm
        for (int j = 0; j < N_OF_GENS; j++) {
            //take best N
            // combine them into say 100 routes, take N best and repeat until small
            allRoutesInThisGeneration = new ArrayList<>();

            //crosses over every single possible route between all routes in array
            for (int route1 = 0; route1 < BEST_N_OF_GEN; route1++) {
                for (int route2 = 0; route2 < BEST_N_OF_GEN; route2++) {
                    ArrayList<Integer> route = ga.crossOver3(bestRoutesInGeneration.get(route1), bestRoutesInGeneration.get(route2));

                    //mutate 3% of routes
                    if (rand.nextInt(100) <= 4) {
                        route = ga.mutate(route);
                    }

                    route = ga.removeAllRedundantLoops(route);

                    //add only valid routes
                    if (route.getLast() == END_INDEX){
                        allRoutesInThisGeneration.add(route);
                    }
                }
            }

            //finds the longest route for roulette selection
            double longestRouteLengthOfThisGen = 0;

            for (int i = 0; i < allRoutesInThisGeneration.size(); i++) {
                if (ga.evaluateFitness(allRoutesInThisGeneration.get(i)) > longestRouteLengthOfThisGen) {
                    longestRouteLengthOfThisGen = ga.evaluateFitness(allRoutesInThisGeneration.get(i));
                }
            }

            // x10000 because using geographical lon and lat, they are all very similar
            // and very close to 0, meaning casting them into an int for the
            // bucketing later was ineffective
            longestRouteLengthOfThisGen = longestRouteLengthOfThisGen * 10000;

            //fitness scores placed into here, for roulette selection to occur
            double[] fitnessScoresOfGen = new double[allRoutesInThisGeneration.size()];

            // a route has a fitness score of at least 1 if they have the longest route, and a score of
            // LONGEST - SHORTEST for the shortest route, which should have the greatest value
            for (int i = 0; i < allRoutesInThisGeneration.size(); i++) {
                fitnessScoresOfGen[i] = longestRouteLengthOfThisGen + 1 - (ga.evaluateFitness(allRoutesInThisGeneration.get(i)) * 10000);
            }

            double[] cumulativeFitnessScoresOfGen = new double[allRoutesInThisGeneration.size()];

            //adds the score based on the previous value, making it cumulative
            cumulativeFitnessScoresOfGen[0] = fitnessScoresOfGen[0];

            for (int i = 1; i < allRoutesInThisGeneration.size(); i++) {
                cumulativeFitnessScoresOfGen[i] = cumulativeFitnessScoresOfGen[i - 1] + fitnessScoresOfGen[i];
            }

            for (int i = 0; i < BEST_N_OF_GEN; i++) {
                //from 0 to largest value of roulette wheel
                int rouletteBall = rand.nextInt((int) cumulativeFitnessScoresOfGen[allRoutesInThisGeneration.size() - 1]);

                //binary search to find where the roulette ball can be inserted
                int index = binarySearchForRoulette(rouletteBall, cumulativeFitnessScoresOfGen, 0, cumulativeFitnessScoresOfGen.length - 1);
                bestRoutesInGeneration.add(allRoutesInThisGeneration.get(index));
            }

        }
        bestRouteOfGen = bestRoutesInGeneration.getFirst();

        if (ga.evaluateFitness(bestRouteOfGen) < ga.evaluateFitness(shortestRouteOverall)) {
            shortestRouteOverall = bestRouteOfGen;
        }

        shortestRouteLength = ga.evaluateFitness(shortestRouteOverall);
        System.out.println(shortestRouteLength);
        //System.out.print(ga.calcOverallTrueDistance(shortestRouteOverall)+" and then euclidean|: ");
    }


    public ArrayList<Integer> getBestRoute(){
        return shortestRouteOverall;
    }

    public double getBestRouteLength(){
        System.out.println(shortestRouteLength);
        return shortestRouteLength;
    }

    private int binarySearchForRoulette(int rouletteBall, double[] rouletteWheel, int lowerBound, int upperBound){
        int mid = ((lowerBound+upperBound)/2);
        if (upperBound > lowerBound) {
            mid = (lowerBound + (upperBound - lowerBound) / 2);

            // If the element is present at the
            // middle itself
            if (rouletteWheel[mid] == rouletteBall)
                return mid;

            // If element is smaller than mid, then
            // it can only be present in left subarray
            if (rouletteWheel[mid] > rouletteBall)
                return binarySearchForRoulette(rouletteBall, rouletteWheel, lowerBound, mid - 1);

            // Else the element can only be present
            // in right subarray
            return binarySearchForRoulette(rouletteBall, rouletteWheel, mid + 1, upperBound);
        } else{
            return mid;
        }
    }

    private ArrayList<ArrayList<Integer>> sliceArrayListInteger(int startIndex, int endIndex, ArrayList<ArrayList<Integer>> arrayListToChop){
        ArrayList<ArrayList<Integer>> slicedArrayList = new ArrayList<>();

        for (int i=startIndex; i < endIndex; i++){
            slicedArrayList.add(arrayListToChop.get(i));
        }

        return slicedArrayList;
    }
}
