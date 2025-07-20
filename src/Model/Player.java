package Model;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Player implements Runnable {
  public enum Decision {
    TAKE, SHARE
  }

  private final String NAME;
  // Between 0 and 100, indicates chance of deciding to TAKE during a contest
  private int selfishness;
  // Willingness to modify selfishness depending on environment
  private int adaptability;
  protected volatile int score = 0;
  private volatile Coordinate coordinates;

  private World world;

  // Game stats
  protected int nContentions = 0;
  protected int nTakes = 0;

  Player(World world, String name, int selfishness, int adaptability, Coordinate coordinates) {
    this.world = world;
    NAME = name;
    // selfishness in [0, 100) can be seen as probability for player to choose TAKE
    this.selfishness = selfishness;
    // In [0, 5] is factor by which we increment or decrement selfishness
    this.adaptability = adaptability;
    this.coordinates = coordinates;
  }

  protected static Coordinate findFreeCoords(World world, Random rand) {
    // Generate random coords from the world map, see if it's unoccupied
    Coordinate coords = new Coordinate(rand.nextInt(world.getWidth()), rand.nextInt(world.getHeight()));
    while (!world.getTile(coords).isOpen()) {
      coords = new Coordinate(rand.nextInt(world.getWidth()), rand.nextInt(world.getHeight()));
    }

    return coords;
  }

  // Spawns a player with random but neutral stats on a random unoccupied tile
  public static Player newNeutralPlayer(World world, String name, boolean canAdapt) {
    Random rand = new Random();
    Coordinate coords = findFreeCoords(world, rand);
    // Generate player stats
    int adaptability = canAdapt ? rand.nextInt(6) : 0;
    Player p = new Player(world, "N. " + name, rand.nextInt(40, 60), adaptability, coords);
    // Add player to that tile
    world.getTile(coords).spawnPlayer(p);
    System.out.println("Player " + p.getName() + " spawned at coordinates " + coords.toString());

    return p;
  }

  public static Player newCooperativePlayer(World world, String name, boolean canAdapt) {
    Random rand = new Random();
    Coordinate coords = findFreeCoords(world, rand);
    int adaptability = canAdapt ? rand.nextInt(6) : 0;
    Player p = new Player(world, "C. " + name, rand.nextInt(40), adaptability, coords);
    world.getTile(coords).spawnPlayer(p);
    System.out.println("Player " + p.getName() + " spawned at coordinates " + coords.toString());

    return p;
  }

  public static Player newGreedyPlayer(World world, String name, boolean canAdapt) {
    Random rand = new Random();
    Coordinate coords = findFreeCoords(world, rand);
    int adaptability = canAdapt ? rand.nextInt(6) : 0;
    Player p = new Player(world, "G. " + name, rand.nextInt(60, 100), adaptability, coords);
    world.getTile(coords).spawnPlayer(p);
    System.out.println("Player " + p.getName() + " spawned at coordinates " + coords.toString());

    return p;
  }

  public int getScore() {
    return score;
  }

  public int getSelfishness() {
    return selfishness;
  }

  public Coordinate getPosition() {
    return coordinates;
  }

  public int[] getGameStats() {
    return new int[] { nContentions, nTakes, nContentions - nTakes };
  }

  public String getName() {
    return NAME;
  }

  public void addPoints(int p) {
    score += p;
  }

  // When a player is contending for a tile against other players,
  // they have to decide whether to TAKE or to SHARE
  // The contend method outputs their choice
  public Decision contend() {
    // See selfishness as probability for player to TAKE
    if (ThreadLocalRandom.current().nextInt(100) < selfishness) {
      return Decision.TAKE;
    }
    return Decision.SHARE;
  }

  // moveIn() is called by the Tile the player will move into.
  // NOTE: moveIn is called by Tile when the latter has
  // a lock on itself, so other players cannot interact with
  // the tile in the process
  public void moveIn(Tile t) {
    Tile currentTile = world.getTile(coordinates);
    currentTile.playerMovingOut();
    // Update coords
    coordinates = t.getCoords();
    System.out.println("Player " + getName() + " moved to coords: " + coordinates.toString());
    // Score update done by tile
  }

  public void move() {
    // Generate random number to decide direction
    // Random rand = new Random();

    // int die = rand.nextInt(4);
    // while ((checkTile(die) == null) || !checkTile(die).isOpen()) {
    // die = rand.nextInt(4);
    // }
    // Implement a more "rational" choice, where players go for neighbouring tile
    // with most points
    int die = 0;
    boolean checked = false; // Keep in loop until valid tile is found
    while (!checked || checkTile(die) == null || !checkTile(die).isOpen()) {
      checked = true;
      for (int i = 0; i < 4; i++) {
        if (checkTile(i) != null && checkTile(i).isOpen()) {
          if (checkTile(die) == null)
            die = i;
          else
            die = checkTile(die).checkPoints() > checkTile(i).checkPoints() ? die : i;
        }
      }
    }

    // Contend for new tile
    Tile t = checkTile(die);
    // To adjust strategy
    int prevScore = score;
    int available = t.checkPoints();
    System.out.println("Player " + getName() + " will contend for tile " + t.getCoords().toString());
    if (!t.isOpen())
      return;
    // BUG: Friedman, Joss and TitForTat do not set Tile.State to OCCUPIED,
    // and I have a feeling it's related to their constructor.
    // contendForTile wants a Player, so it's trying to use polymorphism
    // but something probably goes wrong as it does that.
    t.contendForTile(this);
    nContentions++;
    while (t.isProcessingContention()) {
      try {
        Thread.sleep(100);
        // NOTE: preferred to waiting on Tile. Tile already
        // implements 2 different synchronisation logics;
        // to wait for it to process, we'd have to acquire tile lock, but upon releasing
        // that lock we'd have to notify other player threads that also want it:
        // this risks waking up the tile that is also waiting on this lock when in
        // OCCUPIED state
      } catch (InterruptedException e) {
        // Interrupt signal == init shutdown
        Thread.currentThread().interrupt();
        return;
      }
    }
    // The tile will take care of calling moveIn() in case of success

    adjustStrategy(available, prevScore);
  }

  protected void adjustStrategy(int available, int prevScore) {
    // To adjust strategy, consider how many points were gained
    // Unless no points were available
    if (available == 0)
      return;
    if (score - prevScore != available) {
      // Didn't take all tile's points
      if (score - prevScore == 0) {
        // Case: was greedy, but other players were also greedy
        selfishness = Math.max(selfishness - adaptability, 0);
        nTakes++;
      } else {
        // All players chose to SHARE, increase selfishness
        selfishness = Math.min(selfishness + adaptability, 100);
      }
    } else {
      // Chose TAKE and got all points, no changes required
      nTakes++;
    }
  }

  /*
   * Given a number from 0 to 3,
   * returns the tile in that direction or null if it doesn't exist
   */
  private Tile checkTile(int die) {
    if ((die == 0) && (coordinates.getY() - 1 >= 0))
      // Get tile above after checking new coordinate is valid
      return world.getTile(new Coordinate(coordinates.getX(), coordinates.getY() - 1));
    if ((die == 1) && (world.getWidth() > coordinates.getX() + 1))
      // Get tile to the right
      return world.getTile(new Coordinate(coordinates.getX() + 1, coordinates.getY()));
    if ((die == 2) && (world.getHeight() > coordinates.getY() + 1))
      // Get tile below
      return world.getTile(new Coordinate(coordinates.getX(), coordinates.getY() + 1));
    if ((die == 3) && (coordinates.getX() - 1 >= 0))
      // Get tile to the left
      return world.getTile(new Coordinate(coordinates.getX() - 1, coordinates.getY()));

    return null;
  }

  @Override
  public void run() {
    // Have players move around the map after
    // staying on current tile for a random amount of time
    Random rand = new Random();
    try {
      while (!Thread.currentThread().isInterrupted()) {
        Thread.sleep(rand.nextInt(100, 3000));
        move();
        // System.out.println("Player " + getName() + " score: " + score);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    System.out.println("Player " + NAME + " stopping");
  }
}
