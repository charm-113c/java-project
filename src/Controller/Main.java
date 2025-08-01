package Controller;

import java.util.Scanner;

import Model.*;
import View.GraphicalInterface;

/*
 * This program simulates a world whose inhabitants
 * may interact with one another. Each inhabitant will
 * will act independently from the others --excluding
 * special circumstances-- as they will each be simulated by
 * their own thread.
 *
 * Inhabitants may form colonies (in practice, merge)
 * or choose to stay alone. Either way, just as they live,
 * they can die.
*/

public class Main {
  public static void main(String[] args) {
    System.out.println("Beginning simulation. Enter the world parameters:");

    // Scan input
    Scanner input = new Scanner(System.in);
    System.out.println("Enter game time (in s): ");
    int gameTime = input.nextInt();
    while (gameTime > 300) {
      System.out.println("Please, keep game time to under 5 minutes");
      gameTime = input.nextInt();
    }
    System.out.println("Enter world width: ");
    int w = input.nextInt();
    System.out.println("Enter world height: ");
    int h = input.nextInt();
    System.out.println("Enter number of neutral players: ");
    int n = input.nextInt();
    while (n > w * h) {
      System.out.println("Too many players, please enter a number up to " + w * h);
      n = input.nextInt();
    }
    System.out.println("Enter number of greedy players: ");
    int g = input.nextInt();
    while (g > w * h - n) {
      System.out.println("Too many players, please enter a number up to " + (w * h - n));
      g = input.nextInt();
    }
    System.out.println("Enter number of cooperative players: ");
    int c = input.nextInt();
    while (c > w * h - (n + g)) {
      System.out.println("Too many players, please enter a number up to " + (w * h - n - g));
      c = input.nextInt();
    }
    System.out.println("Players' strategies evolve according to points gained, true or false?");
    boolean adapt = input.nextBoolean();
    System.out.println("All tiles start with the same amount of points, true or false?");
    boolean egalitarian = input.nextBoolean();
    boolean classic = false;
    if (w * h - (n + g + c) >= 3) {
      System.out.println("Add TitForTat, Joss and Friedman, true or false?");
      classic = input.nextBoolean();
    }
    input.close();

    // Init game world
    World world = World.newWorld(gameTime, w, h, n, g, c, adapt, egalitarian);
    if (classic)
      world.addClassicPlayers();
    // Create game window
    GraphicalInterface.newGame(world);
    world.beginSimulation();
    System.out.println("Main thread stopping");
  }
}
