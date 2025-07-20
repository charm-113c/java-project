package Model;

import java.util.Random;

/*
 * Joss is a player that starts cooperative, then every 4 contentions
 * sneakily tries to TAKE.
 * Otherwise, it copies what others have done in their previous move.
 */
public class Joss extends Player {
  private Decision oppPrevChoice;

  private Joss(World world, Coordinate coordinates) {
    super(world, "Joss", 0, 0, coordinates);
    oppPrevChoice = Decision.SHARE;
  }

  public static Joss newJoss(World world) {
    Coordinate coords = Player.findFreeCoords(world, new Random());
    return new Joss(world, coords);
  }

  @Override
  public Decision contend() {
    nTakes = oppPrevChoice == Decision.TAKE ? ++nTakes : nTakes;
    return oppPrevChoice;
  }

  @Override
  protected void adjustStrategy(int available, int prevScore) {
    if (nContentions % 4 == 0) {
      oppPrevChoice = Decision.TAKE;
    } else if (super.score - prevScore > 0) {
      // All previous players decided to share
      oppPrevChoice = Decision.SHARE;
    } else
      // At least one player took
      oppPrevChoice = Decision.TAKE;
  }
}
