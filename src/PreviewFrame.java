import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class PreviewFrame extends JFrame {
	
	private Creature creature;
	private BufferedImage buffer;
	
	public Timer timer;
	
	final int updateEveryMilliseconds = 33;
	final int initialDelayMilliseconds = 1000;
	
// Constructor:
	public PreviewFrame(Creature _creature) {
		
		creature = _creature;
		creature.setToStart();
		
		setSize((int)Creature.SIZE_MAX.getX(), (int)Creature.SIZE_MAX.getY());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		setVisible(true);
		
		buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		initializeTimer();	
	}
	
// Constructor helpers:
	private void initializeTimer() {
		
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				if(creature.physics.time >= creature.physics.timeMax) {
					restartAnimation();
				} else {
					creature.move();
					
					// Buffering to avoid flickering image:
					update(buffer.getGraphics());
					getGraphics().drawImage(buffer, 0, 0, null);
				}		
		    }
		};
			
		timer = new Timer(updateEveryMilliseconds, taskPerformer);
		timer.setInitialDelay(initialDelayMilliseconds);
	}
	
// Animations:
	public void startAnimation() {
		
		timer.start();
	}
	
	public void stopAnimation() {
		
		timer.stop();
	}
	
	public void restartAnimation() {
		
		creature.setToStart();
		timer.restart();
	}
	
// Painting:
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		
		paintWorld(g);
		paintCreature(g);
		paintStats(g);
	}
	
// Painting helpers:	
	private void paintWorld(Graphics g) {
		
		if(!creature.alive) return;
		
		XYPoint center = creature.getCenter();
    	XYPoint screenCenter = new XYPoint(0.5*getWidth(), 0.5*getHeight());
    	center.subtract(screenCenter);
    	
    	int w = getWidth();
    	int h = getHeight();
    	
    	// Ground:
    	Color colorGround = new Color(50,175,50);
    	g.setColor(colorGround);
    	g.fillRect(-w, (int)yCoord(-center.getY()), 3*w, 2*h);
    	
    	// Sky:
    	Color colorSky = new Color(75,125,255);
    	g.setColor(colorSky);
    	g.fillRect(-w, (int)yCoord(-center.getY()+2*h), 3*w, 2*h);
    	
    	// Marks:
    	g.setColor(Color.GRAY);
    	int markDist = 50;   	
    	int markWidth = 4;
    	int markHeight = 16;
    	int centerX = (int)creature.getCenter().getX();
    	int offset = centerX % markDist;
    	
    	// Small marks:
    	int nMarks = getWidth() / markDist + 10;
    	for(int i=-nMarks/2; i<nMarks/2; i++) {
    		int x = centerX + i*markDist - offset;
    		g.fillRect((int)(x - 0.5*markWidth - center.getX()),
    				   (int)yCoord(markHeight - center.getY()),
    				   markWidth, markHeight);
    	}
    	
    	// Start mark:
    	g.setColor(Color.BLACK);
    	g.fillRect((int)(-markWidth - center.getX()),
    			   (int)yCoord(2*markHeight - center.getY()),
    			   2*markWidth, 2*markHeight);    			   
	}
	
	private void paintCreature(Graphics g) {

		if(creature.alive) {
			
			Graphics2D g2d = (Graphics2D)g;
			
	    	XYPoint center = creature.getCenter();
	    	XYPoint screenCenter = new XYPoint(0.5*getWidth(), 0.5*getHeight());
	    	center.subtract(screenCenter);
	    	
	    	// Muscles:
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
			
			// Nodes:
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
	
	private void paintStats(Graphics g) {
		
		g.setFont(new Font("Dialog", Font.PLAIN, 16));
    	g.setColor(Color.BLACK);
    	
    	int time = (int)(creature.physics.time / creature.physics.timeMax * 100.0);
    	double distance = creature.getCenter().getX();
    	String timeString = String.format("Time: %2d %%", time);
    	g.drawString(timeString, 10, 20);
    	String distanceString = String.format("Distance: %.2f", distance);
    	g.drawString(distanceString, 10, 40);
	}
	
	private double yCoord(double y) {
		
		return -y + getHeight();
	}
}