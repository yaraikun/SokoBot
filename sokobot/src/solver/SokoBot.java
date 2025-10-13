package solver;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;
import solver.model.*;
import solver.util.*;

/**
 * Solves Sokoban puzzles using an A* search algorithm. This class manages the
 * search process. It explores game states, from an initial state to a goal
 * state. The goal is to find the shortest path of moves.
 */
public class SokoBot {

    /**
     * The static game board, containing walls and goal locations.
     */
    private GameBoard board;

    /**
     * The Zobrist hashing utility for game states.
     */
    private ZobristHash zobrist;

    /**
     * The heuristic calculator for estimating costs.
     */
    private Heuristic heuristic;

    /**
     * The detector for identifying unsolvable deadlock states.
     */
    private DeadlockDetector deadlockDetector;

    /**
     * Finds a solution to a given Sokoban puzzle. This method sets up the
     * initial game state. It then uses a priority queue to explore possible
     * moves. The search prioritizes states with a lower estimated total cost.
     *
     * @param width     The width of the puzzle board.
     * @param height    The height of the puzzle board.
     * @param mapData   A 2D array defining the static board layout.
     * @param itemsData A 2D array showing the initial positions of the player
     *                  and crates.
     * @return A string of characters representing the solution path. Returns
     *         null if no solution is found.
     */
    public String solveSokobanPuzzle(
            int width, int height, char[][] mapData, char[][] itemsData) {

        initializeServices(width, height, mapData);
        GameState initialState = createInitialState(itemsData);

        Comparator<GameState> heuristicComparator =
                Comparator.comparingInt(s -> s.heuristic);
        PriorityQueue<GameState> openList =
                new PriorityQueue<>(heuristicComparator);
        Set<Long> closedList = new HashSet<>();

        openList.add(initialState);

        while (!openList.isEmpty()) {
            GameState currentState = openList.poll();

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

    /**
     * Initializes the helper services for the solver. This includes the game
     * board, Zobrist hashing, heuristic calculator, and deadlock detector.
     *
     * @param width   The width of the game board.
     * @param height  The height of the game board.
     * @param mapData The 2D array of the static board layout.
     */
    private void initializeServices(int width, int height, char[][] mapData) {
        this.board = new GameBoard(width, height, mapData);
        this.zobrist = new ZobristHash(height, width);
        this.heuristic = new Heuristic();
        this.deadlockDetector = new DeadlockDetector();
    }

    /**
     * Creates the initial game state from the puzzle input. It identifies the
     * starting positions of the player and all crates. It also calculates the
     * initial Zobrist hash and heuristic value.
     *
     * @param itemsData A 2D array with the starting item positions.
     * @return The fully configured initial GameState object.
     */
    private GameState createInitialState(char[][] itemsData) {
        Point player = null;
        Set<Point> crates = new HashSet<>();
        long currentHash = 0L;

        for (int r = 0; r < board.getHeight(); r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                if (itemsData[r][c] == Constants.PLAYER) {
                    player = new Point(r, c);
                    currentHash = zobrist.toggle(currentHash, player,
                            Constants.PLAYER_ZOBRIST_INDEX);
                } else if (itemsData[r][c] == Constants.CRATE) {
                    Point crate = new Point(r, c);
                    crates.add(crate);
                    currentHash = zobrist.toggle(currentHash, crate,
                            Constants.CRATE_ZOBRIST_INDEX);
                }
            }
        }

        GameState initialState = new GameState(player, crates, "",
                currentHash);
        initialState.heuristic = heuristic.calculate(initialState, board);
        return initialState;
    }

    /**
     * Generates all valid successor states from a given state. It tries to
     * move the player in all four directions. Each valid new state is added
     * to the open list for future exploration.
     *
     * @param currentState The state to expand.
     * @param openList     The priority queue of states to visit.
     * @param closedList   A set of hashes of already visited states.
     */
    private void expandState(GameState currentState,
            PriorityQueue<GameState> openList, Set<Long> closedList) {
        for (Direction direction : Direction.values()) {
            GameState nextState = tryMove(currentState, direction);
            if (isStateViable(nextState, closedList)) {
                openList.add(nextState);
            }
        }
    }

    /**
     * Checks if a game state is viable for exploration. A state is viable if
     * it is not null, has not been visited before, and is not in a deadlock.
     *
     * @param state      The game state to check.
     * @param closedList A set of hashes of already visited states.
     * @return True if the state is viable, otherwise false.
     */
    private boolean isStateViable(GameState state, Set<Long> closedList) {
        if (state == null || closedList.contains(state.zobristHash)) {
            return false;
        }
        return !deadlockDetector.isDeadlock(state, board);
    }

    /**
     * Attempts to move the player in a specified direction. The move can be a
     * simple step or a crate push.
     *
     * @param currentState The current state before the move.
     * @param direction    The direction of the attempted move.
     * @return A new GameState if the move is valid, otherwise null.
     */
    private GameState tryMove(GameState currentState, Direction direction) {
        Point playerPos = currentState.player;
        Point newPlayerPos = playerPos.getNeighbor(direction);

        if (board.getTile(newPlayerPos) == Constants.WALL) {
            return null;
        }

        if (currentState.crates.contains(newPlayerPos)) {
            return handleCratePush(currentState, newPlayerPos, direction);
        } else {
            return handlePlayerMove(currentState, newPlayerPos, direction);
        }
    }

    /**
     * Creates a new game state after a simple player move. This method
     * updates the player's position and the Zobrist hash. The heuristic value
     * remains the same.
     *
     * @param currentState The state before the move.
     * @param newPlayerPos The player's new position.
     * @param direction    The direction of the move.
     * @return The new GameState after the move.
     */
    private GameState handlePlayerMove(
            GameState currentState, Point newPlayerPos, Direction direction) {

        long newHash = currentState.zobristHash;
        newHash = zobrist.toggle(newHash, currentState.player,
                Constants.PLAYER_ZOBRIST_INDEX);
        newHash = zobrist.toggle(newHash, newPlayerPos,
                Constants.PLAYER_ZOBRIST_INDEX);

        GameState newState = new GameState(newPlayerPos, currentState.crates,
                currentState.path + direction.symbol, newHash);

        newState.heuristic = currentState.heuristic;
        return newState;
    }

    /**
     * Creates a new game state after a crate push. This method checks if the
     * push is valid. If it is, it updates the positions of the player and the
     * crate. It then recalculates the Zobrist hash and heuristic value.
     *
     * @param currentState The state before the push.
     * @param cratePos     The current position of the crate to be pushed.
     * @param direction    The direction of the push.
     * @return The new GameState if the push is valid, otherwise null.
     */
    private GameState handleCratePush(
            GameState currentState, Point cratePos, Direction direction) {
        Point newCratePos = cratePos.getNeighbor(direction);

        if (board.getTile(newCratePos) == Constants.WALL ||
                currentState.crates.contains(newCratePos)) {
            return null;
        }

        Set<Point> newCrates = new HashSet<>(currentState.crates);
        newCrates.remove(cratePos);
        newCrates.add(newCratePos);

        Point newPlayerPos = cratePos;

        long newHash = currentState.zobristHash;
        newHash = zobrist.toggle(newHash, currentState.player,
                Constants.PLAYER_ZOBRIST_INDEX);
        newHash = zobrist.toggle(newHash, newPlayerPos,
                Constants.PLAYER_ZOBRIST_INDEX);
        newHash = zobrist.toggle(newHash, cratePos,
                Constants.CRATE_ZOBRIST_INDEX);
        newHash = zobrist.toggle(newHash, newCratePos,
                Constants.CRATE_ZOBRIST_INDEX);

        GameState newState = new GameState(newPlayerPos, newCrates,
                currentState.path + direction.symbol, newHash);

        newState.heuristic = heuristic.calculate(newState, board);
        return newState;
    }

    /**
     * Checks if a game state is a solution. A state is a solution if all
     * crates are on goal squares.
     *
     * @param state The game state to check.
     * @return True if the state is a goal state, otherwise false.
     */
    private boolean isGoalState(GameState state) {
        return board.getGoals().size() == state.crates.size() &&
                board.getGoals().containsAll(state.crates);
    }
}
