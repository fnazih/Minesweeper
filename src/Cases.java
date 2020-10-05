/*
    @author : Fatima-Zohra NAZIH
    @title : Deminer
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import static java.lang.Integer.*;

//This class represents one displayed cell on the field and implements the MouseListener in order to change according
//to the user's actions (left-click, right-click...)
public class Cases extends JPanel implements MouseListener {
    private static Demineur demineur;
    private boolean content; //indicates whether there is a mine (true) or not (false)
    private String text = "";
    private final JLabel nbMinesAround;
    private final static int DIMCASE = 25;
    private final static int HIDDEN = 0;
    private final static int REVEALED = 1;
    private final static int FLAGGED = 2;
    private int situation;
    private final int x;   // (x, y) parameters that indicate the position in the grid
    private final int y;
    private static int nbMinesFound;
    private final Counter counter;
    private static int score;

    //Default constructor : sets the different cell parameters
    public Cases(int i, int j, Demineur newDemineur, Counter newCounter) {
        demineur = newDemineur;
        counter = newCounter;
        x = i;
        y = j;
        score = 0;
        setSituation(HIDDEN);   //initially hidden
        nbMinesFound = 0;

        setPreferredSize(new Dimension(DIMCASE, DIMCASE));
        setContent(demineur.getActualChamp().champ[x][y]);  //init : empty or with mine
        setText();

        nbMinesAround = new JLabel(text);
        add(nbMinesAround);
        nbMinesAround.setVisible(false);

        addMouseListener(this);
    }

    //This function writes the appropriate text that will appear in the case
    public void setText() {
        if(getContent()) {
            text = "M";
        }
        else if(!getContent()) {
            if(demineur.getActualChamp().calculNbMinesAutour(x, y) >= 1) {
                text = "" + demineur.getActualChamp().calculNbMinesAutour(x, y);
            }
            else if(demineur.getActualChamp().calculNbMinesAutour(x, y) == 0) {
                text = "";
            }
        }
    }

    //This function says whether the case is revealed or not
    public boolean isRevealed() { return (situation == REVEALED); }

    //This functions decides of the esthetic that is displayed. It is called in every repaint() call function
    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);

        int nbMinesTotal = 0;

        switch (demineur.getActualChamp().getLevel()) {
            case EASY -> nbMinesTotal = Champ.easyLevel[2];
            case MEDIUM -> nbMinesTotal = Champ.mediumLevel[2];
            case HARD -> nbMinesTotal = Champ.hardLevel[2];
            case CUSTOM -> nbMinesTotal = Niveau.customNbMines;
        }

        demineur.getGUI().minesRestantes.setText("Mines left : " + (nbMinesTotal - demineur.getGUI().recalculNbMinesFound()));
        demineur.getGUI().score.setText("Score : " + getScore());

        if((nbMinesTotal - demineur.getGUI().recalculNbMinesFound()) == 0) {
            if(JOptionPane.showConfirmDialog(null, "You won ! Do you want to retry ?", "Congrats buddy!!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                demineur.getGUI().reset(demineur.getActualChamp().getLevel());
            }
        }


        if(!isRevealed()) {
            if(!isRightClicked()) {
                nbMinesAround.setVisible(false);
                setBackground(Color.DARK_GRAY);
                setBorder(BorderFactory.createBevelBorder(0, Color.WHITE, Color.GRAY));
            }
            else if(isRightClicked()) {
                nbMinesAround.setVisible(false);
                setBackground(Color.YELLOW);
                setBorder(BorderFactory.createBevelBorder(0, Color.WHITE, Color.GRAY));
            }
        }
        else {
            if(getContent()) {
//                Toolkit toolkit = getToolkit();
//                gc.drawImage(toolkit.getImage("img/bomb.png"), 0, 0, this);
                setBackground(Color.RED);
                setBorder(BorderFactory.createBevelBorder(0, Color.WHITE, Color.GRAY));
            }
            else if(!getContent()) {
                setBackground(Color.GRAY);
                setBorder(BorderFactory.createBevelBorder(0, Color.WHITE, Color.GRAY));
                nbMinesAround.setVisible(true);
            }
        }
    }

    //This function deals with the different user's interactions with the field
    @Override
    public void mousePressed(MouseEvent e) {
        //starts counting if first click
        if(!counter.hasStarted()) { counter.start(); }

        if(getContent()) {  //if the case contains a mine
            if(SwingUtilities.isLeftMouseButton(e)) {
                situation = REVEALED;
                repaint();

                if (JOptionPane.showOptionDialog(null,
                        "Oh no, you lost ! Do you want to retry ?\nFinal score : " + getScore(),
                        "Game over !",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null) == 0) {
                    demineur.getGUI().reset(demineur.getActualChamp().getLevel());     // implementer fonction de reset qui recommence la partie
                } else { System.exit(0); }
            }
            else if(SwingUtilities.isRightMouseButton(e)) {     //if you want to flag it
                if(this.getSituation() != FLAGGED) {
                    this.situation = FLAGGED;
                    nbMinesFound++;     //one more mine flagged
                    demineur.getGUI().recalculNbMinesFound();
                    repaint();
                }
                else if(this.getSituation() == FLAGGED) {     //if you want to reset the right click
                    this.situation = HIDDEN;
                    nbMinesFound--;
                    repaint();
                }
            }
        }
        else if(!getContent()) {    //if no mine
            if(SwingUtilities.isLeftMouseButton(e)) {
                setSituation(REVEALED);
                score++;    //another revealed safe case
                checkNeighbours();
                repaint();
            }
            else if(SwingUtilities.isRightMouseButton(e)) {
                if(this.getSituation() != FLAGGED) {
                    this.situation = FLAGGED;
                    repaint();
                }
                else if(this.getSituation() == FLAGGED) {
                    this.situation = HIDDEN;
                    repaint();
                }
            }
        }
    }

    //This function is called when a case is clicked. It checks its direct neighbours and theirs and reveals the empty cases around
    private void checkNeighbours() {

        Stack<Cases> fileNeighbours = new Stack<>();

        fileNeighbours.push(demineur.getGUI().tabCases[x][y]);  //we add the concerned cell

        while(!fileNeighbours.empty()) {
            Cases newCase = fileNeighbours.pop();

            int rowStart = max(0, newCase.x - 1);
            int rowFinish = min(demineur.getActualChamp().dimX - 1, newCase.x + 1);
            int colStart = max(0, newCase.y - 1);
            int colFinish = min(demineur.getActualChamp().dimY - 1, newCase.y + 1);

            for (int curRow = rowStart; curRow <= rowFinish; curRow++) {
                for (int curCol = colStart; curCol <= colFinish; curCol++) {
                    Cases currentCase = demineur.getGUI().tabCases[curRow][curCol];
                    if (!currentCase.getContent()) {     //if not a bomb
                        if (currentCase.getSituation() == HIDDEN) {     //if hidden
                            currentCase.setSituation(REVEALED);     //reveal neighbour
                            score++;
                            if (demineur.getActualChamp().calculNbMinesAutour(curRow, curCol) == 0) {
                                fileNeighbours.push(currentCase);
                            }
                        }
                    }
                }
            }
        }
    }

    //This function is not used
    @Override
    public void mouseClicked(MouseEvent e) {}

    //This function is not used
    @Override
    public void mouseReleased(MouseEvent e) {}

    //This function is not used
    @Override
    public void mouseEntered(MouseEvent e) {}

    //This function is not used
    @Override
    public void mouseExited(MouseEvent e) {}

    //This function initiates the case content (empty or with a mine)
    public void setContent(boolean b) { this.content = b; }

    //This function changes the case situation when clicked
    public void setSituation(int newSituation) { this.situation = newSituation;  }

    //This functions tells whether there is a mine in the case or not
    public boolean getContent() { return content; }

    //This function returns the case situation : HIDDEN, REVEALED, FLAGGED
    public int getSituation() { return this.situation; }

    //This functions returns whether the case is flagged or not
    public boolean isRightClicked() { return (situation == FLAGGED); }

    //This function returns the number of flagged mines during the game
    public int getNbMinesFound() { return nbMinesFound; }

    //This functions returns the score which represents the number of revealed safe cases
    public int getScore() { return score; }
}
