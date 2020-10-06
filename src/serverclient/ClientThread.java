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

            }
        } catch (NullPointerException ignored) {
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
}
