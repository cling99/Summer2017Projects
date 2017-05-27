import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 * Simulates an object with known mass and charge at an instance in time in two dimensions  
 *
 * @author cling99
 * @version 0.5
 */
public class NBody
{
    private double mass;
    private double charge;
    private int radius;
    private final static double G = 6.67408e-11; //Gravitational Constant
    private final static double K = 8.98755e9; //Coulomb's Constant
    private final static double U = 1.0e-7;//Vacuum Permeability / (4 * PI)

    private double[] pos = new double[2]; // {x, y}  
    private double[] vel = new double[2]; // {x, y}
    private double[] acc = new double[2]; // {x, y}

    private final static double DENSITY = 5e5;
    
    private int MAX_X = 1920;
    private int MAX_Y = 1080;

    /**
     * Constructs an instance of a NBody with NO initial velocity
     */
    public NBody(double mass, double charge, double x, double y)
    {
        this.mass = mass;
        this.charge = charge;
        radius =(int) (Math.pow( mass / (DENSITY * 4 * Math.PI) , 1.0/3)); 
        pos[0] = x;
        pos[1] = y;
    }

    /**
     * Constructs an instance of a NBody WITH initial velocity
     */
    public NBody(double mass, double charge, double x, double y, double vX, double vY)
    {
        this.mass = mass;
        this.charge = charge;
        radius =(int) (Math.pow( mass / (DENSITY * 4 * Math.PI) , 1.0/3)); 
        pos[0] = x;
        pos[1] = y;
        vel[0] = vX;
        vel[1] = vY;
    }

    /**
     * Calculates the combined force on this NBody by an external NBody of known mass and charge
     * 
     * @param   body    An NBody object with known mass and charge
     * @return  force between two objects (x, y) with individual components after
     */
    public double[] getForces(NBody body)
    {
        double x1 = pos[0];
        double y1 = pos[1];
        double x2 = body.getPos()[0];
        double y2 = body.getPos()[1];
        double r = Math.sqrt(Math.pow((x1-x2),2)+ Math.pow((y1-y2),2));

        double fXG = 0;
        double fYG = 0;
        double fXE = 0;
        double fYE = 0;
        double fXM = 0;
        double fYM = 0;

        // Gravitational Force: For future implement Newton's Third Law
        fXG = (G *  mass * body.getMass() / (Math.pow(r, 2)))
        * Math.abs( (x1 - x2) / r ); //cos
        if (x1 > x2)
            fXG *= -1;

        fYG = G *  mass * body.getMass() / Math.pow(r, 2)
        * Math.abs( (y1 - y2) / r ); //sin
        if (y1 > y2)
            fYG *= -1;

        // Electric Force
        if (charge != 0)
        {
            fXE = K *  charge * body.getCharge() / Math.pow(r, 2)
            * ( (x1 - x2) / r ); //cos 
            fYE = K *  charge * body.getCharge() / Math.pow(r, 2)
            * ( (y1 - y2) / r ); //sin
        }

        // Magnetic Force
        if (charge != 0
        && Math.sqrt(Math.pow(body.getVel()[0],2) + Math.pow(body.getVel()[1],2)) > 0
        && Math.sqrt(Math.pow(vel[0],2) + Math.pow(vel[1],2)) > 0)
        {
            double vX = body.getVel()[0]; 
            double vY = body.getVel()[1];
            double rX = x1 - x2;
            double rY = y1 - y2;
            double b = U * body.getCharge() * (vX * rY - vY * rX) / Math.pow(r, 3);

            fXM = charge * vel[1] * b;
            fYM = charge * vel[0] * b;
        }

        double[] forces =  {fXG + fXE + fXM, fYG + fYE + fYM
                            ,fXG, fYG, fXE, fYE, fXM, fYM};
        return forces;
    }
    
    /**
     * Calculates the magnetic force on this NBody by an external NBody of known charge and velocity
     *      magnetic force doesn't follow Newton's third law so requires more calculations
     *      separated for efficiency
     * 
     * @param   body    An NBody object with known mass and charge
     * @return  the magnetic force between two objects (x, y) with individual components after
     */
    public double[] getMagForces(NBody body)
    {
        double x1 = pos[0];
        double y1 = pos[1];
        double x2 = body.getPos()[0];
        double y2 = body.getPos()[1];
        double r = Math.sqrt(Math.pow((x1-x2),2)+ Math.pow((y1-y2),2));

        double fXM = 0;
        double fYM = 0;

        // Magnetic Force
        if (charge != 0
        && Math.sqrt(Math.pow(body.getVel()[0],2) + Math.pow(body.getVel()[1],2)) > 0
        && Math.sqrt(Math.pow(vel[0],2) + Math.pow(vel[1],2)) > 0)
        {
            double vX = body.getVel()[0]; 
            double vY = body.getVel()[1];
            double rX = x1 - x2;
            double rY = y1 - y2;
            double b = U * body.getCharge() * (vX * rY - vY * rX) / Math.pow(r, 3);

            fXM = charge * vel[1] * b;
            fYM = charge * vel[0] * b;
        }

        double[] forces =  {fXM, fYM};
        return forces;
    }

    /**
     * Checks if the center of an NBody is within the radius of another
     * 
     * @param   body    an NBody with known pos
     * @return  true if two bodies are overlapping, false otherwise
     */
    public boolean isInside(NBody body)
    {
        double x1 = pos[0];
        double y1 = pos[1];
        double x2 = body.getPos()[0];
        double y2 = body.getPos()[1];
        double distance = Math.sqrt(Math.pow((x1-x2),2)+ Math.pow((y1-y2),2));

        if (distance <= radius || distance <= radius)
            return true;
        else
            return false;
    }

    /**
     * Models an inelastic collision between two bodies, only momentum is conserved
     *      returns a larger object with an initial velocity
     * @param   body    an NBody with known mass, charge, and velocity
     * @return  A new NBody combining the masses of two NBody and conserved momentum
     */
    public NBody inelasticCollide(NBody body)
    {
        double mass2 = body.getMass();
        double[] pos2 = body.getPos();
        double[] vel2 = body.getVel();

        return new NBody(
            mass + mass2, //Mass
            charge + body.getCharge(), //Charge
            (mass * pos[0] + mass2 * pos2[0]) / (mass + mass2), // Center of Mass (X)
            (mass * pos[1] + mass2 * pos2[1]) / (mass + mass2), // Center of Mass(Y)
            (mass * vel[0] + mass2 * vel2[0]) / (mass + mass2), // Velocity X
            (mass * vel[1] + mass2 * vel2[1]) / (mass + mass2)); // Velocity Y
    }

    /**
     * Updates the position and velocity of the particle, 
     *      assuming constant force throughout the time interval
     * @param   dT  Time interval of update (Eulers)
     * @param   fX  Net force in the x-direction
     * @param   fY  Net force in the y-direction
     */
    public void update(double dT, double fX, double fY)
    {
        acc[0] = fX / mass;
        acc[1] = fY / mass;

        //Position depends on initial velocity, not final, must be calculated first
        pos[0] += vel[0] * dT + .5 * acc[0] * Math.pow(dT, 2); 
        pos[1] += vel[1] * dT + .5 * acc[1] * Math.pow(dT, 2); 

        vel[0] += acc[0] * dT;
        vel[1] += acc[1] * dT;

        if (pos[0] - radius <= 0) 
        {    
            pos[0] = 0 + radius;
            vel[0] *= -1;
        }
        else if (pos[0] + radius >= MAX_X)
        {
            pos[0] = 1920 - radius;
            vel[0] *= -1;
        }
        if (pos[1] - radius <= 0) 
        {    
            pos[1] = 0 + radius;
            vel[1] *= -1;
        }
        else if (pos[1] + radius >= MAX_Y)
        {
            pos[1] = 1080 - radius;
            vel[1] *= -1;
        }
    }

    public double getMass()
    {
        return mass;
    }

    public double getCharge()
    {
        return charge;
    }

    public double[] getPos()
    {
        return pos;
    }

    public double[] getVel()
    {
        return vel;
    }

    public double[] getAcc()
    {
        return acc;
    }

    public void draw(Graphics2D g2)
    {
        Ellipse2D.Double body = new Ellipse2D.Double(pos[0]-radius, pos[1]-radius, 
                radius * 2, radius * 2);
        g2.setColor(new Color(0, 0, 0));
        g2.draw(body);
        g2.fill(body);
    }
}
