import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Test {
	
	public static void main(String[] args) {
		
		Physics physics = new Physics(0.0, 0.1, 20.0, 2.5, 0.0001);
		Creature creature = new Creature(physics);
		creature.randomize(3, 10.0);
		
		JFrame frame = new PreviewFrame(creature);
		frame.setLocation(300, 200);
	}
}