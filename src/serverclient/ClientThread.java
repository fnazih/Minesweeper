/**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */


package serverclient;


import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class ClientThread extends Thread implements Runnable {
    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;
    private int clientID;
    private String playerName;
    private Server server;
    private Color playerColor = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    private int personalScore = 0;
    private int overallScore = 0;
    private boolean disabled = true;
    private boolean wantsToReplay;
    private boolean hasLeft = false;

    ClientThread(Socket newSock, DataInputStream input, DataOutputStream output, int newClientID, Server newServer) throws IOException {
        this.sock = newSock;
        this.in = input;
        this.out = output;
        this.clientID = newClientID;
        this.server = newServer;
        this.playerName = in.readUTF();
    }

    @Override
    public void run() {
        try {
            while ((this != null)) {
                String instruction = in.readUTF();
                String[] arrayInstructions = instruction.split("\\s+");
                switch (arrayInstructions[0]) {
                    case "click" :
                        int x = Integer.parseInt(arrayInstructions[1]);
                        int y = Integer.parseInt(arrayInstructions[2]);
                        if(!server.getClicked()[x][y] && !disabled) {
                            boolean isMine = server.getChamp().getChamp()[x][y];
                            if(!isMine) {
                                personalScore++;
                            }
                            else {
                                setDisabled(true);
                            }
                            server.getClicked()[x][y] = true;
                            server.forceCellRepaint(x, y, this, isMine);
                        }
                }
            }
            } catch (NullPointerException | IOException ignored) {
        }
    }

    private void stopThread() {
        try {
            in.close();
            out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getClientID() { return clientID; }

    public String getPlayerName() { return playerName; }

    public Color getPlayerColor() { return playerColor; }

    public int getPrivateScore() { return personalScore; }

    public int getOverallScore() { return overallScore; }

    public void setDisabled(boolean disable) { this.disabled = disable; }

    public boolean replays() { return wantsToReplay; }

    public boolean HasLeft() { return hasLeft; }

    public void startGame() {
        disabled = false;
    }

    public DataOutputStream getOutput() { return out; }

    public int getScore() { return overallScore; }
}
