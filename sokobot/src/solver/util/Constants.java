package solver.util;

/**
 * Provides compile-time constants for the solver application. This class
 * centralizes all constant values and characters for the game logic. It is a
 * utility class and cannot be instantiated.
 */
public final class Constants {

    /**
     * Prevents instantiation of this utility class.
     */
    private Constants() {
    }

    /**
     * Represents a wall character on the game board.
     */
    public static final char WALL = '#';

    /**
     * Represents a goal square character on the game board.
     */
    public static final char GOAL = '.';

    /**
     * Represents the player character on the game board.
     */
    public static final char PLAYER = '@';

    /**
     * Represents a crate character on the game board.
     */
    public static final char CRATE = '$';

    /**
     * Defines the index for player-related values in Zobrist hashing arrays.
     * This index points to the random numbers for the player's position.
     */
    public static final int PLAYER_ZOBRIST_INDEX = 0;

    /**
     * Defines the index for crate-related values in Zobrist hashing arrays.
     * This index points to the random numbers for all crate positions.
     */
    public static final int CRATE_ZOBRIST_INDEX = 1;

    /**
     * Specifies the seed for the random number generator in Zobrist hashing.
     * A fixed seed creates deterministic hash values across program runs.
     */
    public static final long ZOBRIST_RNG_SEED = 12345L;
}
