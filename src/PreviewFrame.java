import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class PreviewFrame extends JFrame {
	
	Creature creature;
	Timer timer;
	
	final int updateEveryMilliseconds = 33;
	
// Constructor:
	public PreviewFrame(Creature _creature) {
		
		creature = _creature;
		creature.setToStart();
		
		initializeTimer();
		timer.start();
	}
	
// Constructor helpers:
	private void initializeTimer() {
		
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				creature.physics.propagate();
				creature.move();
				update(getGraphics());
		    }
		};
		timer = new Timer(updateEveryMilliseconds, taskPerformer);
	}
	
// Painting:
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		
		paintWorld(g);
		paintCreature(g);
	}
	
// Painting helpers:	
	private void paintWorld(Graphics g) {
		
		if(!creature.alive) return;
		
		XYPoint center = creature.getCenter();
    	XYPoint screenCenter = new XYPoint(0.5*getWidth(), 0.5*getHeight());
    	center.subtract(screenCenter);
    	
    	int w = getWidth();
    	int h = getHeight();
    	
    	g.setColor(Color.GREEN);
    	g.fillRect((int)(-center.getX() - w), (int)yCoord(-center.getY()), 2*w, 2*h);
    	
    	g.setColor(Color.CYAN);
    	g.fillRect((int)(-center.getX() - w), (int)yCoord(-center.getY()+2*h), 2*w, 2*h);
	}
	
	private void paintCreature(Graphics g) {

		if(creature.alive) {
			
			Graphics2D g2d = (Graphics2D)g;
			
	    	XYPoint center = creature.getCenter();
	    	XYPoint screenCenter = new XYPoint(0.5*getWidth(), 0.5*getHeight());
	    	center.subtract(screenCenter);
	    	
	    	Iterator<Muscle> iteratorM = creature.muscles.iterator();
			while (iteratorM.hasNext()) {			
				Muscle muscle = iteratorM.next();
				Node node0 = muscle.getNode(0);
				Node node1 = muscle.getNode(1);
				
				double relativeStrength = muscle.strength / muscle.STRENGTH_MAX;
			    g2d.setStroke(new BasicStroke((int)(relativeStrength * 5.0)));
			    
			    Color color = new Color((int)(relativeStrength * 255),
			    						(int)(relativeStrength * 25),
			    						(int)(relativeStrength * 25));
			    g.setColor(color);
				
				g.drawLine((int)(node0.pos.getX() - center.getX()),
						   (int)yCoord(node0.pos.getY() - center.getY()),
						   (int)(node1.pos.getX() - center.getX()),
						   (int)yCoord(node1.pos.getY() - center.getY()));
			}
			
			Iterator<Node> iteratorN = creature.nodes.iterator();
			while (iteratorN.hasNext()) {			
				Node node = iteratorN.next();
				
				Color color = new Color((int)((1-node.friction) * 255),
						(int)((1-node.friction) * 255),
						(int)((1-node.friction) * 255));
				g.setColor(color);
				
				int r = (int)node.radius;			
				g.fillOval((int)(node.pos.getX() - r - center.getX()),
						   (int)yCoord(node.pos.getY() + r - center.getY()),	// '+r' to compensate sign from
						   2*r, 2*r);											// the second '+2*r' here !
			}
		} else {
			
			g.setFont(new Font("Dialog", Font.PLAIN, 50));
	    	g.setColor(Color.RED);
	    	g.drawString("DEAD", (int)(0.5*getWidth())-75, (int)(0.5*getHeight()));
		}
		
		
	}
	
	private double yCoord(double y) {
		
		return -y + getHeight();
	}
}