# Prisoner’s dilemma: 2D simulation

Project goal: do the conclusions reached by studies on the prisoner’s dilemma still hold with a more “realistic” version of the experiment?

## Game Theory and the Prisoner’s dilemma

One of the most famous problems in Game Theory is the Prisoner’s dilemma. In its original form, it involves two prisoners who each have to choose whether they will be altruistic or selfish, but an analogous format can be used to allow it to become a tournament among players. This format has players play 1vs1 matches of 200 rounds, and each round consists in each player deciding whether they want to cooperate or defect: if both players cooperate, then they both receive 3 points. If one player cooperates but the other defects, then the one defecting will receive 5 points while the one cooperating receives none. If both players decide to defect, then they both receive 1 point. The difficulty in choosing whether to cooperate or defect is what earned this thought experiment the name “dilemma”.
It’s very important to note that to win the tournament, it doesn’t matter how many times a player wins, what matters is who the player with most points is.

This dilemma has been widely studied, not only because of its complexity, but also because of its implications: would it be better to be altruistic but risk getting taken advantage of, or should one prioritise oneself but risk making the situation worse for all? This question is ubiquitous in the real world, from personal disputes to international politics. One instance of this is environmental pollution: governments have to decide whether they want to preserve the environment and thus decrease, among else, their industrial outputs; this stunts their growth, which can particularly hurt developing countries and put them at a further economic disadvantage compared to developed countries. Alternatively, they could choose not to adhere to international treaties in order to maximise their own growth, but this is to the detriment of the environment and is not sustainable long-term.

## The project’s approach

### The expected results

When more than multiple successive matches are played, and players may adapt their strategies in function of their opponent’s last move, we have the *iterated* prisoner’s dilemma.
Henceforth, we will be referring to the research conducted by Prof. Axelrod, who popularised the tournament format of this dilemma. In the 80’s, prof. Axelrod organised the aforementioned prisoner’s dilemma tournament, and invited game theorists around the world to submit computer programs, each with their own strategies, to try and win the tournament. Some of the notable strategies submitted were a player that always defects, then “Tit for tat”, which starts by cooperating and then copies their opponent’s previous move --they would defect after their opponent defects, and cooperate after cooperation, hence the name; another strategy was “Grim trigger”, aka “Friedman”, who started by cooperating but would then always defect if its opponent defects.
The contestants were a balanced mix of “nice” players (i.e. players who never defect first) and “nasty” players (who would defect unprompted), and their complexity varied from random decisions to complex programs nearing a hundred lines of code.
Prof. Axelrod ran the tournament a few times for robustness, and found, surprisingly, that tit for tat consistently came out on top, and that nice players consistently outperformed nasty players. This is presumably because nice players would cooperate between one another, ensuring both players got a good amount of points from their matches, while nasty players were prone to falling into defect-defect loops. He came to the conclusion that a winning strategy should be:
- Nice
- Retaliating: if defected on, a player should strike back immediately
- Forgiving: but if the opponent returns to cooperation, so should they
- Clear: easy to understand and get a grasp on
Ideally, these are the same conclusions we want to reach.
Obviously, the results are heavily dependent on the strategies being used; for example, tit for tat would do poorly if all other players were nasty. With this in mind, we will try to maintain a balanced environment, or conversely test our simulation through extreme cases.


### The dilemma as a 2D simulation

The version of the dilemma this project focuses on is one with repeated but diversified interactions. Let’s modify the format while maintaining the core: players spawn in a world made of tiles, and when they move on a tile they get the points on it. The goal of the game is to have the most points upon time out.
To move on a tile, a player must contend for it: if no other player contends for it, then the player may move in. But if other players come and contend for the same tile, then the contending players must each decide whether they want to SHARE or TAKE. If all players share, then they divide the tile’s points equally among them and no one moves in. If exactly one player decides to TAKE, then they move into the tile and take all the points for themselves. If two or more players take, then a conflict arises, destroying the tiles resources: no one gets any point, and no one moves into the tile. When a tile is free, it will regenerate points every second.
This version seeks to mimic a very simplified world, where resources are limited and not necessarily distributed equally among tiles, similarly to how they are in the world. Players also spawn in random places.
The details of the implementation are left to the code, but in short, there are three main classes: Tile, Player and World. The World is made up of Tiles and is responsible for spawning Players into it. Both Tiles and Players implement Runnable and are ran by their own threads. Players can contend for surrounding Tiles, but Tiles are the ones responsible for handling contentions and synchronising 1 or more Player to Tile interactions.

## Observations and the path to meaningful data

Initial runs gave results that bordered on the random. They consisted of balanced runs lasting 200 seconds in a 20 by 10 world with a total of 60 players. 20 of the players were neutral, and had a more or less 50% chance of sharing. Another 20 were greedy, and had between 60-100% probability of taking. The last 20 were cooperative and had equally high chances of sharing. This is the standard setup used from here on.

- Increase tiles regenRate; it’s too low, making a lot of tiles worthless.
- Make players attracted to tiles with more points. Current version (random direction choice) is akin to having irrational prisoners.
- Respecting the formal rules and modifying point gains: ￼⁠￼⁠ T > R > P > S, and in the iterative case, 2R > T + S. 
T: points gained by player who defects when other players cooperate. R: Points obtained when all cooperate. P: points received when both players defect. S: points received by cooperating player when other player is defecting.

After applying the above changes, one-by-one one on top of the other, a pattern is beginning to emerge: while it’s not uncommon for cooperative players to make it to the podium, all the high ranking players have chosen to SHARE surprisingly few times. TitForTat also doesn’t seem to be doing so well.

## Conclusions

With high consistency, players who share few times end up on the podium. This might be due to chance, because the player density isn’t high enough, and some players might find a lot of empty tiles. But it’s also consistently the greedy players that come out on top, and even the cooperative ones that make it high up end up having shared very few times. The latter observation is, as said, likely due to chance.
We then test a very high density setup: a 10*10 world with 30+30+30+3 players for 120s. Tiles have fixed number of points and high regen rate, players may change strategies. Single run result: greedy players top the podium, and the highest scoring cooperative player is 15th. Once again, we notice a low to very low share rate among the top players. Rerunning same setup. Extremely high density leaves a lot to chance, since many players don’t even have the possibility to move. And yet, the earlier observations remain.
Different setups show similar results, and none display anything similar to the conclusions prof. Axelrod had reached, not with any consistency anyway.
Is the simulation then flawed? Likely, it’s too different from the original prisoner’s dilemma, even in its iterative format, to be compared. One major difference is that choice is probabilistic in our case, whereas they were deterministic in the tournament. There’s also that in the case of our simulation, the fact that there is movement means that players do not “play successive games” against the same players, they do not necessarily continuously contend against the same players. Given the many runs, it would seem reasonable to conclude that the movement favours greedy players and selfish strategies: the main problem that held them back in Axelrod’s tournament were the endless defection cycles, where if one player defected then so did the other, leading to an endless loop of defection. These are very unlikely to happen in this case, meaning that greed isn’t as punished as it is in a standard prisoner’s dilemma.
This doesn’t invalidate prof. Axelrod’s conclusions in any way, and in fact, it opens the door to a new question: if a mechanic is then implemented that punishes greed similarly to in an iterated prisoner’s dilemma, would we see results that do reflect prof. Axelrod’s conditions for a winning strategy? And if not, would that be because we’ve misdiagnosed the crucial difference that favours selfish players in our simulation, or because there are more intricacies to the prisoner’s dilemma that I personally have yet to discover?
These questions are left open, hopefully to be answered one day by curiosity.

# Appendix

## Some final scoreboards

200s, 20*10 world, 10+10+10+3 players, no adaptation, random tile points
Name | Score | Self. | nContentions | Takes | Shares
G. 9 | 1237 | 90 | 45 | 43 | 2
C. 0 | 1111 | 17 | 45 | 44 | 1
C. 5 | 1047 | 5 | 46 | 41 | 5
N. 7 | 984 | 40 | 46 | 41 | 5
N. 2 | 983 | 58 | 46 | 45 | 1
G. 3 | 979 | 96 | 44 | 44 | 0
Joss | 759 | 0 | 46 | 0 | 46
C. 2 | 744 | 10 | 47 | 39 | 8
N. 8 | 689 | 59 | 46 | 43 | 3
Friedman | 595 | 0 | 44 | 0 | 44
G. 6 | 565 | 79 | 46 | 46 | 0
C. 3 | 539 | 39 | 42 | 42 | 0
G. 1 | 526 | 93 | 46 | 46 | 0
G. 5 | 488 | 79 | 43 | 41 | 2
C. 8 | 459 | 8 | 48 | 37 | 11
G. 8 | 437 | 81 | 46 | 43 | 3
C. 7 | 422 | 24 | 45 | 38 | 7
TitForTat | 385 | 0 | 46 | 0 | 46
G. 7 | 384 | 76 | 47 | 44 | 3
G. 2 | 382 | 65 | 45 | 44 | 1
N. 0 | 379 | 40 | 48 | 44 | 4
G. 0 | 367 | 62 | 47 | 41 | 6
C. 9 | 363 | 20 | 48 | 27 | 21
N. 1 | 357 | 59 | 47 | 35 | 12
C. 4 | 354 | 20 | 47 | 35 | 12
C. 6 | 347 | 18 | 46 | 29 | 17
N. 9 | 342 | 50 | 52 | 49 | 3
G. 4 | 337 | 92 | 47 | 45 | 2
N. 5 | 328 | 46 | 45 | 38 | 7
N. 3 | 300 | 50 | 53 | 44 | 9
N. 4 | 286 | 57 | 47 | 44 | 3
C. 1 | 279 | 4 | 47 | 34 | 13
N. 6 | 264 | 50 | 44 | 29 | 15

200s, 20*10 world, 10+10+10+3 players, no adaptation, random tile points
Name | Score | Self. | Cont. | Takes | Shares
TitForTat | 1046 | 0 | 44 | 0 | 44
G. 5 | 974 | 79 | 46 | 44 | 2
G. 3 | 863 | 82 | 42 | 40 | 2
Friedman | 767 | 0 | 44 | 0 | 44
N. 1 | 766 | 46 | 45 | 43 | 2
G. 6 | 756 | 75 | 44 | 42 | 2
C. 6 | 734 | 4 | 45 | 43 | 2
N. 3 | 665 | 42 | 44 | 43 | 1
C. 4 | 656 | 1 | 47 | 46 | 1
N. 4 | 630 | 43 | 48 | 41 | 7
N. 0 | 611 | 57 | 46 | 40 | 6
C. 0 | 509 | 10 | 45 | 41 | 4
G. 4 | 497 | 89 | 45 | 42 | 3
N. 5 | 475 | 53 | 46 | 39 | 7
G. 2 | 466 | 91 | 44 | 43 | 1
C. 8 | 447 | 11 | 46 | 44 | 2
Joss | 440 | 0 | 48 | 0 | 48
G. 0 | 419 | 93 | 48 | 47 | 1
N. 8 | 414 | 55 | 47 | 42 | 5
G. 8 | 399 | 60 | 45 | 27 | 18
N. 9 | 381 | 44 | 49 | 37 | 12
N. 6 | 363 | 44 | 52 | 45 | 7
C. 5 | 341 | 4 | 50 | 32 | 18
C. 2 | 338 | 7 | 45 | 44 | 1
G. 1 | 325 | 77 | 47 | 41 | 6
G. 7 | 320 | 95 | 51 | 49 | 2
C. 3 | 319 | 8 | 47 | 43 | 4
C. 9 | 307 | 3 | 45 | 38 | 7
C. 1 | 278 | 7 | 47 | 31 | 16
C. 7 | 266 | 3 | 47 | 32 | 15
N. 2 | 262 | 46 | 44 | 38 | 6
N. 7 | 255 | 43 | 48 | 37 | 11
G. 9 | 255 | 79 | 46 | 39 | 7

200s, 20*10 world, 10+10+10+3 players, no adaptation, random tile points
Name | Score | Self. | Cont. | Takes | Shares
C. 6 | 1143 | 36 | 45 | 44 | 1
G. 4 | 1074 | 76 | 45 | 44 | 1
N. 5 | 1063 | 45 | 44 | 41 | 3
G. 5 | 1018 | 87 | 47 | 45 | 2
G. 3 | 772 | 84 | 46 | 45 | 1
C. 7 | 758 | 24 | 45 | 43 | 2
TitForTat | 725 | 0 | 49 | 0 | 49
N. 8 | 686 | 59 | 47 | 46 | 1
Friedman | 680 | 0 | 45 | 0 | 45
N. 9 | 662 | 56 | 48 | 47 | 1
N. 7 | 654 | 49 | 44 | 41 | 3
N. 3 | 650 | 56 | 45 | 40 | 5
G. 8 | 633 | 78 | 48 | 47 | 1
C. 0 | 554 | 4 | 45 | 38 | 7
G. 0 | 537 | 64 | 47 | 42 | 5
G. 6 | 520 | 73 | 47 | 45 | 2
N. 2 | 488 | 42 | 43 | 40 | 3
G. 1 | 444 | 68 | 45 | 39 | 6
N. 4 | 434 | 41 | 46 | 40 | 6
G. 2 | 427 | 94 | 47 | 44 | 3
C. 4 | 396 | 18 | 47 | 42 | 5
C. 9 | 383 | 29 | 48 | 41 | 7
C. 1 | 373 | 14 | 47 | 36 | 11
C. 2 | 345 | 20 | 44 | 38 | 6
N. 6 | 323 | 40 | 45 | 25 | 20
N. 1 | 280 | 53 | 47 | 42 | 5
C. 3 | 265 | 38 | 45 | 34 | 11
C. 5 | 232 | 18 | 49 | 41 | 8
N. 0 | 214 | 42 | 46 | 36 | 10
C. 8 | 177 | 31 | 46 | 24 | 22
G. 7 | 167 | 89 | 46 | 39 | 7
G. 9 | 158 | 67 | 45 | 22 | 23
Joss | 102 | 0 | 46 | 0 | 46

200s, 20*10 world, 10+10+10+3 players, no adaptation, random tile points
Name | Score | Self. | Cont. | Takes | Shares
G. 7 | 963 | 83 | 44 | 42 | 2
N. 9 | 903 | 52 | 46 | 43 | 3
C. 4 | 896 | 20 | 48 | 42 | 6
C. 8 | 866 | 10 | 46 | 40 | 6
G. 1 | 863 | 92 | 44 | 43 | 1
N. 4 | 826 | 59 | 49 | 43 | 6
C. 6 | 815 | 37 | 46 | 41 | 5
N. 2 | 653 | 53 | 47 | 45 | 2
N. 5 | 626 | 46 | 46 | 37 | 9
Joss | 619 | 0 | 43 | 0 | 43
C. 5 | 554 | 3 | 48 | 42 | 6
C. 2 | 548 | 16 | 44 | 37 | 7
G. 8 | 482 | 80 | 47 | 45 | 2
TitForTat | 478 | 0 | 45 | 0 | 45
G. 4 | 463 | 94 | 46 | 45 | 1
N. 0 | 431 | 51 | 45 | 28 | 17
G. 3 | 431 | 93 | 47 | 46 | 1
C. 9 | 392 | 31 | 43 | 33 | 10
N. 8 | 390 | 54 | 46 | 39 | 7
G. 0 | 389 | 60 | 47 | 36 | 11
C. 7 | 383 | 34 | 44 | 41 | 3
G. 5 | 328 | 76 | 48 | 41 | 7
N. 1 | 322 | 46 | 47 | 28 | 19
N. 6 | 295 | 52 | 45 | 39 | 6
G. 9 | 294 | 97 | 48 | 48 | 0
G. 2 | 283 | 95 | 42 | 41 | 1
G. 6 | 277 | 73 | 44 | 28 | 16
Friedman | 259 | 0 | 51 | 0 | 51
C. 1 | 248 | 39 | 47 | 27 | 20
C. 3 | 220 | 12 | 44 | 24 | 20
N. 3 | 216 | 57 | 47 | 31 | 16
N. 7 | 175 | 52 | 49 | 27 | 22
C. 0 | 155 | 23 | 47 | 39 | 8

300s, 20*10 world, 20+20+20+3 players, can adapt, random tile points, increased tile regen rate
Name | Score | Self. | Cont. | Takes | Shares
G. 12 | 1903 | 59 | 68 | 64 | 4
C. 3 | 1697 | 34 | 64 | 56 | 8
G. 2 | 1575 | 67 | 71 | 67 | 4
N. 6 | 1515 | 51 | 71 | 62 | 9
G. 17 | 1515 | 59 | 68 | 63 | 5
G. 14 | 1463 | 57 | 71 | 65 | 6
G. 0 | 1451 | 68 | 74 | 72 | 2
G. 7 | 1431 | 48 | 71 | 65 | 6
N. 11 | 1421 | 42 | 69 | 60 | 9
G. 5 | 1385 | 80 | 74 | 70 | 4
N. 16 | 1313 | 55 | 69 | 63 | 6
G. 8 | 1309 | 54 | 71 | 64 | 7
Friedman | 1287 | 0 | 72 | 0 | 72
C. 1 | 1280 | 27 | 69 | 59 | 10
G. 18 | 1262 | 51 | 72 | 61 | 11
G. 1 | 1248 | 59 | 74 | 71 | 3
Joss | 1204 | 0 | 74 | 0 | 74
C. 8 | 1197 | 13 | 71 | 58 | 13
G. 6 | 1187 | 54 | 75 | 71 | 4
TitForTat | 1185 | 0 | 72 | 0 | 72
N. 3 | 1128 | 48 | 74 | 65 | 9
N. 17 | 1127 | 51 | 66 | 57 | 9
C. 16 | 1125 | 34 | 74 | 59 | 15
N. 14 | 1118 | 0 | 71 | 61 | 10
N. 15 | 1100 | 51 | 72 | 62 | 10
C. 19 | 1100 | 45 | 71 | 57 | 14
C. 18 | 1098 | 19 | 72 | 61 | 11
G. 10 | 1065 | 64 | 72 | 63 | 9
C. 10 | 1064 | 23 | 68 | 53 | 15
C. 4 | 1062 | 11 | 70 | 58 | 12
C. 5 | 1041 | 13 | 70 | 55 | 15
C. 12 | 1030 | 3 | 67 | 58 | 9
G. 15 | 1022 | 50 | 75 | 70 | 5
C. 14 | 1015 | 28 | 68 | 51 | 17
G. 11 | 1006 | 44 | 75 | 63 | 12
G. 13 | 998 | 69 | 72 | 70 | 2
C. 2 | 993 | 20 | 74 | 48 | 26
C. 7 | 993 | 0 | 69 | 59 | 10
G. 16 | 979 | 49 | 70 | 63 | 7
N. 4 | 968 | 32 | 71 | 61 | 10
G. 9 | 958 | 64 | 72 | 58 | 14
N. 12 | 956 | 50 | 70 | 56 | 14
N. 1 | 941 | 25 | 69 | 58 | 11
N. 2 | 931 | 4 | 70 | 62 | 8
G. 4 | 922 | 61 | 69 | 61 | 8
C. 15 | 914 | 4 | 71 | 53 | 18
C. 6 | 911 | 3 | 71 | 60 | 11
N. 19 | 902 | 8 | 72 | 61 | 11
N. 0 | 901 | 48 | 74 | 57 | 17
N. 9 | 883 | 40 | 72 | 59 | 13
N. 13 | 810 | 20 | 71 | 63 | 8
C. 11 | 804 | 6 | 71 | 54 | 17
N. 10 | 803 | 43 | 71 | 61 | 10
N. 5 | 802 | 43 | 71 | 55 | 16
C. 0 | 795 | 0 | 73 | 59 | 14
G. 3 | 780 | 42 | 68 | 58 | 10
C. 9 | 743 | 4 | 68 | 51 | 17
N. 8 | 718 | 48 | 72 | 55 | 17
G. 19 | 710 | 29 | 69 | 57 | 12
N. 7 | 628 | 29 | 71 | 60 | 11
N. 18 | 623 | 42 | 69 | 56 | 13
C. 13 | 623 | 0 | 74 | 54 | 20
C. 17 | 519 | 3 | 73 | 52 | 21

300s, 20*10 world, 50+50+50+3 players, can adapt, random tile point, increased regen rate
G. 20 | 493 | 84 | 81 | 69 | 12
N. 5 | 386 | 62 | 74 | 54 | 20
G. 1 | 344 | 67 | 79 | 72 | 7
G. 11 | 333 | 53 | 77 | 58 | 19
G. 19 | 300 | 62 | 73 | 49 | 24
G. 37 | 300 | 60 | 77 | 57 | 20
G. 40 | 299 | 52 | 72 | 57 | 15
N. 23 | 297 | 64 | 75 | 52 | 23
N. 25 | 287 | 64 | 57 | 40 | 17
G. 3 | 287 | 42 | 70 | 49 | 21
G. 7 | 287 | 45 | 75 | 56 | 19
G. 39 | 287 | 83 | 96 | 78 | 18
N. 18 | 285 | 49 | 64 | 43 | 21
N. 44 | 274 | 42 | 76 | 53 | 23
N. 0 | 271 | 39 | 74 | 54 | 20
G. 25 | 271 | 30 | 75 | 60 | 15
G. 30 | 266 | 81 | 58 | 47 | 11
G. 41 | 265 | 67 | 63 | 48 | 15
G. 31 | 256 | 60 | 62 | 56 | 6
G. 47 | 255 | 82 | 46 | 37 | 9
G. 35 | 254 | 29 | 83 | 71 | 12
G. 18 | 253 | 57 | 77 | 59 | 18
N. 13 | 245 | 38 | 71 | 46 | 25
N. 11 | 244 | 45 | 69 | 48 | 21
C. 1 | 244 | 64 | 74 | 45 | 29
N. 31 | 240 | 30 | 67 | 41 | 26
N. 3 | 233 | 30 | 80 | 55 | 25
C. 41 | 221 | 21 | 75 | 42 | 33
C. 26 | 209 | 29 | 75 | 49 | 26
C. 31 | 209 | 9 | 61 | 40 | 21
G. 6 | 208 | 43 | 79 | 60 | 19
G. 10 | 207 | 52 | 42 | 30 | 12
G. 27 | 207 | 25 | 76 | 56 | 20
Friedman | 205 | 0 | 82 | 0 | 82
C. 43 | 199 | 21 | 71 | 40 | 31
N. 24 | 193 | 41 | 70 | 48 | 22
G. 9 | 184 | 55 | 77 | 43 | 34
C. 34 | 182 | 16 | 60 | 39 | 21
N. 45 | 181 | 0 | 62 | 36 | 26
G. 5 | 180 | 61 | 51 | 41 | 10
G. 28 | 180 | 35 | 74 | 45 | 29
G. 21 | 171 | 63 | 54 | 51 | 3
N. 43 | 168 | 42 | 54 | 32 | 22
N. 21 | 166 | 21 | 74 | 40 | 34
N. 22 | 164 | 44 | 58 | 42 | 16
G. 33 | 164 | 0 | 55 | 46 | 9
N. 6 | 163 | 44 | 77 | 58 | 19
N. 40 | 162 | 20 | 70 | 56 | 14
N. 29 | 159 | 3 | 49 | 38 | 11
N. 16 | 158 | 43 | 73 | 48 | 25
G. 4 | 156 | 62 | 3 | 3 | 0
G. 43 | 155 | 34 | 39 | 28 | 11
C. 15 | 155 | 0 | 39 | 27 | 12
C. 12 | 154 | 11 | 55 | 36 | 19
N. 1 | 153 | 34 | 56 | 37 | 19
G. 13 | 153 | 72 | 34 | 27 | 7
C. 33 | 153 | 14 | 45 | 32 | 13
N. 20 | 151 | 10 | 49 | 38 | 11
C. 45 | 150 | 35 | 81 | 38 | 43
C. 23 | 148 | 13 | 61 | 38 | 23
N. 35 | 147 | 2 | 70 | 50 | 20
G. 22 | 147 | 26 | 65 | 39 | 26
TitForTat | 141 | 0 | 77 | 0 | 77
N. 26 | 139 | 21 | 69 | 37 | 32
C. 44 | 138 | 16 | 47 | 23 | 24
C. 46 | 138 | 0 | 75 | 54 | 21
N. 34 | 137 | 35 | 56 | 38 | 18
C. 17 | 137 | 0 | 47 | 32 | 15
N. 36 | 134 | 9 | 70 | 39 | 31
G. 48 | 134 | 0 | 74 | 51 | 23
C. 30 | 132 | 18 | 70 | 34 | 36
G. 46 | 131 | 59 | 77 | 54 | 23
N. 28 | 130 | 23 | 71 | 41 | 30
C. 48 | 129 | 39 | 65 | 36 | 29
G. 16 | 128 | 74 | 44 | 14 | 30
G. 49 | 128 | 99 | 9 | 9 | 0
N. 33 | 126 | 46 | 7 | 5 | 2
N. 37 | 126 | 0 | 70 | 52 | 18
C. 0 | 126 | 7 | 41 | 26 | 15
C. 25 | 125 | 6 | 57 | 38 | 19
G. 2 | 119 | 26 | 77 | 67 | 10
C. 37 | 119 | 20 | 59 | 37 | 22
C. 20 | 117 | 13 | 66 | 32 | 34
N. 4 | 114 | 36 | 36 | 23 | 13
C. 22 | 114 | 4 | 77 | 43 | 34
C. 16 | 113 | 10 | 23 | 13 | 10
N. 30 | 112 | 8 | 63 | 45 | 18
C. 4 | 111 | 44 | 51 | 29 | 22
G. 26 | 110 | 37 | 71 | 62 | 9
N. 27 | 109 | 39 | 27 | 16 | 11
N. 7 | 107 | 14 | 68 | 44 | 24
C. 32 | 106 | 10 | 42 | 18 | 24
N. 17 | 105 | 6 | 70 | 43 | 27
G. 38 | 105 | 0 | 77 | 65 | 12
C. 5 | 105 | 15 | 57 | 35 | 22
C. 7 | 105 | 0 | 75 | 63 | 12
C. 39 | 105 | 8 | 81 | 60 | 21
G. 14 | 104 | 0 | 60 | 40 | 20
G. 0 | 102 | 40 | 62 | 41 | 21
N. 19 | 99 | 0 | 63 | 43 | 20
G. 36 | 99 | 63 | 38 | 32 | 6
N. 38 | 98 | 40 | 66 | 50 | 16
N. 46 | 95 | 4 | 59 | 40 | 19
C. 27 | 93 | 5 | 56 | 27 | 29
G. 29 | 84 | 61 | 1 | 1 | 0
C. 24 | 84 | 34 | 62 | 50 | 12
G. 45 | 82 | 13 | 70 | 48 | 22
C. 8 | 82 | 17 | 71 | 39 | 32
C. 28 | 82 | 0 | 74 | 58 | 16
C. 13 | 81 | 5 | 39 | 20 | 19
N. 15 | 80 | 20 | 64 | 28 | 36
G. 15 | 78 | 38 | 45 | 35 | 10
N. 49 | 76 | 4 | 67 | 47 | 20
G. 17 | 74 | 17 | 45 | 35 | 10
N. 41 | 73 | 0 | 59 | 36 | 23
N. 14 | 72 | 12 | 67 | 41 | 26
Joss | 71 | 0 | 48 | 0 | 48
C. 9 | 70 | 34 | 66 | 34 | 32
C. 36 | 66 | 0 | 56 | 44 | 12
N. 8 | 65 | 0 | 63 | 53 | 10
N. 10 | 64 | 6 | 71 | 53 | 18
C. 2 | 63 | 21 | 16 | 11 | 5
C. 35 | 63 | 29 | 79 | 53 | 26
C. 40 | 61 | 0 | 71 | 44 | 27
G. 12 | 59 | 63 | 1 | 1 | 0
C. 42 | 58 | 0 | 69 | 44 | 25
C. 11 | 57 | 25 | 2 | 1 | 1
C. 3 | 56 | 0 | 60 | 33 | 27
C. 18 | 56 | 0 | 38 | 28 | 10
C. 21 | 56 | 0 | 61 | 45 | 16
G. 32 | 55 | 54 | 9 | 7 | 2
N. 39 | 51 | 36 | 32 | 25 | 7
N. 2 | 49 | 0 | 37 | 29 | 8
C. 38 | 49 | 4 | 62 | 40 | 22
C. 6 | 48 | 0 | 37 | 31 | 6
C. 49 | 48 | 0 | 46 | 34 | 12
G. 44 | 46 | 61 | 19 | 16 | 3
C. 19 | 46 | 26 | 62 | 38 | 24
G. 8 | 44 | 82 | 2 | 2 | 0
N. 32 | 42 | 0 | 18 | 15 | 3
C. 38 | 49 | 4 | 62 | 40 | 22
C. 14 | 42 | 0 | 1 | 1 | 0
G. 24 | 40 | 23 | 48 | 46 | 2
C. 47 | 37 | 0 | 49 | 37 | 12
G. 42 | 34 | 0 | 64 | 52 | 12
N. 48 | 33 | 15 | 11 | 9 | 2
C. 29 | 31 | 0 | 20 | 14 | 6
C. 10 | 29 | 25 | 2 | 1 | 1
N. 42 | 28 | 51 | 1 | 1 | 0
G. 34 | 28 | 67 | 1 | 1 | 0
G. 23 | 23 | 91 | 0 | 0 | 0
N. 47 | 21 | 0 | 63 | 55 | 8
N. 12 | 14 | 8 | 33 | 26 | 7
N. 9 | 11 | 56 | 2 | 1 | 1

200s, 20*10 world, 20+20+20+3 players, can adapt, fixed points per tile, increased regen rate
G. 3 | 963 | 77 | 46 | 42 | 4
C. 12 | 963 | 11 | 49 | 44 | 5
G. 18 | 962 | 70 | 47 | 45 | 2
C. 0 | 955 | 12 | 47 | 41 | 6
N. 3 | 906 | 59 | 49 | 41 | 8
G. 16 | 906 | 41 | 47 | 42 | 5
G. 7 | 892 | 70 | 51 | 45 | 6
G. 1 | 886 | 67 | 48 | 44 | 4
G. 10 | 848 | 71 | 47 | 40 | 7
C. 15 | 835 | 29 | 49 | 45 | 4
G. 19 | 822 | 85 | 48 | 48 | 0
Friedman | 820 | 0 | 48 | 0 | 48
N. 12 | 816 | 27 | 46 | 41 | 5
N. 13 | 800 | 39 | 48 | 40 | 8
N. 15 | 800 | 34 | 46 | 42 | 4
G. 15 | 800 | 83 | 49 | 48 | 1
G. 8 | 789 | 55 | 46 | 41 | 5
C. 7 | 782 | 56 | 49 | 38 | 11
C. 14 | 781 | 23 | 48 | 39 | 9
N. 11 | 775 | 50 | 48 | 42 | 6
G. 12 | 762 | 48 | 46 | 45 | 1
G. 6 | 745 | 43 | 47 | 43 | 4
C. 18 | 734 | 14 | 47 | 33 | 14
C. 6 | 733 | 6 | 48 | 38 | 10
G. 5 | 720 | 61 | 49 | 44 | 5
C. 4 | 711 | 23 | 51 | 36 | 15
C. 10 | 706 | 12 | 48 | 38 | 10
C. 19 | 705 | 19 | 46 | 38 | 8
C. 17 | 698 | 0 | 45 | 39 | 6
N. 16 | 697 | 0 | 49 | 42 | 7
N. 19 | 671 | 46 | 49 | 46 | 3
C. 16 | 662 | 8 | 46 | 36 | 10
G. 9 | 661 | 35 | 48 | 41 | 7
N. 8 | 645 | 30 | 52 | 45 | 7
G. 2 | 617 | 53 | 50 | 47 | 3
C. 9 | 613 | 38 | 47 | 34 | 13
N. 10 | 608 | 19 | 50 | 42 | 8
N. 2 | 607 | 15 | 47 | 40 | 7
G. 13 | 607 | 36 | 47 | 46 | 1
N. 7 | 606 | 53 | 49 | 43 | 6
C. 8 | 598 | 0 | 46 | 35 | 11
N. 9 | 592 | 12 | 49 | 40 | 9
C. 3 | 592 | 21 | 51 | 39 | 12
N. 0 | 583 | 33 | 48 | 39 | 9
N. 2 | 607 | 15 | 47 | 40 | 7
G. 13 | 607 | 36 | 47 | 46 | 1
N. 7 | 606 | 53 | 49 | 43 | 6
C. 8 | 598 | 0 | 46 | 35 | 11
N. 9 | 592 | 12 | 49 | 40 | 9
C. 3 | 592 | 21 | 51 | 39 | 12
N. 0 | 583 | 33 | 48 | 39 | 9
N. 5 | 582 | 13 | 48 | 42 | 6
G. 14 | 580 | 52 | 46 | 44 | 2
C. 1 | 571 | 9 | 47 | 31 | 16
Joss | 557 | 0 | 48 | 0 | 48
G. 0 | 556 | 56 | 46 | 39 | 7
TitForTat | 556 | 0 | 46 | 0 | 46
N. 6 | 548 | 45 | 50 | 44 | 6
C. 2 | 548 | 34 | 50 | 38 | 12
C. 5 | 535 | 12 | 48 | 35 | 13
C. 13 | 521 | 8 | 46 | 36 | 10
N. 18 | 510 | 41 | 45 | 38 | 7
C. 11 | 507 | 6 | 47 | 41 | 6
N. 1 | 486 | 8 | 49 | 38 | 11

120s, 20*10 world, 20+20+20+3, can adapt, random tile points, increased regen rate
Name | Score | Self. | Cont. | Takes | Shares
G. 6 | 733 | 79 | 28 | 28 | 0
G. 7 | 715 | 72 | 29 | 28 | 1
G. 2 | 686 | 86 | 30 | 26 | 4
Friedman | 676 | 0 | 27 | 11 | 16
N. 18 | 606 | 57 | 27 | 24 | 3
G. 9 | 601 | 65 | 28 | 23 | 5
N. 9 | 588 | 42 | 28 | 22 | 6
G. 0 | 565 | 61 | 28 | 25 | 3
TitForTat | 556 | 0 | 30 | 1 | 29
G. 18 | 551 | 75 | 31 | 28 | 3
N. 10 | 540 | 56 | 30 | 28 | 2
N. 6 | 537 | 39 | 28 | 26 | 2
C. 17 | 533 | 6 | 29 | 21 | 8
N. 8 | 532 | 26 | 28 | 24 | 4
N. 5 | 522 | 39 | 29 | 24 | 5
G. 8 | 522 | 75 | 29 | 28 | 1
G. 4 | 515 | 87 | 29 | 26 | 3
C. 18 | 515 | 31 | 28 | 19 | 9
G. 10 | 507 | 59 | 29 | 26 | 3
C. 8 | 507 | 18 | 27 | 24 | 3
G. 1 | 497 | 63 | 28 | 25 | 3
N. 15 | 475 | 50 | 28 | 23 | 5
N. 7 | 467 | 53 | 28 | 26 | 2
N. 2 | 464 | 53 | 29 | 25 | 4
C. 14 | 454 | 3 | 27 | 23 | 4
G. 3 | 443 | 63 | 30 | 27 | 3
C. 12 | 442 | 0 | 26 | 22 | 4
G. 11 | 438 | 66 | 28 | 26 | 2
G. 17 | 435 | 53 | 27 | 27 | 0
G. 14 | 429 | 85 | 27 | 25 | 2
N. 11 | 423 | 52 | 32 | 21 | 11
N. 19 | 421 | 37 | 28 | 26 | 2
G. 15 | 416 | 85 | 29 | 26 | 3
C. 1 | 414 | 2 | 29 | 24 | 5
N. 12 | 413 | 56 | 30 | 26 | 4
C. 6 | 411 | 10 | 27 | 22 | 5
N. 1 | 408 | 44 | 28 | 27 | 1
N. 17 | 403 | 43 | 27 | 23 | 4
N. 16 | 401 | 44 | 29 | 25 | 4
C. 10 | 390 | 35 | 27 | 23 | 4
N. 13 | 389 | 35 | 27 | 25 | 2
G. 16 | 383 | 85 | 26 | 23 | 3
C. 11 | 381 | 21 | 28 | 25 | 3
N. 3 | 373 | 15 | 29 | 26 | 3
Joss | 370 | 0 | 26 | 4 | 22
N. 4 | 365 | 46 | 29 | 20 | 9
G. 19 | 358 | 83 | 27 | 25 | 2
C. 9 | 357 | 27 | 29 | 16 | 13
C. 13 | 356 | 22 | 26 | 21 | 5
C. 3 | 351 | 23 | 29 | 21 | 8
G. 12 | 347 | 77 | 28 | 26 | 2
N. 14 | 340 | 49 | 27 | 21 | 6
G. 13 | 321 | 45 | 29 | 24 | 5
C. 16 | 309 | 0 | 28 | 25 | 3
C. 7 | 304 | 20 | 28 | 17 | 11
C. 2 | 303 | 15 | 29 | 20 | 9
C. 15 | 297 | 23 | 32 | 20 | 12
C. 5 | 285 | 5 | 26 | 26 | 0
C. 19 | 281 | 18 | 27 | 20 | 7
C. 0 | 274 | 0 | 27 | 23 | 4
C. 4 | 271 | 16 | 29 | 19 | 10
G. 5 | 256 | 64 | 28 | 25 | 3
N. 0 | 239 | 27 | 29 | 27 | 2

120s, 20*10 world, 20+20+20+3 players, can adapt, random tile points, increased regen rate
Name | Score | Self. | Cont. | Takes | Shares
C. 1 | 608 | 5 | 30 | 24 | 6
N. 14 | 604 | 46 | 27 | 22 | 5
N. 0 | 599 | 55 | 30 | 22 | 8
G. 5 | 592 | 87 | 26 | 19 | 7
C. 13 | 572 | 1 | 27 | 25 | 2
C. 9 | 544 | 28 | 27 | 21 | 6
N. 9 | 537 | 44 | 27 | 21 | 6
G. 18 | 537 | 91 | 31 | 24 | 7
N. 11 | 527 | 73 | 27 | 18 | 9
N. 5 | 504 | 68 | 29 | 22 | 7
G. 19 | 501 | 96 | 28 | 20 | 8
C. 16 | 482 | 31 | 28 | 22 | 6
C. 8 | 479 | 30 | 31 | 21 | 10
C. 2 | 477 | 24 | 31 | 24 | 7
G. 4 | 471 | 100 | 29 | 20 | 9
C. 12 | 461 | 7 | 29 | 20 | 9
G. 15 | 458 | 85 | 29 | 23 | 6
G. 8 | 457 | 99 | 27 | 21 | 6
N. 10 | 454 | 69 | 28 | 18 | 10
G. 14 | 444 | 100 | 28 | 22 | 6
G. 16 | 444 | 100 | 28 | 20 | 8
Joss | 442 | 0 | 28 | 3 | 25
G. 1 | 440 | 100 | 31 | 19 | 12
G. 3 | 439 | 89 | 27 | 15 | 12
G. 2 | 424 | 63 | 30 | 22 | 8
G. 12 | 423 | 76 | 26 | 18 | 8
G. 9 | 421 | 100 | 27 | 21 | 6
G. 6 | 418 | 96 | 25 | 16 | 9
C. 10 | 411 | 6 | 29 | 25 | 4
N. 13 | 405 | 53 | 31 | 18 | 13
N. 8 | 404 | 50 | 29 | 22 | 7
N. 18 | 401 | 55 | 27 | 19 | 8
N. 16 | 396 | 66 | 29 | 22 | 7
C. 17 | 395 | 30 | 31 | 21 | 10
G. 0 | 393 | 100 | 32 | 22 | 10
N. 4 | 389 | 47 | 28 | 21 | 7
G. 7 | 381 | 92 | 23 | 16 | 7
N. 1 | 378 | 41 | 28 | 21 | 7
N. 17 | 377 | 59 | 29 | 17 | 12
C. 6 | 373 | 5 | 30 | 24 | 6
C. 14 | 368 | 37 | 33 | 17 | 16
Friedman | 358 | 0 | 29 | 8 | 21
G. 17 | 356 | 100 | 29 | 21 | 8
C. 18 | 356 | 25 | 26 | 20 | 6
N. 1 | 378 | 41 | 28 | 21 | 7
N. 17 | 377 | 59 | 29 | 17 | 12
C. 6 | 373 | 5 | 30 | 24 | 6
C. 14 | 368 | 37 | 33 | 17 | 16
Friedman | 358 | 0 | 29 | 8 | 21
G. 17 | 356 | 100 | 29 | 21 | 8
C. 18 | 356 | 25 | 26 | 20 | 6
C. 11 | 355 | 11 | 28 | 21 | 7
G. 10 | 353 | 100 | 31 | 14 | 17
C. 7 | 351 | 14 | 29 | 16 | 13
N. 2 | 335 | 75 | 27 | 18 | 9
C. 4 | 322 | 36 | 34 | 26 | 8
C. 15 | 322 | 36 | 27 | 23 | 4
C. 3 | 314 | 23 | 27 | 22 | 5
C. 5 | 314 | 36 | 29 | 18 | 11
N. 15 | 304 | 29 | 30 | 24 | 6
N. 19 | 295 | 58 | 28 | 17 | 11
G. 11 | 293 | 74 | 30 | 18 | 12
C. 0 | 293 | 33 | 28 | 19 | 9
N. 12 | 255 | 70 | 29 | 20 | 9
TitForTat | 246 | 0 | 27 | 2 | 25
N. 7 | 236 | 67 | 27 | 11 | 16
N. 6 | 217 | 55 | 29 | 17 | 12
N. 3 | 214 | 51 | 24 | 18 | 6
C. 19 | 190 | 10 | 28 | 17 | 11
G. 13 | 185 | 100 | 30 | 10 | 20