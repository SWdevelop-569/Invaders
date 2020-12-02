package engine;

public class MultiGameState{
    /** Current game level. */
    private int level;
    /** Current score. */
    private int score1;
    private int score2;
    /** Lives currently remaining. */
    private int livesRemaining1;
    private int livesRemaining2;
    /** Bullets shot until now. */
    private int bulletsShot1;
    private int bulletsShot2;

    /** Ships destroyed until now. */
    private int shipsDestroyed;

    /**
     * Constructor.
     *
     * @param level
     *            Current game level.
     * @param score1,2
     *            Current score.
     * @param livesRemaining1,2
     *            Lives currently remaining.
     * @param bulletsShot
     *            Bullets shot until now.
     * @param shipsDestroyed
     *            Ships destroyed until now.
     */

    public MultiGameState(final int level, final int score1, final int score2,
                     final int livesRemaining1, final int livesRemaining2,
                     final int bulletsShot, final int bulletsShot2,
                          final int shipsDestroyed) {
        this.level = level;
        this.score1 = score1;
        this.score2 = score2;
        this.livesRemaining1 = livesRemaining1;
        this.livesRemaining2 = livesRemaining2;
        this.bulletsShot1 = bulletsShot;
        this.bulletsShot2 = bulletsShot2;
        this.shipsDestroyed = shipsDestroyed;
    }
    /**
     * @return the level
     */
    public final int getLevel() {
        return level;
    }

    /**
     * @return the score
     */
    public final int[] getScore() {
        return new int[]{score1,score2};
    }

    /**
     * @return the livesRemaining
     */
    public final int[] getLivesRemaining() {
        return new int[]{livesRemaining1,livesRemaining2};
    }

    /**
     * @return the bulletsShot
     */
    public final int[] getBulletsShot() {
        return new int[]{bulletsShot1,bulletsShot2};
    }

    /**
     * @return the shipsDestroyed
     */
    public final int getShipsDestroyed() {
        return shipsDestroyed;
    }
}
