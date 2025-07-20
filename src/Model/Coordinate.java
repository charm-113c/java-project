package Model;

/*
 * This class is needed to be used as key for worldMap.
 * Originally, worldMap used a simple array of integers,
 * however this made getTile fail as arrays' equality
 * depend on their hashcode. In other words,
 * new int[] {1, 2} != new int[] {1, 2}
 * as they're two separate objects that just happen
 * to have the same content.
 * This would make getting keys fail, hence this
 * custom class with a custom equals() method.
 * */

public class Coordinate {
  private int x;
  private int y;

  public Coordinate(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if ((o == null) || (this.getClass() != o.getClass()))
      return false;
    // Check based on content, meaning we need some polymorphism
    Coordinate that = (Coordinate) o;
    return this.x == that.x && this.y == that.y;
  }

  @Override
  public int hashCode() {
    return 42 * x + y;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public void goUp() {
    y--;
  }

  public void goDown() {
    y++;
  }

  public void goLeft() {
    x--;
  }

  public void goRight() {
    x++;
  }

  @Override
  public String toString() {
    return "[" + x + ", " + y + "]";
  }
}
