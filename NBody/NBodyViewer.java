import javax.swing.JFrame;
import java.awt.Color;

/**
 * Creates the frame containing the component
 *
 * @author cling99
 * @version 0.5
 */
public class NBodyViewer
{
    private static final int ANIMATION_TIME = 1000000000;
    
    public static void main(String[] args) throws InterruptedException
    {
        JFrame frame = new JFrame();
        
        frame.setBackground(new Color(255, 255 , 255));
        frame.setSize(1920, 1080);
        frame.setTitle("N Body Problem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        NBodyComponent component = new NBodyComponent();
        double dT = component.getDT();
        frame.add(component);
        
        frame.setVisible(true);
        
        for (double seconds = 0; seconds <= ANIMATION_TIME; seconds += dT)
        {
            component.nextFrame();
            frame.setTitle(component.getBodyCount()+ " Body Problem \n T: "+Math.round(seconds)+ " s");
            Thread.sleep(1);
        }
      
    }
}
