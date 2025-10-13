package solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class SokoBot {

    // --- Constants for Map Elements ---
    private static final char WALL = '#';
    private static final char GOAL = '.';
    private static final char PLAYER = '@';
    private static final char CRATE = '$';

    // --- Zobrist Hashing Constants ---
    private static final int PLAYER_ZOBRIST_INDEX = 0;
    private static final int CRATE_ZOBRIST_INDEX = 1;
    private static final long ZOBRIST_RNG_SEED = 12345L;

    // --- Solver State ---
    private int width;
    private int height;
    private char[][] mapData;
    private List<Point> goals;
    private boolean[][] deadSquares;
    private long[][][] zobristTable;
    private final Random zobristRng = new Random(ZOBRIST_RNG_SEED);

    // --- Advanced Deadlock Detection ---
    private int[][] roomIds;
    private List<Integer> goalCountsByRoomId;

    public String solveSokobanPuzzle(
            int width, int height, char[][] mapData, char[][] itemsData) {
        initializeSolver(width, height, mapData);
        State initialState = createInitialState(itemsData);

        Comparator<State> heuristicComparator = Comparator.comparingInt(s -> s.heuristic);
        PriorityQueue<State> openList = new PriorityQueue<>(heuristicComparator);
        Set<Long> closedList = new HashSet<>();

        openList.add(initialState);

        while (!openList.isEmpty()) {
            State currentState = openList.poll();

            if (isGoalState(currentState)) {
                return currentState.path;
            }

            if (closedList.contains(currentState.zobristHash)) {
                continue;
            }
            closedList.add(currentState.zobristHash);

            expandState(currentState, openList, closedList);
        }

        return null;
    }

    private void initializeSolver(int width, int height, char[][] mapData) {
        this.width = width;
        this.height = height;
        this.mapData = mapData;
        this.goals = findGoals();
        initializeZobristTable();
        precomputeStaticDeadlocks();
        precomputeRooms();
    }

    private void initializeZobristTable() {
        zobristTable = new long[height][width][2];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                zobristTable[r][c][PLAYER_ZOBRIST_INDEX] = zobristRng.nextLong();
                zobristTable[r][c][CRATE_ZOBRIST_INDEX] = zobristRng.nextLong();
            }
        }
    }

    private void expandState(State currentState,
            PriorityQueue<State> openList, Set<Long> closedList) {
        for (Direction direction : Direction.values()) {
            State nextState = tryMove(currentState, direction);
            if (isStateViable(nextState, closedList)) {
                openList.add(nextState);
            }
        }
    }

    private boolean isStateViable(State state, Set<Long> closedList) {
        if (state == null || closedList.contains(state.zobristHash)) {
            return false;
        }
        return !isDeadlock(state);
    }

    private State tryMove(State currentState, Direction direction) {
        Point playerPos = currentState.player;
        Point newPlayerPos = playerPos.getNeighbor(direction);

        if (mapData[newPlayerPos.r][newPlayerPos.c] == WALL) {
            return null;
        }

        boolean isCratePush = currentState.crates.contains(newPlayerPos);
        if (isCratePush) {
            return handleCratePush(currentState, newPlayerPos, direction);
        } else {
            return handlePlayerMove(currentState, newPlayerPos, direction);
        }
    }

    private State handlePlayerMove(
            State currentState, Point newPlayerPos, Direction direction) {
        String newPath = currentState.path + direction.symbol;

        long newHash = currentState.zobristHash;
        newHash = toggleZobristHash(newHash, currentState.player, PLAYER_ZOBRIST_INDEX);
        newHash = toggleZobristHash(newHash, newPlayerPos, PLAYER_ZOBRIST_INDEX);

        State newState = new State(newPlayerPos, currentState.crates, newPath, newHash);
        newState.heuristic = currentState.heuristic;
        return newState;
    }

    private State handleCratePush(
            State currentState, Point cratePos, Direction direction) {
        Point newCratePos = cratePos.getNeighbor(direction);

        if (isPushBlocked(newCratePos, currentState.crates)) {
            return null;
        }

        Set<Point> newCrates = new HashSet<>(currentState.crates);
        newCrates.remove(cratePos);
        newCrates.add(newCratePos);

        String newPath = currentState.path + direction.symbol;
        Point newPlayerPos = cratePos;

        long newHash = currentState.zobristHash;
        newHash = toggleZobristHash(newHash, currentState.player, PLAYER_ZOBRIST_INDEX);
        newHash = toggleZobristHash(newHash, newPlayerPos, PLAYER_ZOBRIST_INDEX);
        newHash = toggleZobristHash(newHash, cratePos, CRATE_ZOBRIST_INDEX);
        newHash = toggleZobristHash(newHash, newCratePos, CRATE_ZOBRIST_INDEX);

        State newState = new State(newPlayerPos, newCrates, newPath, newHash);
        newState.heuristic = calculateHeuristic(newState.crates);
        return newState;
    }

    private long toggleZobristHash(long currentHash, Point p, int typeIndex) {
        return currentHash ^ zobristTable[p.r][p.c][typeIndex];
    }

    private boolean isPushBlocked(Point newCratePos, Set<Point> crates) {
        return mapData[newCratePos.r][newCratePos.c] == WALL
                || crates.contains(newCratePos);
    }

    // --- Deadlock Detection ---

    private boolean isDeadlock(State state) {
        for (Point crate : state.crates) {
            if (isStaticallyDead(crate) ||
                    isCornerDeadlock(crate) ||
                    isFrozenCrateDeadlock(crate, state.crates) ||
                    is2x2BlockDeadlock(crate, state.crates)) {
                return true;
            }
        }
        return isRoomDeadlock(state);
    }

    private boolean isStaticallyDead(Point crate) {
        return deadSquares[crate.r][crate.c];
    }

    private boolean isCornerDeadlock(Point crate) {
        if (mapData[crate.r][crate.c] == GOAL)
            return false;

        boolean isUpWall = (mapData[crate.r - 1][crate.c] == WALL);
        boolean isDownWall = (mapData[crate.r + 1][crate.c] == WALL);
        boolean isLeftWall = (mapData[crate.r][crate.c - 1] == WALL);
        boolean isRightWall = (mapData[crate.r][crate.c + 1] == WALL);

        return (isUpWall || isDownWall) && (isLeftWall || isRightWall);
    }

    private boolean isFrozenCrateDeadlock(Point crate, Set<Point> allCrates) {
        if (mapData[crate.r][crate.c] == GOAL)
            return false;

        boolean upWall = (mapData[crate.r - 1][crate.c] == WALL);
        boolean downWall = (mapData[crate.r + 1][crate.c] == WALL);
        boolean leftWall = (mapData[crate.r][crate.c - 1] == WALL);
        boolean rightWall = (mapData[crate.r][crate.c + 1] == WALL);

        if ((upWall || downWall)) {
            boolean blockedLeft = leftWall || allCrates.contains(
                    crate.getNeighbor(Direction.LEFT));
            boolean blockedRight = rightWall || allCrates.contains(
                    crate.getNeighbor(Direction.RIGHT));
            if (blockedLeft && blockedRight)
                return true;
        }
        if ((leftWall || rightWall)) {
            boolean blockedUp = upWall || allCrates.contains(
                    crate.getNeighbor(Direction.UP));
            boolean blockedDown = downWall || allCrates.contains(
                    crate.getNeighbor(Direction.DOWN));
            if (blockedUp && blockedDown)
                return true;
        }
        return false;
    }

    private boolean is2x2BlockDeadlock(Point crate, Set<Point> allCrates) {
        Point rightCrate = crate.getNeighbor(Direction.RIGHT);
        Point downCrate = crate.getNeighbor(Direction.DOWN);
        Point diagCrate = downCrate.getNeighbor(Direction.RIGHT);

        if (allCrates.contains(rightCrate) &&
                allCrates.contains(downCrate) &&
                allCrates.contains(diagCrate)) {
            return mapData[crate.r][crate.c] != GOAL ||
                    mapData[rightCrate.r][rightCrate.c] != GOAL ||
                    mapData[downCrate.r][downCrate.c] != GOAL ||
                    mapData[diagCrate.r][diagCrate.c] != GOAL;
        }
        return false;
    }

    private boolean isRoomDeadlock(State state) {
        if (goalCountsByRoomId.isEmpty())
            return false;

        int[] crateCounts = new int[goalCountsByRoomId.size()];
        for (Point crate : state.crates) {
            int roomId = roomIds[crate.r][crate.c];
            if (roomId != -1) {
                crateCounts[roomId]++;
            }
        }

        for (int i = 0; i < crateCounts.length; i++) {
            if (crateCounts[i] > goalCountsByRoomId.get(i)) {
                return true;
            }
        }
        return false;
    }

    // --- Heuristic Calculation ---

    private int calculateHeuristic(Set<Point> crates) {
        int totalHeuristicValue = 0;
        List<Point> unassignedCrates = new LinkedList<>(crates);
        List<Point> unassignedGoals = new LinkedList<>(goals);

        while (!unassignedCrates.isEmpty()) {
            int minDistance = Integer.MAX_VALUE;
            Point bestCrate = null;
            Point bestGoal = null;

            for (Point crate : unassignedCrates) {
                for (Point goal : unassignedGoals) {
                    int distance = Math.abs(crate.r - goal.r) +
                            Math.abs(crate.c - goal.c);
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestCrate = crate;
                        bestGoal = goal;
                    }
                }
            }

            if (bestCrate != null) {
                totalHeuristicValue += minDistance;
                unassignedCrates.remove(bestCrate);
                unassignedGoals.remove(bestGoal);
            } else {
                break;
            }
        }
        return totalHeuristicValue;
    }

    // --- State and Map Initialization ---

    private boolean isGoalState(State state) {
        return goals.containsAll(state.crates);
    }

    private State createInitialState(char[][] itemsData) {
        Point player = null;
        Set<Point> crates = new HashSet<>();
        long currentHash = 0L;

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (itemsData[r][c] == PLAYER) {
                    player = new Point(r, c);
                    currentHash = toggleZobristHash(currentHash, player, PLAYER_ZOBRIST_INDEX);
                } else if (itemsData[r][c] == CRATE) {
                    Point crate = new Point(r, c);
                    crates.add(crate);
                    currentHash = toggleZobristHash(currentHash, crate, CRATE_ZOBRIST_INDEX);
                }
            }
        }
        State initialState = new State(player, crates, "", currentHash);
        initialState.heuristic = calculateHeuristic(initialState.crates);
        return initialState;
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

    // --- Static Pre-computation ---

    private void precomputeRooms() {
        roomIds = new int[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                roomIds[r][c] = -1;
            }
        }

        goalCountsByRoomId = new ArrayList<>();
        int currentRoomId = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                boolean isNewRoom = (mapData[r][c] != WALL && roomIds[r][c] == -1);
                if (isNewRoom) {
                    int goalCount = floodFillAndCountGoals(new Point(r, c), currentRoomId);
                    goalCountsByRoomId.add(goalCount);
                    currentRoomId++;
                }
            }
        }
    }

    private int floodFillAndCountGoals(Point startPoint, int roomId) {
        int goalCount = 0;
        LinkedList<Point> queue = new LinkedList<>();

        queue.add(startPoint);
        roomIds[startPoint.r][startPoint.c] = roomId;

        while (!queue.isEmpty()) {
            Point currentPoint = queue.poll();
            if (mapData[currentPoint.r][currentPoint.c] == GOAL) {
                goalCount++;
            }

            for (Direction dir : Direction.values()) {
                Point neighbor = currentPoint.getNeighbor(dir);
                if (!isOutOfBounds(neighbor) &&
                        mapData[neighbor.r][neighbor.c] != WALL &&
                        roomIds[neighbor.r][neighbor.c] == -1) {
                    roomIds[neighbor.r][neighbor.c] = roomId;
                    queue.add(neighbor);
                }
            }
        }
        return goalCount;
    }

    private void precomputeStaticDeadlocks() {
        boolean[][] liveSquares = new boolean[height][width];
        LinkedList<Point> queue = new LinkedList<>();

        for (Point goal : goals) {
            queue.add(goal);
            liveSquares[goal.r][goal.c] = true;
        }

        runReverseBfs(queue, liveSquares);
        markDeadSquares(liveSquares);
    }

    private void runReverseBfs(
            LinkedList<Point> queue, boolean[][] liveSquares) {
        while (!queue.isEmpty()) {
            Point pullTarget = queue.poll();
            for (Direction direction : Direction.values()) {
                Point pullOrigin = pullTarget.getNeighbor(direction.opposite());
                if (isPullInvalid(pullOrigin, pullTarget, liveSquares)) {
                    continue;
                }
                liveSquares[pullOrigin.r][pullOrigin.c] = true;
                queue.add(pullOrigin);
            }
        }
    }

    private boolean isPullInvalid(
            Point pullOrigin, Point pullTarget, boolean[][] liveSquares) {
        Point playerPos = pullOrigin.getNeighbor(
                Direction.fromPoints(pullOrigin, pullTarget).opposite());

        if (isOutOfBounds(playerPos) || isOutOfBounds(pullOrigin)) {
            return true;
        }
        if (mapData[pullOrigin.r][pullOrigin.c] == WALL ||
                mapData[playerPos.r][playerPos.c] == WALL) {
            return true;
        }
        return liveSquares[pullOrigin.r][pullOrigin.c];
    }

    private boolean isOutOfBounds(Point p) {
        return p.r < 0 || p.r >= height || p.c < 0 || p.c >= width;
    }

    private void markDeadSquares(boolean[][] liveSquares) {
        this.deadSquares = new boolean[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (mapData[r][c] != WALL && !liveSquares[r][c]) {
                    this.deadSquares[r][c] = true;
                }
            }
        }
    }

    // --- Private Nested Classes and Enums ---

    private enum Direction {
        UP('u', -1, 0), DOWN('d', 1, 0), LEFT('l', 0, -1), RIGHT('r', 0, 1);

        private final char symbol;
        private final int dr;
        private final int dc;

        Direction(char symbol, int dr, int dc) {
            this.symbol = symbol;
            this.dr = dr;
            this.dc = dc;
        }

        public Direction opposite() {
            switch (this) {
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                default:
                    throw new IllegalStateException("Unknown direction");
            }
        }

        public static Direction fromPoints(Point from, Point to) {
            if (to.r < from.r)
                return UP;
            if (to.r > from.r)
                return DOWN;
            if (to.c < from.c)
                return LEFT;
            if (to.c > from.c)
                return RIGHT;
            throw new IllegalArgumentException("Points must be neighbors");
        }
    }

    private static final class Point {
        final int r, c;
        private static final int HASH_PRIME = 31;

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
            return HASH_PRIME * r + c;
        }
    }

    private static final class State {
        final Point player;
        final Set<Point> crates;
        final String path;
        final long zobristHash;
        int heuristic;

        State(Point player, Set<Point> crates, String path, long zobristHash) {
            this.player = player;
            this.crates = crates;
            this.path = path;
            this.zobristHash = zobristHash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            State other = (State) obj;
            return zobristHash == other.zobristHash &&
                    player.equals(other.player) &&
                    crates.equals(other.crates);
        }

        @Override
        public int hashCode() {
            return (int) (zobristHash ^ (zobristHash >>> 32));
        }
    }
}
