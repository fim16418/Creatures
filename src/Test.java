import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Test {
	
	public static void main(String[] args) {
		
		Physics physics = new Physics(0.0, 0.1, 10.0, 1.5, 0.0001);
		Creature creature = new Creature(physics);
		creature.randomize(3, 10.0);
		
		JFrame frame = new PreviewFrame(creature);
		frame.setLocation(300, 200);
		frame.setSize((int)Creature.SIZE_MAX.getX(), (int)Creature.SIZE_MAX.getY());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//frame.setUndecorated(true);
		frame.setVisible(true);
	}
}