/**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package minesweeper;


import java.util.*;
import static java.lang.Integer.*;
import tools.*;

//This class represents the field regardless to the interface. It places mines randomly, and displays the field on the console
public class Champ {
    public boolean[][] champ; //array of boolean elements representing the field
    public int dimX; // number of columns
    public int dimY; //number of lines
    private int nbMines; //number of mines in the field
    public final static int[] easyLevel = {8, 8, 15}; //Parameters of the easy level
    public final static int[] mediumLevel= {12, 12, 45}; //Parameters of the medium level
    public final static int[] hardLevel = {16, 16, 60}; //Parameters of the hard level
    private final Random aleaGenerator = new Random(); //random integers generator

    //Default constructor of the minesweeper.Champ class : EASY level
    public Champ() {    //default constructor of class minesweeper.Champ : level EASY
        createChamp(easyLevel[0], easyLevel[1], easyLevel[2]);
    }

    //This function creates a field, and calls the placeMines() function to place mines randomly
    public void createChamp(int newDimX, int newDimY, int newNbMines) {
        this.dimX = newDimX;
        this.dimY = newDimY;
        this.nbMines = newNbMines;
        champ = new boolean[dimX][dimY];

        for(int i = 0; i < dimX; i++) {
            for(int j = 0; j < dimY; j++) {
                champ[i][j] = false;    //init : no mine in the field
            }
        }
        placeMines();  //then we place the mines
    }

    //This function places the mines randomly
    public void placeMines() {
        for(int cpt = 0; cpt < nbMines; cpt ++) {
            int x = aleaGenerator.nextInt(dimX);
            int y = aleaGenerator.nextInt(dimY);


            while(champ[x][y]) {    //if the randomly selected case is already a mine, we keep refreshing the random numbers until we find an empty case
                x = aleaGenerator.nextInt(dimX);
                y = aleaGenerator.nextInt(dimY);
            }

            champ[x][y] = true;
        }
    }

    //This function displays the field on the console to check if the mines are properly placed
    public void affChamp() {
        for(int i = 0; i < dimX; i++) {
            for(int j = 0; j < dimX; j++) {
                if(champ[i][j]) {
                    System.out.print("X  ");    //an X means there is a mine
                }
                else {
                    System.out.print(calculNbMinesAutour(i, j) + "  ");     //displays the number of mines around the case
                }
            }
            System.out.println();
        }
    }

    //This function calculates the numbers of mines around the (x, y) case
    public int calculNbMinesAutour(int x, int y) {
        int nbMinesAutour = 0;

        //variables rowStart, rowFinish, colStart et colFinish allow to check all the cases
        //around the chosen case, no matter where the case is (center, sides, corners)
        int rowStart  = max( x - 1, 0);
        int rowFinish = min( x + 1, champ.length - 1 );
        int colStart  = max( y - 1, 0);
        int colFinish = min( y + 1, champ[0].length - 1 );
        for ( int curRow = rowStart; curRow <= rowFinish; curRow++ ) {
            for (int curCol = colStart; curCol <= colFinish; curCol++) {
                if (champ[curRow][curCol]) {    //if the case is a mine
                    nbMinesAutour++;    //increment counter
                }
            }
        }

        return nbMinesAutour;
    }

    //overload of the toString() function, used with println()
    public String toString(){
        affChamp();
        return "";
    }

    //This function returns the level of the current field according to its dimensions
    public Niveau.Level getLevel() {
        if(dimX == easyLevel[0] && dimY == easyLevel[1] && nbMines == easyLevel[2])
        {
            return Niveau.Level.EASY;
        }
        else if(dimX == mediumLevel[0] && dimY == mediumLevel[1] && nbMines == mediumLevel[2])
        {
            return Niveau.Level.MEDIUM ;
        }
        else if(dimX == hardLevel[0] && dimY == hardLevel[1] && nbMines== hardLevel[2])
        {
            return  Niveau.Level.HARD;
        }
        else
        {
            return Niveau.Level.CUSTOM;
        }
    }

    public boolean[][] getChamp() { return champ; }
}
