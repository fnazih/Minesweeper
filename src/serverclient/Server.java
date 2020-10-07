 /**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package serverclient;


import java.io.*;
import java.net.*;
import java.util.HashSet;
import minesweeper.*;
import tools.*;

/*
This class represents the server that is going to create the socket and allow the client threads to connect
to the game
 */
public class Server extends Thread implements Runnable {
    private ServerSocket serverSocket;
    private serverclient.GUIServer GUIServer;
    private int clientID = 0;
    private HashSet<ClientThread> clientList = new HashSet<>();
    private Champ champ;
    private boolean[][] clicked;

    Server(int port, GUIServer NEWGUIServer) {
        this.GUIServer = NEWGUIServer;

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!(GUIServer.GameHasStarted())) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                ClientThread clientThread = new ClientThread(socket, in, out, clientID, this);
                out.writeInt(clientID); //send client ID to the client
                clientID++;
                clientList.add(clientThread);
                GUIServer.getExplainText().append(clientThread.getPlayerName() + " is connected  with ID = " + clientID + "\n");

                for(ClientThread clientT : clientList) {
                    broadcastMessage("     " + clientT.getPlayerName() + "\n");
                }
                clientThread.start();
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSockerServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void startGame(Niveau.Level level) {
        for (ClientThread clientThread : clientList) {
            clientThread.startGame();
        }

        int[] levelParam = new int[3];

        switch (level) {
            case EASY :
                levelParam[0] = Champ.easyLevel[0];
                levelParam[1] = Champ.easyLevel[1];
                levelParam[2] = Champ.easyLevel[2];
                break;
            case MEDIUM :
                levelParam[0] = Champ.mediumLevel[0];
                levelParam[1] = Champ.mediumLevel[1];
                levelParam[2] = Champ.mediumLevel[2];
                break;
            case HARD :
                levelParam[0] = Champ.hardLevel[0];
                levelParam[1] = Champ.hardLevel[1];
                levelParam[2] = Champ.hardLevel[2];
                break;
            case CUSTOM :
                levelParam[0] = Niveau.customDimX;
                levelParam[1] = Niveau.customDimY;
                levelParam[2] = Niveau.customNbMines;
                break;
        }

        this.champ.createChamp(levelParam[0], levelParam[1], levelParam[2]);
        this.clicked = new boolean[champ.getChamp().length][champ.getChamp()[0].length];

        broadcastMessage("start" + " " + level);
    }

    void broadcastMessage(String msg) {
        for (ClientThread clientThread : clientList) {
            try {
                clientThread.getOutput().writeUTF(msg);
            } catch (IOException | NullPointerException ignored) {
            }
        }
    }

    public HashSet<ClientThread> getClientList() { return clientList; }

    public void pauseGame() {
        GUIServer.getExplainText().append("Game has been paused\n");
        broadcastMessage("pause" + " ");
        for (ClientThread clientThread : clientList) {
            clientThread.setDisabled(true);
        }
    }

    public void resumeGame() {
        GUIServer.getExplainText().append("Game has been resumed\n");
        broadcastMessage("resume" + " " );
        for (ClientThread clientThread : clientList) {
            clientThread.setDisabled(false);
        }
    }

    public void stopGame() {
        int playerScore;
        broadcastMessage("Game is over ! Here are the overall scores:\n");
        for (ClientThread clientThread : clientList) {
            playerScore = clientThread.getOverallScore();
            broadcastMessage("     " + clientThread.getPlayerName() + " : " + playerScore + "\n");
            GUIServer.getExplainText().append("     " + clientThread.getPlayerName() + " : " + playerScore + "\n");
        }
        broadcastMessage("The server will be closed shortly. Thanks for playing!\n");
        closeServer();
    }

    public void forceCellRepaint(int x, int y, ClientThread clientThread, boolean isMine) {
        if (isMine) {
            broadcastMessage("eliminated" + " " + clientThread.getPlayerName() + " " + x + " " + y + " " + String.valueOf(clientThread.getScore()) + " " + clientThread.getClientID());
        } else {
            int minesAround = champ.calculNbMinesAutour(x, y);
            if (minesAround == 0) {
                broadcastMessage("clicked" + " " + 0 + " " + x + " " + y);
            } else {
                broadcastMessage("clicked" + " " + minesAround + " " + x + " " + y);
            }
        }
    }

    private void closeServer() {
        clientList.clear();
        System.exit(0);
    }

    public boolean[][] getClicked() { return clicked; }

    public Champ getChamp() { return champ; }
}
