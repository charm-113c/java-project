# Prisoner's Dilemma Simulation

## The simulation

This project consists in making a 2D simulation of game theory's famous
Prisoner's Dilemma.
In short, the world is an n x m grid, with up to p = n x m players. Each tile on
the grid contains resources (points) and players may gain those points by
moving onto a tile.
However, in order to move into a tile, a player must contend for it:
it could be that multiple players are contending for the same tile, in which
case each individual must decide whether they want to SHARE the tile's points,
or try to TAKE it all for themselves.
If all players choose to SHARE, then the tile's points are shared equally
and no one moves into the tile.
If only one player chooses to TAKE, then that player moves into the tile
and gets all its points.
If more than one player chooses to take, then a conflict arises, destroying
the tile's resources: all players get a small fraction of points, none move
in.

The final objective for all players is to maximise the number of points obtained;
the player with most points upon time out wins.

## Quick start

There are two ways to run the code quickly:

1. Run the `Main.java` in the Controller package,
2. If the `make` command is available, run `make run` in the command line
while in the project's root directory.

Alternatively, from the command line, in the root directory, compilation and running
can be done manually with:
`javac -d bin src/**/*.java && cd bin && java Controller.Main && cd ..`

## Implementation overview

Since it comports a graphical interface (albeit a minimal one),
the project follows the MVC pattern, therefore comports a `Model`, a `View` and
a `Controller` package.

### The `Model` package

This package contains the main logic of the simulation, reflected through
the `Player`, `Tile` and `World` classes as well as other auxiliary classes.

The `World` contains all the `Tile`'s and `Player`'s, and is responsible for
instantiating them upon game start, as well as managing threads' life cycles
through a simple game timer.

The `Player` class models a generic player, capable of moving around and
deciding whether to TAKE or SHARE. Since all players move and decide
independently from one another, this class `implements Runnable` and
each instance is assigned its own Thread.

The `Tile` class also `implements Runnable`: while it does model
each individual tile of the world, each instance also acts as an arbitrator
of the players that are contending for it, and they therefore all play
an active role in this simulation.

### The `View` package

Responsible for the visual part of this simulation, it is made of two classes
that make a graphical representation of the simulation in the simplest
possible way.

### The Controller package

As per the MVC pattern, this package is responsible for connecting the Model
and View packages, and it is also responsible for taking in user input before
the start of the simulation.
