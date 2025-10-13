package solver.util;

import java.util.LinkedList;
import java.util.List;
import solver.model.GameBoard;
import solver.model.GameState;
import solver.model.Point;

/**
 * Calculates a heuristic value for a given game state. This value estimates
 * the minimum cost to move all crates to goal squares. The calculation uses
 * the sum of Manhattan distances between matched crates and goals.
 */
public class Heuristic {

    /**
     * Computes the heuristic value for the current game state. The method
     * pairs each crate with the closest available goal square. It then sums
     * the Manhattan distances of these pairs. This sum serves as an estimate
     * of the remaining cost to solve the puzzle.
     *
     * @param state The game state to evaluate. It contains the current crate
     *              positions.
     * @param board The game board. It provides the locations of all goal
     *              squares.
     * @return An integer representing the total heuristic cost.
     */
    public int calculate(GameState state, GameBoard board) {
        int totalValue = 0;
        List<Point> unassignedCrates = new LinkedList<>(state.crates);
        List<Point> unassignedGoals = new LinkedList<>(board.getGoals());

        while (!unassignedCrates.isEmpty()) {
            int minDistance = Integer.MAX_VALUE;
            Point bestCrate = null;
            Point bestGoal = null;

            for (Point crate : unassignedCrates) {
                for (Point goal : unassignedGoals) {
                    int dist = manhattanDistance(crate, goal);
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestCrate = crate;
                        bestGoal = goal;
                    }
                }
            }

            if (bestCrate != null) {
                totalValue += minDistance;
                unassignedCrates.remove(bestCrate);
                unassignedGoals.remove(bestGoal);
            } else {
                break;
            }
        }
        return totalValue;
    }

    /**
     * Calculates the Manhattan distance between two points. The distance is
     * the sum of the absolute differences of their coordinates. It represents
     * the minimum number of moves on a grid.
     *
     * @param a The first point.
     * @param b The second point.
     * @return The Manhattan distance between the two points.
     */
    private int manhattanDistance(Point a, Point b) {
        return Math.abs(a.r - b.r) + Math.abs(a.c - b.c);
    }
}
