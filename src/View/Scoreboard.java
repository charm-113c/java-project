package View;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;

import Model.Player;
import Model.World;

public class Scoreboard extends JPanel implements ActionListener, KeyListener {
  private List<Player> players;
  private final int HEIGHT;
  private final int TILE_HEIGHT;

  public Scoreboard(World world) {
    this.players = world.getPlayers();
    HEIGHT = world.getHeight();

    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    TILE_HEIGHT = (int) Math.ceilDiv(gd.getDisplayMode().getHeight(), 2 * HEIGHT);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    drawScores(g);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // Not used
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // TODO: implement
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Not used
  }

  private void drawScores(Graphics g) {
    // Sort in descending score order
    players.sort((p, q) -> q.getScore() - p.getScore());
    for (Player p : players) {
      // TODO: draw each player's score at respective tile height
    }
  }
}
