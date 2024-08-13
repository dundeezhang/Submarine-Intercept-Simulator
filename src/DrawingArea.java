
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dundee Zhang
 */
public class DrawingArea extends javax.swing.JPanel {

    /**
     * Creates new form DrawingArea
     */
    public static ArrayList<Integer> xy = new ArrayList<Integer>();

    Timer t1;
    int count = 0; // number of times the program ran
    double angle = 0.0 - Math.PI / 2; // the current angle
    int angleCount = 0; // number of angles in the circle calculated
    int radius = 5; // radius of spiral function
    int counter = 0; // for timer and speeds
    int haidaPosX = haidaX;
    int haidaPosY = haidaY;
    int subPosX = 0;
    int subPosY = 1;
    boolean runSub = true; // variable to allow the completion of the interceptor path even after the sub has escaped

    // gui vairables
    public static boolean started = false;
    public static boolean tracers = false;
    public static boolean inwards = false;
    public static int subX;
    public static int subY;
    public static int haidaX;
    public static int haidaY;
    public static double a;
    public static double b;
    public static int subSpeed = 6;
    public static int haidaSpeed;
    public static int haidaRadius;

    public DrawingArea() {
        initComponents();
        // timer
        t1 = new Timer(10, new DrawingArea.TimerListener());
        t1.start();

    }

    protected void paintComponent(Graphics g) {
        // if tracers are wanted, do not refresh
        if (!tracers) {
            super.paintComponent(g);
        }
        // only run if search button is pressed
        if (started) {
            // update location
            if (runSub) {
                if (counter % (int) (50 / subSpeed) == 0) {
                    if(xy.size() != count) {
                        subPosX = xy.get(count);
                        subPosY = xy.get(count + 1);
                    }  
                    count += 2;
                }
            }
            // if the haida function is spiraling inwards
            if (!inwards) {
                if (counter % (100 / haidaSpeed) == 0) { // speed of haida
                    haidaPosX = (int) (radius * Math.cos(angle) + haidaX); // haida positions
                    haidaPosY = (int) (radius * Math.sin(angle) + haidaY); // haida positions
                    angle += Math.PI / 9; // angle of theta for haita
                    angleCount++; // count the amount of points per spiral
                    if (angleCount == 20) { // 20 is the max about of points per spiral
                        angle = 0 - Math.PI / 2; // top point of circle
                        angleCount = 0; // reset angle count
                        if (radius < haidaRadius) {
                            radius += 30; // add 30px to radius per circle
                        } else {
                            runSub = false; // only keep drawing the haida positions
                        }

                    }
                }
            } else {
                if (counter % (100 / haidaSpeed) == 0) {// speed of haida
                    haidaPosX = (int) (haidaRadius * Math.cos(angle) + haidaX); // haida positions
                    haidaPosY = (int) (haidaRadius * Math.sin(angle) + haidaY); // haida positions
                    angle += Math.PI / 9;// angle of theta for haita
                    angleCount++;// count the amount of points per spiral
                    if (angleCount == 20) {
                        angle = 0 - Math.PI / 2; // top point of circle
                        angleCount = 0; // reset angle count
                        if (radius + 20 < haidaRadius) {
                            haidaRadius -= 30; // shrink the radius of the circles by 30 each run
                        } else {
                            runSub = false; // only keep drawing the haida positions
                        }
                    }
                }
            }
            // display coords for sub on the drawingArea
            g.setColor(Color.black);
            g.fillRect(0, 425, 80, 20);
            g.setColor(Color.white);
            g.drawString("(" + subPosX + ", " + (460 - subPosY) + ")", 5, 440);
            
            // display boat and sub on the drawingArea
            g.setColor(Color.red); // Sub
            g.fillOval(subPosX, 460 - subPosY, 10, 20);
            g.setColor(Color.blue); // Haida
            g.fillRect(haidaPosX, haidaPosY, 15, 25);

            // check for the difference between the sub and the Haida
            int difference = 999;
            if (count != 0) { // calculate the distance between sub and haida
                difference = pointDiff(haidaPosX, haidaPosY, xy.get(count - 2), 460 - xy.get(count - 1));
                g.setColor(Color.black);
                g.fillRect(580, 0, 120, 20);
                g.setColor(Color.white);
                g.drawString("Distance: " + difference, 600, 12);
            }
            // if sub and haida distance is less than 50, the sub was found
            if (difference <= 50) {
                g.setColor(Color.black);
                g.fillRect(150, 150, 500, 100);
                g.setColor(Color.white);
                // display sub found message
                g.drawString("Submarine Caught! Last Position was (" + subPosX + ", " + (460 - subPosY) + ") and distance was " + difference, 200, 200);
                started = false;
            }
            // run if sub goes off screen
            if (subPosY < 0 || subPosY > 460 || subPosX > 700) {
                g.setColor(Color.black);
                g.fillRect(150, 150, 400, 100);
                g.setColor(Color.white);
                // display sub escaped message
                g.drawString("Submarine Escaped! Current Position is (" + subPosX + ", " + (460 - subPosY) + ")", 200, 200);
                runSub = false;
            }

        }
    }

    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (started) {
                counter++; // counter for the amount of times the timer has run paintComponent()
                repaint();
            }
        }
    }
    // returns the f(x) for the x value
    public static int subFunc(int x) {
        return (int) Math.round((a * Math.pow(b, x))) + subY;
    }
    
    // recursive function for finding all the points for the submarine
    /**
     * 
     * @param coords ArrayList to store the coordinates of the submarine
     * @param realX The un-scaled value of x
     * @return 
     */
    public static double submarine(ArrayList<Integer> coords, int realX) {
        // get old coords
        int x = coords.get(coords.size() - 2);
        int y = coords.get(coords.size() - 1);
        // calc new values for x y
        int newX = (coords.get(coords.size() - 2)) + 20; // new x val
        int newY = subFunc(realX); // new y val
        // interpolation runs if the distance between the old and new points is greater than 20
        if (pointDiff(x, y, newX, newY) >= 20) {
            distanceBetween(coords, x, y, newX, newY);
        }
        // add new values to the ArrayList
        coords.add(newX);
        coords.add(subFunc(realX));

        // recursively run until one of these values are met
        if (x <= 700 && subFunc(realX) <= 460 && subFunc(realX) >= 0) {
            // return itself to find more points recursively
            return submarine(coords, realX + 1);
        } else {
            // return 0 if all points were found within the base case
            return 0.0;
        }
    }
    // method to calculate the difference between two points
    /**
     * 
     * @param x X-1
     * @param y Y-1
     * @param x2 X-2
     * @param y2 Y-2
     * @return The distance between (X-1, Y-1) and (X-2, Y-2)
     */
    public static int pointDiff(int x, int y, int x2, int y2) {
        return (int) Math.round(Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2)));
    }
    
    // interpolation method
    public static void distanceBetween(ArrayList<Integer> coords, int x, int y, int x2, int y2) {
        // calculate the distance between the two points
        double d = Math.sqrt(Math.pow(x2 - x, 2) + Math.pow(y2 - y, 2));
        // find slope
        double slope = (y2 - y) / (x2 - x);
        // amount of points to interpolate
        double amount = d / 40;
        // y-intercept
        double linearB = y - (slope * (x));
        
        for (int i = 1; i <= amount; i++) {
            // add x according to the interval (amount)
            coords.add((int) (i * (20 / amount)) + x);
            // add y=mx + b
            coords.add((int) (slope * (i * (20 / amount) + x) + linearB));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
