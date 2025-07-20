package Model;

public class TestModelPackage {
  public static void main(String[] args) {
    System.out.println("Creating new world");
    World world = World.newWorld(10, 10, 10, 3, 3, 3, false, true);

    System.out.println("Creating player threads");

    world.beginSimulation();

  }
}
