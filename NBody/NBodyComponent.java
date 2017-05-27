import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * Creates multiple instances of NBody, models interaction between bodies
 *
 * @author cling99
 * @version 0.5
 */
public class NBodyComponent extends JComponent
{
    private ArrayList<NBody> bodies = new ArrayList<NBody>();
    private static final double DT = .001;

    public NBodyComponent()
    {
        // bodies.add(new NBody(2e14,0,960,540));
        // bodies.add(new NBody(5e9,0,1920,540));
        // bodies.add(new NBody(5e9,0,0,540));
        // bodies.add(new NBody(5e9,0,960,0));
        // bodies.add(new NBody(5e9,0,960,1080));
        // bodies.add(new NBody(5e9,0,0,0));
        // bodies.add(new NBody(5e9,0,1920,0));
        // bodies.add(new NBody(5e9,0,0,1080));
        // bodies.add(new NBody(5e9,0,1920,1080));
        bodies.add(new NBody(5e9,5e3,0,540));
        bodies.add(new NBody(5e9,-5e3,500,540));
        bodies.add(new NBody(5e9,5e3,1000,540));
    }

    public NBodyComponent(int numBodies)
    {
        for (int i = 0; i < numBodies; i++)
            bodies.add(
                new NBody(
                    6.3e6 + Math.random() * 1e9,
                    0,
                    1 + Math.random() * 1919,
                    1 + Math.random() * 1079,
                    -25 + Math.random() * 51,
                    -25 + Math.random() * 51));
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < bodies.size(); i++)
            bodies.get(i).draw(g2);

    }

    /**
     * Finds the net force on all bodies by adding forces due to each body
     *  updates pos, vel, acc.
     */
    public void nextFrame()
    {
        //Checks for overlaping
        for (int i = 0; i < bodies.size(); i++)
        {
            NBody body1 = bodies.get(i);

            for (int j = i + 1; j < bodies.size(); j++)
            {
                NBody body2 = bodies.get(j);

                if (body1.isInside(body2) || body2.isInside(body1))
                {
                    bodies.add(body1.inelasticCollide(body2));
                    bodies.remove(body1);
                    bodies.remove(body2);

                    //To not skip any checks of the i + 1 body
                    i--;
                    break;
                }
            }
        }

        double[][] forces = new double[bodies.size()][2];

        for (int i = 0; i < bodies.size(); i ++)
        {
            double fX = 0;
            double fY = 0;
            NBody body1 = bodies.get(i);
            for (int j = 0; j < i; j ++)
            {
                NBody body2 = bodies.get(j);
                double[] force = body1.getMagForces(body2);
                fX += force[0];
                fY += force[1];
            }
            for (int j = i + 1; j < bodies.size(); j ++)
            {
                NBody body2 = bodies.get(j);
                double[] force = body1.getForces(body2);
                fX += force[0];
                fY += force[1];
                
                //Newton's third law applies to Newton's law of universal gravitation & Coulomb's law
                forces[j][0] += -1 * (force[2] + force[4]);
                forces[j][1] += -1 * (force[3] + force[5]);
            }

            forces[i][0] += fX;
            forces[i][1] += fY;
        }

        for (int i = 0; i < forces.length; i++)
        {
            bodies.get(i).update(DT, forces[i][0], forces[i][1]);
        }

        repaint();
    }

    public double getDT()
    {
        return DT;
    }

    public int getBodyCount()
    {
        return bodies.size();
    }
}
