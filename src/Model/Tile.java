package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import Model.Player.Decision;

public class Tile implements Runnable {
  private enum State {
    FREE, AWAITING, CONTENDING, OCCUPIED
  }

  private final int MAX_POINTS;
  private int points;
  // REGEN_RATE determines how much of maxPoints
  // can be regenerated per tick; 0 < REGEN_RATE < 0.1;
  // NOTE: changed to [0.1, 0.2]
  private final float REGEN_RATE;
  private List<Player> contendingPlayers;
  private final int MAX_CONTENDERS = 4;
  private final Coordinate COORDS;
  private final Object lock;

  // Used to wait for players to declare contention before
  // tile locks itself away and begins resolution
  private Semaphore contenderSemaphore;
  private CountDownLatch awaitLatch;

  // Volatile keyword ensures all threads read up-to-date data
  private volatile Player occupyingPlayer;
  private volatile State state;
  private volatile boolean awaitPhaseComplete;
  private volatile boolean processingContention;

  Tile(int points, Coordinate coords) {
    MAX_POINTS = points;
    this.points = points;
    REGEN_RATE = ThreadLocalRandom.current().nextFloat(0.1f, 0.2f);
    COORDS = coords;
    state = State.FREE;
    contendingPlayers = new ArrayList<Player>();
    lock = new Object();
    contenderSemaphore = new Semaphore(MAX_CONTENDERS, true);
    awaitLatch = new CountDownLatch(1);
  }

  // Called only once (maybe never) if a player spawns
  // into current tile
  // Since players spawn serially, no synchronisation
  // is required
  public void spawnPlayer(Player p) {
    state = State.OCCUPIED;
    occupyingPlayer = p;
    p.addPoints(points);
  }

  // Called by Player AFTER they've DEFINITIVELY moved
  // to another tile
  public void playerMovingOut() {
    synchronized (lock) {
      occupyingPlayer = null;
      state = State.FREE;
      // Wake up tile to listen for new players
      lock.notify();
    }
  }

  // NOTE: if a tile is isn't open, players cannot
  // contend for it in the first place
  public synchronized boolean isOpen() {
    return state == State.FREE || state == State.AWAITING;
  }

  public synchronized boolean isProcessingContention() {
    return processingContention;
  }

  public Player getPlayer() {
    return occupyingPlayer;
  }

  public int checkPoints() {
    return points;
  }

  public Coordinate getCoords() {
    return COORDS;
  }

  // A player must contend for a tile before they
  // can move into it.
  public void contendForTile(Player p) {
    if (awaitPhaseComplete)
      return;
    try {
      // NOTE: semaphore permit limit ensures max 4 players can contend
      if (contenderSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
        // If semaphore permit acquired, acquire lock on tile
        synchronized (lock) {
          if (state == State.FREE) {
            // Initiate Tile's Await phase
            state = State.AWAITING;
            processingContention = true;
            // Wake up Tile
            lock.notifyAll();
          }
          contendingPlayers.add(p);
          // If contendingPlayers list is full, initiate resolution
          if (contendingPlayers.size() == MAX_CONTENDERS) {
            awaitPhaseComplete = true;
            // Signal tile
            awaitLatch.countDown();
          }
        }
      }
    } catch (InterruptedException e) {
      // Interrupt sig == initiate shutdown
      // Reset Thread's interrupted status
      Thread.currentThread().interrupt();
    }
    return;
  }

  private void regeneratePoints() {
    points = points > MAX_POINTS ? MAX_POINTS : points + (int) (MAX_POINTS * REGEN_RATE);
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      synchronized (lock) {
        // Check state:
        // 0. If OCCUPIED, wait() to avoid busy waiting
        while (state == State.OCCUPIED) {
          // NOTE: in a while loop because JVM allows spurious wakeups
          try {
            lock.wait();
          } catch (InterruptedException e) {
            // Interrupt sig == initiate shutdown
            Thread.currentThread().interrupt();
            return;
          }
        }
        // 1. If FREE, wait for potential contenders
        if (state == State.FREE) {
          regeneratePoints();
          try {
            // Wait 1s for players to contend
            // (and limit regen rate to 1 tick/s)
            lock.wait(1000);
          } catch (InterruptedException e) {
            // Interrupt sig == initiate shutdown
            Thread.currentThread().interrupt();
            return;
          }
        }
      }
      // 2. After 1st contender arrives,
      if (state == State.AWAITING) {
        try {
          // Wait until timeout (allow 3s window for new contenders to come)
          awaitLatch.await(3000, TimeUnit.MILLISECONDS);
          // Or until MAX_CONTENDERS is reached and latch is freed
        } catch (InterruptedException e) {
          // Or interrupt signalled
          Thread.currentThread().interrupt();
          return;
        }
        // 3. Conclude await state, resolve contention
        synchronized (lock) {
          state = State.CONTENDING;
          resolveContention();
        }
      }
    }
  }

  // Resolve contention depending on number of players
  // and their decisions
  public void resolveContention() {
    if (contendingPlayers.size() == 1) {
      Player p = contendingPlayers.removeFirst();
      p.moveIn(this);
      p.addPoints(points);
      occupyingPlayer = p;
      postContentionReset();
      return;
    }

    HashMap<Player, Player.Decision> decisions = new HashMap<Player, Player.Decision>();
    // Collect decisions
    for (Player p : contendingPlayers) {
      decisions.put(p, p.contend());
    }
    // Count number of taking players
    int takeCount = (int) decisions.values().stream()
        .filter(decision -> decision == Decision.TAKE)
        .count();
    // Resolve contention accordingly
    if (takeCount == 0) {
      // Distribute points among contenders equally,
      // no player moves in
      for (Player p : contendingPlayers) {
        p.addPoints(1 + (int) points / contendingPlayers.size());
      }
    } else if (takeCount == 1) {
      // The taking player moves in and gets all the points
      for (Player p : contendingPlayers) {
        if (decisions.get(p) == Decision.TAKE) {
          p.moveIn(this);
          p.addPoints(points);
          occupyingPlayer = p;
        }
      }
    } else {
      for (Player p : contendingPlayers) {
        p.addPoints(1);
      }
    }
    // Else:
    // Two or more players decided to TAKE,
    // -> conflict, no one gets any point
    // NOTE: per formal rules, a valid Prisoner's dilemma
    // must have that in this case they still get more point
    // than when one player takes all -> give one point to all

    postContentionReset();
  }

  private void postContentionReset() {
    state = occupyingPlayer == null ? State.FREE : State.OCCUPIED;
    points = 0;
    contendingPlayers.clear();
    // Reset semaphore
    contenderSemaphore.release(MAX_CONTENDERS - contenderSemaphore.availablePermits());
    // Make new CountDownLatch
    awaitLatch = new CountDownLatch(1);
    // Reset flags
    awaitPhaseComplete = false;
    processingContention = false;
  }
}
