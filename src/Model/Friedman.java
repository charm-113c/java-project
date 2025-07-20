package Model;

import java.util.Random;

/*
 * Friedman is a player that starts cooperative,
 * but if in one contention it encounters a player
 * that TAKE, then from then on Friedman will always TAKE.
 */
public class Friedman extends Player {
  private Decision choice;

  private Friedman(World world, Coordinate coordinates) {
    super(world, "Friedman", 0, 0, coordinates);
    choice = Decision.SHARE;
  }

  public static Friedman newFriedman(World world) {
    Coordinate coords = Player.findFreeCoords(world, new Random());
    return new Friedman(world, coords);
  }

  @Override
  public Decision contend() {
    nTakes = choice == Decision.TAKE ? ++nTakes : nTakes;
    return choice;
  }

  @Override
  protected void adjustStrategy(int available, int prevScore) {
    if (super.score - prevScore == 0 && choice == Decision.SHARE)
      // All previous players decided to share
      choice = Decision.TAKE;
  }
}
