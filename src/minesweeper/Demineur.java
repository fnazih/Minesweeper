 /**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package minesweeper;


import javax.swing.*;
import java.awt.*;
import serverclient.*;
import tools.*;

//This class represents the full interface JFrame, it regroups all of the game parameters and displays them
public class Demineur extends JFrame {
    private final Champ actualChamp;  //current game field variable
    private final GUI gui;        //graphical user interface variable
    private Client client;

    //This function returns the current field created
    public Champ getActualChamp() { return actualChamp; }

    //Default constructor : creates the field, the minesweeper.GUI and sets the tab size
    public Demineur() {
        super("Deminer");  //calls the JFrame constructor with the "Deminer" label
        actualChamp = new Champ();  //creation of the default level field : EASY

        gui = new GUI(this);
        setContentPane(gui);

        gui.setPreferredSize(new Dimension(350, 380));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        setVisible(true);
    }

    //This function adapts the tab size to the current level
    public void setTabSize() {  //function that adapts the window size to the level
        if(getActualChamp().getLevel() == Niveau.Level.EASY) {
            setSize(new Dimension(350, 380));
        }
        if(getActualChamp().getLevel() == Niveau.Level.MEDIUM) {
            setSize(new Dimension(460, 480));
        }
        if(getActualChamp().getLevel() == Niveau.Level.HARD) {
            setSize(new Dimension(590, 610));
        }
        if(getActualChamp().getLevel() == Niveau.Level.CUSTOM) {
            setSize(new Dimension(490, 500));
        }
    }

    public GUI getGUI() { return gui; }

    //The main function only calls the minesweeper.Demineur's constructor; everything gets created after that
    public static void main(String[] args) { new Demineur(); }

    public void setClient(Client newClient) { this.client = newClient; }

    public Client getClient() { return client; }
}
