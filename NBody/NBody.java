import java.awt.geom.Point2D;

/**
 * Simulates an object with known mass and charge at an instance in time in two dimensions  
 *
 * @author cling99
 * @version 0.2
 */
public class NBody
{
    private double mass;
    private double charge;
    private final static double G = 6.67408e-11; //Gravitational Constant
    private final static double K = 8.98755e9; //Coulomb's Constant
    private final static double U = 1.0e-7;//Vacuum Permeability / (4 * PI)

    private double[] pos = new double[2]; // {x, y}  
    private double[] vel = new double[2]; // {x, y}
    private double[] acc = new double[2]; // {x, y}

    /**
     * Constructs an instance of a NBody with no initial velocity
     */
    public NBody(double mass, double charge, double x, double y)
    {
        this.mass = mass;
        this.charge = charge;
        pos[0] = x;
        pos[1] = y;
    }

    /**
     * Calculates the combined force on this NBody by an external object
     * (electomagnetic & gravitational)
     * 
     * @param   body    An NBody object with known mass and charge
     * @return  force between two objects (x, y)
     */
    public double[] getForce(NBody body)
    {
        double x1 = pos[0];
        double y1 = pos[1];
        double x2 = body.getPos()[0];
        double y2 = body.getPos()[1];
        double radius = Math.sqrt(Math.pow((x1-x2),2)+ Math.pow((y1-y2),2));

        double fXG = 0;
        double fYG = 0;
        double fXE = 0;
        double fYE = 0;
        double fXM = 0;
        double fYM = 0;

        // Gravitational Force
        fXG = G *  mass * body.getMass() / Math.pow(radius, 2)
        * ( (x1 - x2) / radius ); //cos
        fYG = G *  mass * body.getMass() / Math.pow(radius, 2)
        * ( (y1 - y2) / radius ); //sin       

        // Electric Force
        if (charge != 0)
        {
            fXE = K *  charge * body.getCharge() / Math.pow(radius, 2)
                * ( (x1 - x2) / radius ); //cos 
            fYE = K *  charge * body.getCharge() / Math.pow(radius, 2)
                * ( (y1 - y2) / radius ); //sin
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
            double b = U * body.getCharge() * (vX * rY - vY * rX) / Math.pow(radius, 3);
            
            fXM = charge * vel[1] * b;
            fYM = charge * vel[0] * b;
        }
        
        double[] forces =  {fXG + fYG + fXE, fYE + fXM + fYM};
        return forces;
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
        pos[0] += vel[0] * dT + .5 * acc[0] * Math.pow(dT, 2); 
        
        vel[0] += acc[0] * dT;
        vel[1] += acc[1] * dT;
    }
    
    /**
     * Updates the position and velocity of the particle, assuming constant force 
     */

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
}
