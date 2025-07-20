package View;

import javax.swing.*;

import Model.World;

public class GraphicalInterface {
  public static void newGame(World world) {
    // Create a window to display everything in
    JFrame window = new JFrame("Prisoner's Dilemma");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create the board we'll display
    Board board = new Board(world);
    // Scoreboard scoreboard = new Scoreboard(world.getPlayers());

    window.add(board);
    // window.add(scoreboard);
    window.pack();
    // Centre the window
    window.setLocationRelativeTo(null);
    window.setVisible(true);
  }
}
