package solver;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class SokoBot {
    private static final char WALL = '#';
    private static final char GOAL = '.';
    private static final char PLAYER = '@';
    private static final char CRATE = '$';

    private int width;
    private int height;
    private char[][] mapData;
    private List<Point> goals;

    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        this.width = width;
        this.height = height;
        this.mapData = mapData;
        this.goals = findGoals();

        State initialState = createInitialState(itemsData);

        Queue<State> openList = new LinkedList<>();
        Set<State> closedList = new HashSet<>();

        openList.add(initialState);
        closedList.add(initialState);

        while (!openList.isEmpty()) {
            State currentState = openList.poll();

            if (isGoalState(currentState)) {
                return currentState.path;
            }

            for (Direction direction : Direction.values()) {
                State nextState = tryMove(currentState, direction);

                if (nextState != null && !closedList.contains(nextState)) {
                    openList.add(nextState);
                    closedList.add(nextState);
                }
            }
        }

        return null;
    }

    private State tryMove(State currentState, Direction direction) {
        Point playerPos = currentState.player;
        Point newPlayerPos = playerPos.getNeighbor(direction);

        if (mapData[newPlayerPos.r][newPlayerPos.c] == WALL) {
            return null;
        }

        if (currentState.crates.contains(newPlayerPos)) {
            Point newCratePos = newPlayerPos.getNeighbor(direction);

            if (mapData[newCratePos.r][newCratePos.c] == WALL || currentState.crates.contains(newCratePos)) {
                return null;
            }

            Set<Point> newCrates = new HashSet<>(currentState.crates);
            newCrates.remove(newPlayerPos);
            newCrates.add(newCratePos);
            return new State(newPlayerPos, newCrates, currentState.path + direction.symbol);
        } else {
            return new State(newPlayerPos, currentState.crates, currentState.path + direction.symbol);
        }
    }

    private boolean isGoalState(State state) {
        return goals.containsAll(state.crates);
    }

    private State createInitialState(char[][] itemsData) {
        Point player = null;
        Set<Point> crates = new HashSet<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (itemsData[r][c] == PLAYER) {
                    player = new Point(r, c);
                } else if (itemsData[r][c] == CRATE) {
                    crates.add(new Point(r, c));
                }
            }
        }
        return new State(player, crates, "");
    }

    private List<Point> findGoals() {
        List<Point> goalPoints = new LinkedList<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (mapData[r][c] == GOAL) {
                    goalPoints.add(new Point(r, c));
                }
            }
        }
        return goalPoints;
    }

    private enum Direction {
        UP('u', -1, 0), DOWN('d', 1, 0), LEFT('l', 0, -1), RIGHT('r', 0, 1);

        final char symbol;
        final int dr;
        final int dc;

        Direction(char symbol, int dr, int dc) {
            this.symbol = symbol;
            this.dr = dr;
            this.dc = dc;
        }
    }

    private static final class Point {
        final int r, c;

        Point(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public Point getNeighbor(Direction dir) {
            return new Point(this.r + dir.dr, this.c + dir.dc);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Point other = (Point) obj;
            return r == other.r && c == other.c;
        }

        @Override
        public int hashCode() {
            return 31 * r + c;
        }
    }

    private static final class State {
        final Point player;
        final Set<Point> crates;
        final String path;

        State(Point player, Set<Point> crates, String path) {
            this.player = player;
            this.crates = crates;
            this.path = path;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            State other = (State) obj;
            return player.equals(other.player) && crates.equals(other.crates);
        }

        @Override
        public int hashCode() {
            return 31 * player.hashCode() + crates.hashCode();
        }
    }
}
