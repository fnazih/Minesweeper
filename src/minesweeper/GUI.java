 /**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package minesweeper;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import serverclient.*;
import tools.*;

//This class represents the Graphical User Interface. It creates every panel that are going to be displayed,
//and deals with the user's actions (clicks...)
public class GUI extends JPanel implements ActionListener {
    private static Demineur demineur;
    private final JPanel gridContainer;
    public JLabel minesRestantes;   //label displaying the number of non-found mines
    public Cases[][] tabCases;
    private final Counter counter;
    public JLabel score;
    private JPanel bottomPanel;

    //Menu parameters
    private final static JMenuBar menuBar = new JMenuBar();
    private final static JMenu menu = new JMenu("Options");
    private final static JMenuItem instructions = new JMenuItem("Instructions");
    private final static JMenuItem quitMenu = new JMenuItem("Quit", KeyEvent.VK_Q);
    private final static JMenu changeLevel = new JMenu("Change level");   //a sub-menu
    private final static JMenuItem easyLevel = new JMenuItem("EASY");
    private final static JMenuItem mediumLevel = new JMenuItem("MEDIUM");
    private final static JMenuItem hardLevel = new JMenuItem("HARD");
    private final static JMenuItem customLevel = new JMenuItem("CUSTOM");

    //serverclient.Client parameters
    private JLabel ipAddLabel = new JLabel("IP Address : ");
    private JTextField ipAddText = new JTextField();
    private JLabel portLabel = new JLabel("Port : ");
    private JTextField portText = new JTextField();
    private JButton playButt = new JButton("Play !");
    private JLabel playerr = new JLabel("Player : ");
    private String playerpseudo = "";

    //Default constructor : creates every panel and initializes them
    public GUI(Demineur newDemineur) {
        super(new BorderLayout());

        demineur = newDemineur;
        counter = new Counter();

        demineur.getActualChamp().createChamp(Champ.easyLevel[0],
                Champ.easyLevel[1],
                Champ.easyLevel[2]);

        demineur.setTabSize();
        createMenu();
        gridContainer = new JPanel();

        playerpseudo = JOptionPane.showInputDialog(null,
                "Welcome to the Minesweeper ! What is your name ?",
                "Welcome !",
                JOptionPane.PLAIN_MESSAGE);

        playerr.setText("Player : " + playerpseudo);
        JPanel middlePanel = createMiddlePanel();
        bottomPanel = createBottomPanel();
        JPanel topPanel = createTopPanel(playerpseudo);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    //This function creates the menu
    private void createMenu() {
        menuBar.add(menu);
        menu.add(quitMenu);
        menu.add(instructions);
        menuBar.add(changeLevel);

        quitMenu.addActionListener(this);  //ajout de l'ecoute de la souris aux boutons
        easyLevel.addActionListener(this);
        mediumLevel.addActionListener(this);
        hardLevel.addActionListener(this);
        customLevel.addActionListener(this);

        changeLevel.add(easyLevel); //ajout des items du sous-menu
        changeLevel.add(mediumLevel);
        changeLevel.add(hardLevel);
        changeLevel.add(customLevel);

        menu.setVisible(true);

        demineur.setJMenuBar(menuBar);
    }

    //This function creates the top panel
    private JPanel createTopPanel(String name) {
        JPanel topPanel = new JPanel();

        JLabel title = new JLabel("Démineur");
        JLabel level = new JLabel("Level : " + demineur.getActualChamp().getLevel());

        topPanel.add(title);
        topPanel.add(playerr);
        topPanel.add(level);
        topPanel.setLayout(new GridLayout(1, 4));
        topPanel.setPreferredSize(new Dimension(400, 50));

        return topPanel;
    }

    //This function creates the interface grid
    public void createGrid() {
        GridLayout layout = new GridLayout(demineur.getActualChamp().dimX, demineur.getActualChamp().dimY);

        layout.setHgap(2);
        layout.setVgap(2);

        int dimX = demineur.getActualChamp().dimX;
        int dimY = demineur.getActualChamp().dimY;

        tabCases = new Cases[dimX][dimY];

        for(int i = 0; i < dimX; i++) {
            for(int j = 0; j < dimY; j++) {
                tabCases[i][j] = new Cases(i, j, demineur, counter);
                tabCases[i][j].repaint();
                gridContainer.add(tabCases[i][j]);
            }
        }

        demineur.getActualChamp().affChamp();
        gridContainer.setLayout(layout);
    }

    //This function resets the game grid according to the previous level
    public void reset(Niveau.Level niveau) {
        gridContainer.removeAll();
        counter.resetCount();

        switch (niveau) {
            case EASY -> {
                demineur.getActualChamp().createChamp(Champ.easyLevel[0],
                        Champ.easyLevel[1],
                        Champ.easyLevel[2]);
                createGrid();
                demineur.setTabSize();
                repaint();
            }

            case MEDIUM -> {
                demineur.getActualChamp().createChamp(Champ.mediumLevel[0],
                        Champ.mediumLevel[1],
                        Champ.mediumLevel[2]);
                createGrid();
                demineur.setTabSize();
                repaint();
            }

            case HARD -> {
                demineur.getActualChamp().createChamp(Champ.hardLevel[0],
                        Champ.hardLevel[1],
                        Champ.hardLevel[2]);
                createGrid();
                demineur.setTabSize();
                repaint();
            }

            case CUSTOM -> {
                demineur.getActualChamp().createChamp(Niveau.customDimX,
                        Niveau.customDimY,
                        Niveau.customNbMines);
                createGrid();
                demineur.setTabSize();
                repaint();
            }

            default -> throw new IllegalStateException("Unexpected value : " + niveau);
        }

        for(int i = 0; i < demineur.getActualChamp().dimX; i++) {
            for(int j = 0; j < demineur.getActualChamp().dimY; j++) {
                tabCases[i][j].repaint();
            }
        }
    }

    //This function initializes the center panel with the game grid
    private JPanel createMiddlePanel() {
        JPanel middlePanell = new JPanel();
        createGrid();
        middlePanell.add(gridContainer);

        return middlePanell;
    }

    //This function initializes the bottom panel
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();

        bottomPanel.setLayout(new GridLayout(2, 4));

        int nbMinesTotal = 0;

        switch (demineur.getActualChamp().getLevel()) {
            case EASY -> nbMinesTotal = Champ.easyLevel[2];
            case MEDIUM -> nbMinesTotal = Champ.mediumLevel[2];
            case HARD -> nbMinesTotal = Champ.hardLevel[2];
            case CUSTOM -> nbMinesTotal = Niveau.customNbMines;
        }

        bottomPanel.add(ipAddLabel);
        bottomPanel.add(ipAddText);
        bottomPanel.add(portLabel);
        bottomPanel.add(portText);

        minesRestantes = new JLabel("Mines left : " + nbMinesTotal);
        bottomPanel.add(minesRestantes);
        bottomPanel.add(counter);


        score = new JLabel();
        score.setText("Score : " + tabCases[0][0].getScore());

        bottomPanel.add(score);

        playButt.addActionListener(this);
        bottomPanel.add(playButt);

        return bottomPanel;
    }

    //This function recalculates the number of find mines to refresh the panel
    public int recalculNbMinesFound() { return tabCases[0][0].getNbMinesFound(); }

    //This function deals with the differents user's interactions with the game interface
    @Override
    public void actionPerformed(ActionEvent ev) {
        Object source = ev.getSource();

        if(quitMenu.equals(source)) {
            int reponse = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to quit ?",
                    "Quit game", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (reponse == JOptionPane.YES_OPTION) {
                counter.resetCount();
                System.exit(0);
            }
        }
        else if(easyLevel.equals(source)) {
            reset(Niveau.Level.EASY);
        }
        else if(mediumLevel.equals(source)) {
            reset(Niveau.Level.MEDIUM);
        }
        else if(hardLevel.equals(source)) {
            reset(Niveau.Level.HARD);
        }
        else if(customLevel.equals(source)) {
            reset(Niveau.Level.CUSTOM);
        }
        else if(playButt.equals(source)) {
            demineur.setClient(new Client(ipAddText.getText(), portText.getText(), getPlayerName().getText(), demineur));
        }
    }

    public JLabel getPlayerName() { return playerr; }

    public void disableButtons() {
        ipAddText.setEnabled(false);
        portText.setEnabled(false);
        playButt.setEnabled(false);
    }

    public void disableOnlineDisplay() {
        //pseudo.setEnabled(true);
        portText.setEnabled(true);
        ipAddText.setEnabled(true);
        playButt.setEnabled(true);
    }

    public Cases[][] getTabCases() { return tabCases; }

    public String getPlayerpseudo() { return playerpseudo; }
}