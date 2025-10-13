package solver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import solver.util.Constants;

/**
 * Represents the static elements of the game board. This class stores the
 * layout of walls, floors, and goal squares. It also precomputes data for
 * deadlock detection, such as dead squares and rooms. The board's state does
 * not change during gameplay.
 */
public class GameBoard {

    /**
     * The width of the game board in tiles.
     */
    private final int width;

    /**
     * The height of the game board in tiles.
     */
    private final int height;

    /**
     * A 2D array representing the physical layout of the map.
     */
    private final char[][] mapData;

    /**
     * A list of all goal square locations.
     */
    private final List<Point> goals;

    /**
     * A 2D boolean array marking squares that are static deadlocks. A crate
     * on a dead square can never reach a goal.
     */
    private final boolean[][] deadSquares;

    /**
     * A 2D array that maps each floor tile to a specific room ID. Walls have
     * an ID of -1.
     */
    private final int[][] roomIds;

    /**
     * Stores the number of goal squares for each room. The list index
     * corresponds to the room ID.
     */
    private final List<Integer> goalCountsByRoomId;

    /**
     * Constructs a new GameBoard from map data. The constructor initializes
     * the board's dimensions and layout. It then precomputes goal locations,
     * dead squares, and room layouts for later use.
     *
     * @param width   The width of the game board.
     * @param height  The height of the game board.
     * @param mapData The 2D character array defining the board layout.
     */
    public GameBoard(int width, int height, char[][] mapData) {
        this.width = width;
        this.height = height;
        this.mapData = mapData;
        this.goals = findGoals();
        this.deadSquares = precomputeStaticDeadlocks();

        this.roomIds = new int[height][width];
        this.goalCountsByRoomId = new ArrayList<>();
        precomputeRooms();
    }

    /**
     * Returns the width of the game board.
     *
     * @return The board's width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the game board.
     *
     * @return The board's height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the character tile at a specific point on the board.
     *
     * @param p The point to check.
     * @return The character representing the tile at that point.
     */
    public char getTile(Point p) {
        return mapData[p.r][p.c];
    }

    /**
     * Returns an unmodifiable list of all goal locations.
     *
     * @return A list of points representing goal squares.
     */
    public List<Point> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    /**
     * Checks if a specific square is a precomputed dead square.
     *
     * @param p The point to check.
     * @return True if the square is a dead square, otherwise false.
     */
    public boolean isDeadSquare(Point p) {
        return deadSquares[p.r][p.c];
    }

    /**
     * Gets the room ID for a given point on the board.
     *
     * @param p The point to check.
     * @return The integer ID of the room at that point.
     */
    public int getRoomId(Point p) {
        return roomIds[p.r][p.c];
    }

    /**
     * Gets the number of goal squares within a specific room.
     *
     * @param roomId The ID of the room.
     * @return The total count of goals in that room.
     */
    public int getGoalCountForRoom(int roomId) {
        return goalCountsByRoomId.get(roomId);
    }

    /**
     * Returns the total number of distinct rooms on the board.
     *
     * @return The number of rooms.
     */
    public int getRoomCount() {
        return goalCountsByRoomId.size();
    }

    /**
     * Scans the map data to locate all goal squares.
     *
     * @return A list of points for each goal square.
     */
    private List<Point> findGoals() {
        List<Point> goalPoints = new ArrayList<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (mapData[r][c] == Constants.GOAL) {
                    goalPoints.add(new Point(r, c));
                }
            }
        }
        return goalPoints;
    }

    /**
     * Identifies all squares where a crate can never be pushed to a goal. It
     * uses a reverse search algorithm. The search starts from goal squares
     * and finds all reachable squares. Any non-wall square that is not
     * reachable is a dead square.
     *
     * @return A 2D boolean array where true indicates a dead square.
     */
    private boolean[][] precomputeStaticDeadlocks() {
        boolean[][] liveSquares = new boolean[height][width];
        LinkedList<Point> queue = new LinkedList<>(goals);
        for (Point goal : goals) {
            liveSquares[goal.r][goal.c] = true;
        }

        while (!queue.isEmpty()) {
            Point pullTarget = queue.poll();
            for (Direction dir : Direction.values()) {
                Point pullOrigin = pullTarget.getNeighbor(dir.opposite());
                Point playerPos = pullOrigin.getNeighbor(dir.opposite());

                if (isPullValid(pullOrigin, playerPos, liveSquares)) {
                    liveSquares[pullOrigin.r][pullOrigin.c] = true;
                    queue.add(pullOrigin);
                }
            }
        }

        return markDeadSquares(liveSquares);
    }

    /**
     * Checks if a crate can be pulled from a specific origin square. A pull
     * is valid if the origin and the required player position are within
     * bounds and are not walls. The origin square must also not be an already
     * identified live square.
     *
     * @param pullOrigin  The square the crate is pulled from.
     * @param playerPos   The square the player must stand on to pull.
     * @param liveSquares The current set of known live squares.
     * @return True if the pull is valid, otherwise false.
     */
    private boolean isPullValid(Point pullOrigin, Point playerPos,
            boolean[][] liveSquares) {
        if (isOutOfBounds(pullOrigin) || isOutOfBounds(playerPos)) {
            return false;
        }
        if (mapData[pullOrigin.r][pullOrigin.c] == Constants.WALL ||
                mapData[playerPos.r][playerPos.c] == Constants.WALL) {
            return false;
        }
        return !liveSquares[pullOrigin.r][pullOrigin.c];
    }

    /**
     * Creates the final dead squares map from the live squares map. This
     * method iterates through all squares. It marks any non-wall square that
     * is not in the live squares set as a dead square.
     *
     * @param liveSquares A 2D boolean array where true indicates a live
     *                    square.
     * @return A 2D boolean array where true indicates a dead square.
     */
    private boolean[][] markDeadSquares(boolean[][] liveSquares) {
        boolean[][] newDeadSquares = new boolean[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (mapData[r][c] != Constants.WALL && !liveSquares[r][c]) {
                    newDeadSquares[r][c] = true;
                }
            }
        }
        return newDeadSquares;
    }

    /**
     * Identifies all separate rooms on the board. It iterates through every
     * tile. If an unassigned tile is found, it starts a flood fill to map
     * that room and count its goals.
     */
    private void precomputeRooms() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                roomIds[r][c] = -1;
            }
        }

        int currentRoomId = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (mapData[r][c] != Constants.WALL && roomIds[r][c] == -1) {
                    int goalCount = floodFill(new Point(r, c), currentRoomId);
                    goalCountsByRoomId.add(goalCount);
                    currentRoomId++;
                }
            }
        }
    }

    /**
     * Uses a flood-fill algorithm to map a single room. It starts from a
     * point and expands to all reachable neighbors that are not walls. Each
     * visited tile is assigned the given room ID. The method also counts the
     * number of goal squares in the room.
     *
     * @param start  The starting point for the flood fill.
     * @param roomId The ID to assign to the new room.
     * @return The number of goal squares found in the room.
     */
    private int floodFill(Point start, int roomId) {
        int goalCount = 0;
        LinkedList<Point> queue = new LinkedList<>();
        queue.add(start);
        roomIds[start.r][start.c] = roomId;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (mapData[current.r][current.c] == Constants.GOAL) {
                goalCount++;
            }

            for (Direction dir : Direction.values()) {
                Point neighbor = current.getNeighbor(dir);
                if (!isOutOfBounds(neighbor) &&
                        mapData[neighbor.r][neighbor.c] != Constants.WALL &&
                        roomIds[neighbor.r][neighbor.c] == -1) {
                    roomIds[neighbor.r][neighbor.c] = roomId;
                    queue.add(neighbor);
                }
            }
        }
        return goalCount;
    }

    /**
     * Checks if a point is outside the boundaries of the game board.
     *
     * @param p The point to check.
     * @return True if the point is out of bounds, otherwise false.
     */
    private boolean isOutOfBounds(Point p) {
        return p.r < 0 || p.r >= height || p.c < 0 || p.c >= width;
    }
}
