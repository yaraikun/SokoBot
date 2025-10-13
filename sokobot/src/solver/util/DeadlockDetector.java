package solver.util;

import java.util.Set;
import solver.model.Direction;
import solver.model.GameBoard;
import solver.model.GameState;
import solver.model.Point;

/**
 * Identifies deadlock states in the game. A deadlock is a position from which
 * the puzzle cannot be solved. This class contains several checks for common
 * deadlock patterns.
 */
public class DeadlockDetector {

    /**
     * Checks a game state for any deadlock conditions. It iterates through
     * each crate and applies several deadlock tests. These tests include
     * simple corners, frozen crates, and 2x2 blocks.
     *
     * @param state The current game state, including player and crate
     *              positions.
     * @param board The static game board layout.
     * @return Returns true if a deadlock is found, otherwise returns false.
     */
    public boolean isDeadlock(GameState state, GameBoard board) {
        for (Point crate : state.crates) {
            if (board.isDeadSquare(crate) ||
                    isSimpleCorner(crate, board) ||
                    isFrozenCrate(crate, state.crates, board) ||
                    is2x2Block(crate, state.crates, board)) {
                return true;
            }
        }
        return isRoomDeadlock(state, board);
    }

    /**
     * Determines if a crate is in a simple corner deadlock. A crate in a
     * corner is deadlocked if it is not a goal square. A corner is a non-goal
     * square adjacent to two perpendicular walls.
     *
     * @param crate The position of the crate to check.
     * @param board The static game board layout.
     * @return Returns true if the crate is in a corner deadlock.
     */
    private boolean isSimpleCorner(Point crate, GameBoard board) {
        if (board.getTile(crate) == Constants.GOAL)
            return false;

        boolean upWall = board.getTile(crate.getNeighbor(Direction.UP))
                == Constants.WALL;
        boolean downWall = board.getTile(crate.getNeighbor(Direction.DOWN))
                == Constants.WALL;
        boolean leftWall = board.getTile(crate.getNeighbor(Direction.LEFT))
                == Constants.WALL;
        boolean rightWall = board.getTile(crate.getNeighbor(Direction.RIGHT))
                == Constants.WALL;

        return (upWall || downWall) && (leftWall || rightWall);
    }

    /**
     * Checks if a crate is frozen against a wall. A crate is frozen if it is
     * next to a wall and cannot move parallel to it. The parallel movement is
     * blocked by another wall or another crate on each side.
     *
     * @param crate     The position of the crate to check.
     * @param allCrates A set containing the positions of all crates.
     * @param board     The static game board layout.
     * @return Returns true if the crate is frozen, otherwise false.
     */
    private boolean isFrozenCrate(Point crate, Set<Point> allCrates,
            GameBoard board) {
        if (board.getTile(crate) == Constants.GOAL)
            return false;

        boolean upWall = board.getTile(crate.getNeighbor(Direction.UP))
                == Constants.WALL;
        boolean downWall = board.getTile(crate.getNeighbor(Direction.DOWN))
                == Constants.WALL;
        boolean leftWall = board.getTile(crate.getNeighbor(Direction.LEFT))
                == Constants.WALL;
        boolean rightWall = board.getTile(crate.getNeighbor(Direction.RIGHT))
                == Constants.WALL;

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

    /**
     * Detects a 2x2 block of crates. This formation is a deadlock if any of
     * the four squares are not goal squares. The check originates from the
     * top-left crate of the potential block.
     *
     * @param crate     The position of the crate, treated as the top-left of a
     *                  2x2 square.
     * @param allCrates A set containing the positions of all crates.
     * @param board     The static game board layout.
     * @return Returns true if the crate is part of a 2x2 deadlock.
     */
    private boolean is2x2Block(Point crate, Set<Point> allCrates,
            GameBoard board) {
        Point[] neighbors = {
                crate.getNeighbor(Direction.RIGHT),
                crate.getNeighbor(Direction.DOWN),
                crate.getNeighbor(Direction.DOWN).getNeighbor(Direction.RIGHT)
        };

        if (allCrates.contains(neighbors[0]) &&
                allCrates.contains(neighbors[1]) &&
                allCrates.contains(neighbors[2])) {
            return board.getTile(crate) != Constants.GOAL ||
                    board.getTile(neighbors[0]) != Constants.GOAL ||
                    board.getTile(neighbors[1]) != Constants.GOAL ||
                    board.getTile(neighbors[2]) != Constants.GOAL;
        }
        return false;
    }

    /**
     * Checks for deadlocks within predefined rooms. A room deadlock happens
     * when a room has more crates than goal squares. This makes it impossible
     * to place all crates in that room on a goal.
     *
     * @param state The current game state containing all crate positions.
     * @param board The static game board, which defines the rooms.
     * @return Returns true if any room contains too many crates.
     */
    private boolean isRoomDeadlock(GameState state, GameBoard board) {
        if (board.getRoomCount() == 0)
            return false;

        int[] crateCounts = new int[board.getRoomCount()];
        for (Point crate : state.crates) {
            int roomId = board.getRoomId(crate);
            if (roomId != -1) {
                crateCounts[roomId]++;
            }
        }

        for (int i = 0; i < crateCounts.length; i++) {
            if (crateCounts[i] > board.getGoalCountForRoom(i)) {
                return true;
            }
        }
        return false;
    }
}
