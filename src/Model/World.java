package Model;

import java.util.Random;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class World {
  // Part of singleton pattern, static to ensure only one World exists at a time
  private static World theWorld;

  // Game time in seconds
  private final long gameTime;
  private final int height;
  private final int width;
  private final int nNeutralPlayers;
  private final int nGreedyPlayers;
  private final int nCoopPlayers;
  private List<Player> people;
  private final boolean canAdapt;
  private final boolean egalitarian;

  // The world is a hash map in practice, associating
  // a Tile object to each coordinate
  private ConcurrentHashMap<Coordinate, Tile> worldMap;

  private World(long time, int width, int height, int nNeutralPlayers, int nGreedyPlayers, int nCoopPlayers,
      boolean canAdapt, boolean egalitarian) {
    gameTime = time;
    this.height = height;
    this.width = width;
    this.nNeutralPlayers = nNeutralPlayers;
    this.nGreedyPlayers = nGreedyPlayers;
    this.nCoopPlayers = nCoopPlayers;
    this.canAdapt = canAdapt;
    this.egalitarian = egalitarian;
    // NOTE: this would seriously benefit from builder pattern
  }

  // Using the static field `theWorld`, the singleton
  // pattern ensures only one World can exist at a time
  public static World newWorld(long time, int width, int height, int nNeutralPlayers, int nGreedyPlayers,
      int nCoopPlayers, boolean canAdapt, boolean egalitarian) {
    if (theWorld == null) {
      System.out.println("Creating world");
      theWorld = new World(time, width, height, nNeutralPlayers, nGreedyPlayers, nCoopPlayers, canAdapt, egalitarian);
      theWorld.initWorld();
    }
    return theWorld;
  }

  public static void resetWorld() {
    System.out.println("The world will be reset, call newWorld() to recreate it.");
    theWorld = null;
  }

  // initWorld effectively creates the world
  private void initWorld() {
    Random r = new Random();
    // Use random to randomise what's going to be on each tile

    if (worldMap == null)
      worldMap = new ConcurrentHashMap<Coordinate, Tile>(width * height);

    // Create world map
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Coordinate coords = new Coordinate(j, i);
        int points = egalitarian ? 25 : r.nextInt(51);
        Tile t = new Tile(points, coords);
        worldMap.put(coords, t);
      }
    }

    // Init people
    people = new ArrayList<Player>();

    // Create players and add them to the world
    for (int i = 0; i < nNeutralPlayers; i++) {
      people.add(Player.newNeutralPlayer(this, Integer.toString(i), canAdapt));
    }
    for (int i = 0; i < nGreedyPlayers; i++) {
      people.add(Player.newGreedyPlayer(this, Integer.toString(i), canAdapt));
    }
    for (int i = 0; i < nCoopPlayers; i++) {
      people.add(Player.newCooperativePlayer(this, Integer.toString(i), canAdapt));
    }
  }

  public void addClassicPlayers() {
    people.add(TitForTat.newTitForTat(this));
    people.add(Friedman.newFriedman(this));
    people.add(Joss.newJoss(this));
  }

  // beginSimulation runs the simulation for given game time
  // and interrupts all threads afterwards
  public void beginSimulation() {
    List<Thread> tileThreads = startTiles();
    List<Thread> playerThreads = startPlayers();

    try {
      Thread.sleep(Duration.ofSeconds(gameTime));
    } catch (InterruptedException e) {
      System.out.println("WARNING: time keeper thread accidentally interrupted, initiating shutdown.");
    }
    // Interrupt player threads first
    for (Thread t : playerThreads) {
      t.interrupt();
    }
    for (Thread t : tileThreads) {
      t.interrupt();
    }
    System.out.println("Game ending");

    try {
      // Give all threads 1s to stop
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.out.println("WARNING: time keeper thread accidentally interrupted after game over.");
    }

    printFinalScores();
  }

  List<Thread> startTiles() {
    List<Thread> tThreads = new ArrayList<Thread>();
    for (Tile tile : worldMap.values()) {
      Thread t = new Thread(tile);
      tThreads.add(t);
      t.start();
    }
    return tThreads;
  }

  List<Thread> startPlayers() {
    List<Thread> pThreads = new ArrayList<Thread>();
    for (Player p : people) {
      Thread t = new Thread(p);
      pThreads.add(t);
      t.start();
    }
    return pThreads;
  }

  void printFinalScores() {
    // Print the scores of all players, from highest to lowest
    people.sort((p, q) -> q.getScore() - p.getScore());
    System.out.println("Name | Score | Self. | Cont. | Takes | Shares");
    for (Player p : people) {
      int[] stats = p.getGameStats();
      System.out.println(p.getName() + " | " + p.getScore() + " | " + p.getSelfishness() + " | " + stats[0] + " | "
          + stats[1] + " | " + stats[2]);
    }
  }

  public static World getWorld() {
    return theWorld;
  }

  public List<Player> getPlayers() {
    return people;
  }

  public int getWidth() {
    return theWorld.width;
  }

  public int getHeight() {
    return theWorld.height;
  }

  // Get tile at a given coordinate
  public synchronized Tile getTile(Coordinate c) {
    return worldMap.get(c);
  }
}
