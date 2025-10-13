package solver.util;

import java.util.Random;
import solver.model.Point;

/**
 * Implements Zobrist hashing for game states. This class creates a table of
 * random numbers. These numbers represent game pieces at each board position.
 * The hash of a game state is the XOR sum of the numbers for each piece.
 */
public class ZobristHash {

    /**
     * Stores the random numbers for hashing. The dimensions are height,
     * width, and piece type. The piece type index corresponds to constants
     * for player and crate.
     */
    private final long[][][] table;

    /**
     * The random number generator for the hash table. It uses a fixed seed to
     * produce the same random numbers for each program run.
     */
    private final Random rng = new Random(Constants.ZOBRIST_RNG_SEED);

    /**
     * Constructs a ZobristHash object for a specific board size. It
     * initializes a table with random 64-bit numbers. Each position on the
     * board gets a unique random number for each type of piece.
     *
     * @param height The height of the game board.
     * @param width  The width of the game board.
     */
    public ZobristHash(int height, int width) {
        table = new long[height][width][2];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                table[r][c][Constants.PLAYER_ZOBRIST_INDEX] = rng.nextLong();
                table[r][c][Constants.CRATE_ZOBRIST_INDEX] = rng.nextLong();
            }
        }
    }

    /**
     * Updates a hash value by applying a piece at a specific position. This
     * operation uses the XOR operator. Applying the same operation again will
     * reverse the change. This is useful for calculating new hashes after a
     * move.
     *
     * @param currentHash The hash value before the change.
     * @param p           The position of the piece on the board.
     * @param typeIndex   The type of piece, either player or crate.
     * @return The new hash value after the update.
     */
    public long toggle(long currentHash, Point p, int typeIndex) {
        return currentHash ^ table[p.r][p.c][typeIndex];
    }
}
