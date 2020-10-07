 /**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package serverclient;


import javax.swing.*;
import java.io.*;
import java.net.Socket;
import minesweeper.*;
import tools.*;

public class Client extends Thread {

    private int playerID;
    private String playerName;
    private Socket sock;
    private DataOutputStream output;
    private DataInputStream input;
    private Demineur demineur;
    private boolean started;
    private boolean replaying;

    public Client(String ipAddress, String port, String playerName, Demineur newDemineur) {
        try {
            sock = new Socket(ipAddress, Integer.parseInt(port));

            output = new DataOutputStream(sock.getOutputStream());
            input = new DataInputStream(sock.getInputStream());

            if (demineur.getGUI().getPlayerpseudo() == "") {
                this.playerName = "Unknown";
                demineur.getGUI().getPlayerName().setText(playerName);
            } else {
                this.playerName = playerName;
                demineur.getGUI().getPlayerName().setText("Player : " + playerName);
            }
            this.demineur = newDemineur;
            this.start();
        } catch (IOException e) {
            System.out.println("(IP Address):(Port) (" + ipAddress + "):(" + port + ") is unreachable");
            JOptionPane.showConfirmDialog(
                    null,
                    "(IP Address):(Port) (" + ipAddress + "):(" + port + ") is unreachable",
                    "Error",
                    JOptionPane.DEFAULT_OPTION
            );
        }
    }

    @Override
    public void run() {
        try {
            output.writeUTF(getPlayerName());   //first message sent : player name
            playerID = input.readInt();     //first message received : the player ID
            demineur.getGUI().disableButtons();

            while (this != null) {
                String instruction = input.readUTF();
                String[] arrayInstruction = instruction.split("\\s+");
                String playerScore;
                int x, y, nbMines, playerID;
                switch (arrayInstruction[0]) {
                    case "start":
                        replaying = false;
                        started = true;
                        String difficulty = arrayInstruction[1];
                        demineur.getActualChamp().createChamp(getLevelParam(Niveau.Level.valueOf(difficulty))[0],
                                getLevelParam(Niveau.Level.valueOf(difficulty))[1],
                                getLevelParam(Niveau.Level.valueOf(difficulty))[2]);
                        demineur.getGUI().createGrid();
                        break;
                    case "eliminated":
                        String playerName = arrayInstruction[1];
                        x = Integer.parseInt(arrayInstruction[2]);
                        y = Integer.parseInt(arrayInstruction[3]);
                        playerScore = arrayInstruction[4];
                        playerID = Integer.parseInt(arrayInstruction[5]);
                        demineur.getGUI().getTabCases()[x][y].clientRepaint(true, 0);
                        if (playerID == demineur.getClient().getPlayerID()) {
                            if (JOptionPane.showConfirmDialog(
                                    null,
                                    "Your final score is " + playerScore + " points.\nDo you want to replay ?",
                                    "Game over !",
                                    JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.NO_OPTION) {
                                output.writeUTF("new false");
                                this.close();
                            } else {
                                output.writeUTF("new true");
                                replaying = true;
                            }
                        }
                        break;
                    case "clicked":
                        nbMines = Integer.parseInt(arrayInstruction[1]);
                        x = Integer.parseInt(arrayInstruction[2]);
                        y = Integer.parseInt(arrayInstruction[3]);
                        demineur.getGUI().getTabCases()[x][y].clientRepaint(false, nbMines);
                        break;
                    case "pause":
                        JOptionPane.showConfirmDialog(
                                null,
                                "Game has been paused by an administrator",
                                "Pause",
                                JOptionPane.DEFAULT_OPTION
                        );
                        break;
                    case "end":
                        if (!replaying) {
                            if (JOptionPane.showConfirmDialog(
                                    null,
                                    "Game is over. Do you want to play again ?",
                                    "Game is over !",
                                    JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.YES_OPTION) {
                                output.writeUTF("new true");
                                replaying = true;
                            }
                        }
                        break;
                    default:

                }
            }

        } catch (IOException e) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "You have lost your connection with the server! Do you want to play in solo mode ?",
                    "Connexion lost !",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.close();
            }
        }
    }

    private void close() {
        try {
            input.close();
            output.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        demineur.setClient(null);
        demineur.getActualChamp().createChamp(Champ.easyLevel[0], Champ.easyLevel[1], Champ.easyLevel[2]);
        demineur.getGUI().disableOnlineDisplay();
    }

    public int getPlayerID() { return playerID; }

    public String getPlayerName() { return playerName; }

    public DataOutputStream getOutput() { return output; }

    public boolean isStarted() { return started; }

    public int[] getLevelParam(Niveau.Level level) {
        int[] levelParam = new int[3];

        switch (level) {
            case EASY :
                for(int i = 0; i < 3; i++) {
                    levelParam[i] = Champ.easyLevel[i];
                }
                break;
            case MEDIUM :
                for(int i = 0; i < 3; i++) {
                    levelParam[i] = Champ.mediumLevel[i];
                }
                break;
            case HARD :
                for(int i = 0; i < 3; i++) {
                    levelParam[i] = Champ.hardLevel[i];
                }
                break;
            case CUSTOM :
                levelParam[0] = Niveau.customDimX;
                levelParam[1] = Niveau.customDimY;
                levelParam[2] = Niveau.customNbMines;
                break;
        }

        return levelParam;
    }
}