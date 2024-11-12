package diver;

import game.*;
import graph.ShortestPaths;

import java.util.*;

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

    public void seek(SeekState state) {
        HashMap<Long, Boolean> visited = new HashMap<>();
        HashMap<Long, Boolean> noVisits = new HashMap<>();
        dfs(state, state.currentLocation(), visited, noVisits);
    }

    public void scram(ScramState state) {
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
