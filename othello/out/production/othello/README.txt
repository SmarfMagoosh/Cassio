The only files you really need to look at for your project are:
AIPlayer.java
othello.Board.java

In addition, othello.GreedyPlayer.java and othello.RandomPlayer.java might offer some insight into how to write your player.

Other than the files listed above, everything else is just implementation details that are described in the project instructions or are not important for your project. However, for those who are curious, the rest of this file gives a brief description of all the classes in this package. They can be organized into categories:

1. Relating to Players

othello.Player is an interface that all Othello players must implement.

AIPlayer extends othello.Player with some additional functionality that your AI players should support (e.g., minimax computation).

othello.GreedyPlayer, othello.RandomPlayer, HumanPlayer, and HumanGUIPlayer are specific implementations of Players. You can look at Greedy and Random to get some ideas for your player.


2. Relating to the othello.Board

othello.Board is an interface that an Othello board must implement. ALL of your interactions with the othello.Board should use methods listed in this interface.

othello.BoardImplementation is the othello.Board implementation we will be using.


3. Classes with main methods

OthelloGUI and Tournament provide two different ways to run an Othello game: with a GUI, and at the console (with move time limits), respectively.



4. Classes for the GUI

PlayerPanel, othello.BoardPanel, OthelloThread, and OthelloFrame each provide part of the GUI functionality.


5. Others

The Misc, othello.IllegalCellException, and othello.IllegalMoveException are the leftovers that didn't fit elsewhere.

