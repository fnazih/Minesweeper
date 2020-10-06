 /**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package serverclient;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class GUIServer extends JFrame implements ActionListener {
    //Different elements displayed in the server panel
    private final JLabel portLabel = new JLabel("Port : ");
    private JTextField portText = new JTextField();
    private final JButton launch = new JButton("Launch");
    private final JButton stop = new JButton("Stop");
    private JTextArea explainText = new JTextArea("Press 'Launch' to create thread and start accepting clients\n");
    private JPanel footer = new JPanel();
    JPanel centerPanel = new JPanel();

    //Inner attributes
    private Server server;
    private boolean gameHasStarted;

    GUIServer()  {
        super("MineSweeper Server GUI");
        setLayout(new BorderLayout());

        explainText.setEditable(false);
        gameHasStarted = false;

        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);
        setPreferredSize(new Dimension(400, 500));

        pack();
    }

    private JPanel createCenterPanel() {
        centerPanel.add(explainText);

        return centerPanel;
    }

    private JPanel createFooter() {
        footer.setLayout(new FlowLayout());

        footer.add(portLabel);
        portText.setPreferredSize(new Dimension(100, 20));
        footer.add(portText);
        footer.add(launch);
        launch.addActionListener(this);
        footer.add(stop);
        stop.addActionListener(this);
        stop.setEnabled(false);
        return footer;
    }

    public static void main(String[] args) {
        GUIServer GUIServer = new GUIServer();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(launch.equals(source) && launch.getText().equals("Launch")) {
            int portNumber = Integer.parseInt(portText.getText());
            String ipAddress = null;
            try {
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            launch.setText("Start game");
            explainText.append("Server started.\nIP address: " + ipAddress + ":" + portNumber + "\n");
            pack();
            server = new Server(portNumber, this);
            server.start();
        }
        else if(launch.equals(source) && launch.getText().equals("Start game")) {
            gameHasStarted = true;
            launch.setText("Pause game");
            stop.setEnabled(true);
            portText.setEnabled(false);
            server.stopSockerServer();
            explainText.append("The game is about to start. No more clients accepted, " + server.getClientList().size() +  " clients connected\n");
        }
        else if (e.getSource() == launch && launch.getText().equals("Pause game")) {
            server.pauseGame();
            launch.setText("Resume game");
        }
        else if (e.getSource() == launch && launch.getText().equals("Resume game")) {
            server.resumeGame();
            launch.setText("Pause game");
        }
        else if (e.getSource() == stop) {
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure?",
                    "Quit",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) {
                gameHasStarted = false;
                server.stopGame();
            }
        }
    }

    public boolean GameHasStarted() { return gameHasStarted; }

    public JTextArea getExplainText() { return explainText; }
}
