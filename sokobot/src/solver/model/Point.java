package solver.model;

/**
 * Represents a 2D coordinate on the game board. This class uses row and
 * column integers to define a location. Objects of this class are immutable.
 */
public final class Point {
    /**
     * The row coordinate of the point.
     */
    public final int r;

    /**
     * The column coordinate of the point.
     */
    public final int c;

    /**
     * Constructs a new Point with specified coordinates.
     *
     * @param r The row coordinate.
     * @param c The column coordinate.
     */
    public Point(int r, int c) {
        this.r = r;
        this.c = c;
    }

    /**
     * Calculates the coordinates of a neighboring point. The neighbor is one
     * step away in the given direction.
     *
     * @param dir The direction to the neighbor.
     * @return A new Point object for the neighbor's location.
     */
    public Point getNeighbor(Direction dir) {
        return new Point(this.r + dir.dr, this.c + dir.dc);
    }

    /**
     * Compares this point to another object for equality. Two points are
     * equal if they have the same row and column values.
     *
     * @param obj The object to compare with this point.
     * @return Returns true if the points are equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Point other = (Point) obj;
        return r == other.r && c == other.c;
    }

    /**
     * Generates a hash code for this point. The hash code is based on the row
     * and column coordinates. It is consistent with the equals method.
     *
     * @return An integer hash code for the point.
     */
    @Override
    public int hashCode() {
        final int HASH_PRIME = 31;
        return HASH_PRIME * r + c;
    }

    /**
     * Returns a string representation of the point. The format is
     * "Point(r, c)", which is useful for debugging.
     *
     * @return A string showing the point's coordinates.
     */
    @Override
    public String toString() {
        return "Point(" + r + ", " + c + ")";
    }
}
