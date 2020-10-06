/**
 *   @author : Fatima-Zohra NAZIH
 *  @title : MineSweeper
 */

package tools;

//This is a class regrouping the enum of level possibilities as well as the custom level default parameters
public class Niveau {
    public enum Level { EASY, MEDIUM, HARD, CUSTOM }

    public static int customDimX = 12;
    public static int customDimY = 16;
    public static int customNbMines = 85;
}
