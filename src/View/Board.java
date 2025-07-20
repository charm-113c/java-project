package View;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import Model.Coordinate;
import Model.Player;
import Model.World;
import Model.Tile;

public class Board extends JPanel implements ActionListener, KeyListener {
  // Clock period in ms
  private final int DELAY = 25;

  private final int WIDTH;
  private final int HEIGHT;
  private final int TILE_WIDTH;
  private final int TILE_HEIGHT;

  private World world;

  public Board(World world) {
    WIDTH = world.getWidth();
    HEIGHT = world.getHeight();
    this.world = world;

    // Get screen size
    // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // TILE_WIDTH = (int) Math.round(screenSize.getWidth() / WIDTH);
    // TILE_HEIGHT = (int) Math.round(screenSize.getHeight() / HEIGHT);
    // When there are multiple monitors:
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    // Keep size to about half-screen
    TILE_WIDTH = (int) Math.ceilDiv(gd.getDisplayMode().getWidth(), 2 * WIDTH);
    TILE_HEIGHT = (int) Math.ceilDiv(gd.getDisplayMode().getHeight(), 2 * HEIGHT);

    // Make a square, for aesthetics
    setPreferredSize(new Dimension(TILE_HEIGHT * WIDTH, TILE_HEIGHT * HEIGHT));

    Timer timer = new Timer(DELAY, this);
    timer.start();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    repaint();
  }

  /*
   * At every update this is called
   */
  @Override
  public void paintComponent(Graphics g) {
    // JPanel extends Component
    super.paintComponent(g);

    drawTiles(g);
    drawPlayers(g);
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

  // Draw the tiles of world
  private void drawTiles(Graphics g) {
    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
        // Get tile at (x,y) and draw it
        Tile t = world.getTile(new Coordinate(x, y));
        int green = t.checkPoints();
        g.setColor(new Color(0, green, 0));
        // Make a square for aesthetics
        g.fillRect(x * TILE_HEIGHT, y * TILE_HEIGHT, TILE_HEIGHT, TILE_HEIGHT);
        // Draw borders
        g.setColor(new Color(128, 128, 128));
        g.drawRect(x * TILE_HEIGHT, y * TILE_HEIGHT, TILE_HEIGHT, TILE_HEIGHT);
        if (t.isProcessingContention()) {
          g.setColor(new Color(128, 0, 0));
          g.fillRect(x * TILE_HEIGHT, y * TILE_HEIGHT, TILE_HEIGHT, TILE_HEIGHT);
          g.setColor(new Color(128, 128, 128));
          g.drawRect(x * TILE_HEIGHT, y * TILE_HEIGHT, TILE_HEIGHT, TILE_HEIGHT);
        }
      }
    }
  }

  private void drawPlayers(Graphics graphics) {
    for (Player p : world.getPlayers()) {
      // Map player's selfishness to a colour
      // Less selfish == bluer, more selfish == redder
      int b = 128 + (int) (127 / 50) * (50 - p.getSelfishness());
      int r = 128 + (int) (127 / 50) * (p.getSelfishness() - 50);

      graphics.setColor(new Color(r, 128, b));
      graphics.fillOval(p.getPosition().getX() * TILE_HEIGHT, p.getPosition().getY() * TILE_HEIGHT, TILE_HEIGHT,
          TILE_HEIGHT);
      graphics.setColor(new Color(255, 255, 255));
      graphics.drawString(p.getName(), p.getPosition().getX() * TILE_HEIGHT,
          p.getPosition().getY() * TILE_HEIGHT + TILE_HEIGHT / 2);
    }
  }
}
