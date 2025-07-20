# TODO list

## 03/07/2025

There's a lot of things we have to do, but for today we have to finalise the Tile class.
Player.moveIn() must:

- Lock the target tile <- Tile is locked during contention, we must allow exclusive access to player moving in. How can we do that? Occupying player semaphore? Wait, perhaps we don’t need to lock the tile. The tile is already locked while resolving contention, and since the only class that calls Player.moveIn() is the Tile class, we can avoid the complications of using synchronisation mechanisms. Conclusion: don’t lock target tile. This is bad practice in production, but in our specific case, it should be fine.
- Ensure tile is resolving a contention (Player doesn’t make use of the FREE status, as it can move into a tile only after contending for it) ← This isn't actually necessary, once again because moveIn() is called only in contention state.
- Move out of current tile calling Tile.playerMovingOut()
- Move into target tile
- Modify target tile’s status ← Player can’t do this, so it falls to Tile
- Modify player coordinates to target tile’s
Good to go, do as written.

Check moveIn()'s position in the code: should it be within a synchronised block?
DeepSeek suggests having it outside, as it would be deadlock-prone inside.
After that is checked, we've finished with this class.

## 04/07/2025

World.startClock(Thread timekeeper) takes the time master as parameter.
It then proceeds to synchronise on it and sleeps until the game is over,
at which point it notifies the timekeeper.
This means that in the controller package, *timekeeper must also acquire lock on itself before calling gameStart*.
