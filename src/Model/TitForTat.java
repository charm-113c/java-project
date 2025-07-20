package Model;

import java.util.Random;

/*
 * TitForTat is a player that starts cooperative,
 * and then copies what others have done in their previous move.
 * E.g.: if in last tile TitForTat chose SHARE but someone else TAKE,
 * for the next tile TitForTat will choose TAKE. If everyone chose
 * SHARE, TitForTat will SHARE for the next tile.
 */
public class TitForTat extends Player {
  private Decision oppPrevChoice;

  private TitForTat(World world, Coordinate coordinates) {
    super(world, "TitForTat", 0, 0, coordinates);
    oppPrevChoice = Decision.SHARE;
  }

  public static TitForTat newTitForTat(World world) {
    Coordinate coords = Player.findFreeCoords(world, new Random());
    return new TitForTat(world, coords);
  }

  @Override
  public Decision contend() {
    nTakes = oppPrevChoice == Decision.TAKE ? ++nTakes : nTakes;
    return oppPrevChoice;
  }

  @Override
  protected void adjustStrategy(int available, int prevScore) {
    if (super.score - prevScore > 0)
      // All previous players decided to share
      oppPrevChoice = Decision.SHARE;
    else
      // At least one player took
      oppPrevChoice = Decision.TAKE;
  }
}
