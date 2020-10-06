package tools;/*
    @author : Fatima-Zohra NAZIH
    @title : Deminer
 */

import javax.swing.*;
import java.awt.*;

public class Counter extends JPanel implements Runnable {
    private Thread processScores;
    private int counter;    //counter in seconds
    private boolean started;
    private final static int widthCounter = 100;
    private final static int heightCounter = 30;

    public Counter() {
        started = false;
        counter = 0;
        processScores = new Thread(this);
        repaint();
    }

    public void start() {
        if (!started) {
            processScores.start();
            started = true;
        }
    }

    @Override
    public void run() {
        while (processScores != null) {
            try{
                processScores.sleep(1000);   //wait for a second
                counter++;
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ca sort du run");
    }

    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);

        setPreferredSize(new Dimension(widthCounter, heightCounter));
        setBackground(Color.CYAN);
        setBorder(BorderFactory.createBevelBorder(0, Color.BLACK, Color.GRAY));

        gc.drawString("Timer : " + counter/60 + "m " + counter%60, 15, 18);

    }

    public boolean hasStarted() { return started; }

    public void resetCount() {
        stopCount();
        counter = 0;
        started = false;
        processScores = new Thread(this);
    }

    void stopCount() { processScores = null; }
}
