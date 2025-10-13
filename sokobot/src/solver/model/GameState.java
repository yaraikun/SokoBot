package solver.model;

import java.util.Set;

/**
 * Represents a specific state of the game at a single point in time. This
 * class is a snapshot of all dynamic game elements. These elements include
 * the player's position and the positions of all crates. It also stores the
 * path taken to reach this state and a Zobrist hash for quick comparisons.
 * This class is final and its core fields are immutable.
 */
public final class GameState {

    /**
     * The current position of the player on the game board.
     */
    public final Point player;

    /**
     * A set containing the positions of all crates on the game board.
     */
    public final Set<Point> crates;

    /**
     * A string representing the sequence of moves to reach this state.
     */
    public final String path;

    /**
     * The Zobrist hash value for this specific game state. This hash is used
     * for fast equality checks and for storage in hash-based collections.
     */
    public final long zobristHash;

    /**
     * The heuristic value of this state. This value estimates the cost to
     * reach a solution from the current state. It is used by search
     * algorithms to prioritize states.
     */
    public int heuristic;

    /**
     * Constructs a new GameState. It captures the complete arrangement of
     * movable pieces on the board at a moment in time.
     *
     * @param player      The position of the player.
     * @param crates      The set of all crate positions.
     * @param path        The move sequence that led to this state.
     * @param zobristHash The Zobrist hash calculated for this state.
     */
    public GameState(Point player, Set<Point> crates, String path,
            long zobristHash) {
        this.player = player;
        this.crates = crates;
        this.path = path;
        this.zobristHash = zobristHash;
    }

    /**
     * Compares this game state to another object for equality. Two game
     * states are equal if they have the same Zobrist hash, the same player
     * position, and the same set of crate positions. The hash check provides
     * a fast path for inequality.
     *
     * @param obj The object to compare with this game state.
     * @return Returns true if the objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        GameState other = (GameState) obj;

        return zobristHash == other.zobristHash &&
                player.equals(other.player) &&
                crates.equals(other.crates);
    }

    /**
     * Returns a hash code for this game state. The hash code is derived from
     * the 64-bit Zobrist hash value. This is consistent with the equals
     * method.
     *
     * @return An integer hash code for the game state.
     */
    @Override
    public int hashCode() {
        return (int) (zobristHash ^ (zobristHash >>> 32));
    }
}
