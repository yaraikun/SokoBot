package solver.model;

/**
 * Represents the four cardinal directions. Each direction stores its symbol
 * and the corresponding change in row and column coordinates. This enum is
 * used for player movement and crate pushes.
 */
public enum Direction {
    /**
     * The upward direction, decreasing the row coordinate.
     */
    UP('u', -1, 0),

    /**
     * The downward direction, increasing the row coordinate.
     */
    DOWN('d', 1, 0),

    /**
     * The leftward direction, decreasing the column coordinate.
     */
    LEFT('l', 0, -1),

    /**
     * The rightward direction, increasing the column coordinate.
     */
    RIGHT('r', 0, 1);

    /**
     * A single character symbol for the direction. This is useful for
     * representing a sequence of moves in a compact string format.
     */
    public final char symbol;

    /**
     * The change in the row coordinate when moving in this direction.
     */
    public final int dr;

    /**
     * The change in the column coordinate when moving in this direction.
     */
    public final int dc;

    /**
     * Constructs a Direction enum constant.
     *
     * @param symbol The character symbol for the direction.
     * @param dr     The delta for the row.
     * @param dc     The delta for the column.
     */
    Direction(char symbol, int dr, int dc) {
        this.symbol = symbol;
        this.dr = dr;
        this.dc = dc;
    }

    /**
     * Returns the opposite direction. For example, the opposite of UP is
     * DOWN.
     *
     * @return The opposing Direction constant.
     */
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

    /**
     * Determines the direction of movement between two adjacent points. The
     * points must be direct neighbors, not diagonal.
     *
     * @param from The starting point.
     * @param to   The destination point.
     * @return The Direction from the start point to the destination point.
     * @throws IllegalArgumentException if the points are not neighbors.
     */
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
