package diver;

import game.*;
import graph.ShortestPaths;

import java.util.*;


/** This is the place for your implementation of the {@code SewerDiver}.
 */
public class McDiver implements SewerDiver {
    private void dfs(SeekState state, long curr, Map<Long, Boolean> visited, Map<Long, Boolean> noVisit) {
        if (state.distanceToRing() == 0) {
            return;
        }
        visited.put(curr, true);
        long bestNeighbor = -1;
        int dist = 1000000000;

        for (NodeStatus neighbor : state.neighbors()) {
            long neighborId = neighbor.getId();
            if (!visited.containsKey(neighborId) && neighbor.getDistanceToRing() < dist && !noVisit.containsKey(neighborId)) {
                dist = neighbor.getDistanceToRing();
                bestNeighbor = neighborId;
            }
        }
        if (!visited.containsKey(bestNeighbor)) {
            visited.put(bestNeighbor, true);
        }
        if (bestNeighbor != -1) {
            if (state.neighbors().size()==1) {
                noVisit.put(state.currentLocation(), true);
            }
            state.moveTo(bestNeighbor);
        }
        else {
            noVisit.put(state.currentLocation(), true);
            visited.clear();
        }
        dfs(state, bestNeighbor, visited, noVisit);
    }

    /** See {@code SewerDriver} for specification. */
    @Override
    public void seek(SeekState state) {
        // TODO : Look for the ring and return.
        // DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
        // Instead, write your method (it may be recursive) elsewhere, with a
        // good specification, and call it from this one.
        //
        // Working this way provides you with flexibility. For example, write
        // one basic method, which always works. Then, make a method that is a
        // copy of the first one and try to optimize in that second one.
        // If you don't succeed, you can always use the first one.
        //
        // Use this same process on the second method, scram.
        HashMap<Long, Boolean> visited = new HashMap<>();
        HashMap<Long, Boolean> noVisits = new HashMap<>();
        dfs(state, state.currentLocation(), visited, noVisits);
    }

    @Override
    public void scram(ScramState state) {
        // TODO: Get out of the sewer system before the steps are used up.
//        // DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
//        // with a good specification, and call it from this one.
//        Node n = state.currentNode();
//        LinkedList visited = new LinkedList();
//        ShortestPaths path;
//        dfs2(state, n, visited, path);


        Maze m = new Maze((Set<Node>) state.allNodes());
        Node nextBCoin = bestCoin(state);
        ShortestPaths<Node, Edge> s = new ShortestPaths<>(m);
        s.singleSourceDistances(state.currentNode());
        List<Edge> bP = s.bestPath(state.exit());
        if (nextBCoin == null) {
            for (Edge e : bP) {
                state.moveTo(e.destination());
            }
        }
    }

    private Node bestCoin(ScramState state) {
        HashMap<Long, Boolean> visited = new HashMap<>();
        Map<Double, Node> coinPosVals = new HashMap<>();
        Maze m = new Maze((Set<Node>) state.allNodes());
        ShortestPaths<Node, Edge> coinExit = new ShortestPaths<>(m);
        ShortestPaths<Node, Edge> actualExit = new ShortestPaths<>(m);
        actualExit.singleSourceDistances(state.currentNode());


        for (Node t : state.allNodes()) {
            if (!visited.contains(t) && t.getTile().originalCoinValue() > 0) {
                coinExit.singleSourceDistances(t);
                if (actualExit.getDistance(t) + coinExit.getDistance(state.exit())
                        < state.stepsToGo()) {
                    double ratio = t.getTile().originalCoinValue() / actualExit.getDistance(t);
                    coinPosVals.put(ratio, t);
                }
            }
        }


        double bestVal = 0;
        for (double val : coinPosVals.keySet()) {
            if (val > bestVal && val < 10000) {
                bestVal = val;
            }
        }


        if (bestVal == 0) {
            return null;
        }
        visited.add(coinPosVals.get(bestVal));
        return coinPosVals.get(bestVal);
    }

}
