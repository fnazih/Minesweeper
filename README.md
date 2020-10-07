# Minesweeper game
This is an individual school project in Java where I had to code the Minesweeper game. This file explains how to run the game.

### Intro
To run the game, you just open the project on a Java IDE (IntelliJ IDEA for example), and run the "Demineur.java" file as the main class.
A pop up tab appears, asking for your name. Your can enter your name, and then press enter.

### How to start
The default game level is EASY, an 8x8 cell grid. You can change level in the menu bar, by clicking the "Change level" menu and then choosing a new level.
To play online (with a local server + multiple clients), you need to use two configurations on your IDE :
  - The "Server" configuration where the main class is serverclient.GUIServer
  - The "Client" configuration where the main class is minesweeper.Demineur

To run the app as a server, run the "Server" configuration on your IDE, and choose the listening port in the opened panel. You will see your server IP address in the panel.
To run the app as a client and play the game, run the "Client" configuration. A pop-up tab asks for your name, and then the principal panel pops up. You will need to enter the server IP address and the connecting port (available in the server panel). You can then press the "Join" button and start playing.

### How to play
Left-click a cell to reveal it.
Right-click a cell to flag it.

As soon as you click on the first cell (left or right click), the counter starts, counting the time you spend on the game. Your score (= the number of revealed empty cells) is displayed on the bottom of the panel.
If you reveal a mined cell, a pop up tab appears; telling you that you lost and giving you your final score. You can choose to leave the game, or retry.
