
public class Muscle {
	
	public double strength;
	public double cycleTime, contractStart, contractTime;
	public double lenMin, lenMax;
	
	private Node[] nodes = new Node[2];
	
	final static double STRENGTH_MAX = 100.0;
	final static double CYCLE_TIME_MAX = 10.0;
	final static double LEN_MIN = 5.0;
	final static double LEN_MAX = 100.0;
	
	public Muscle(Node node1, Node node2,
				  double _strength, double _cycleTime,
				  double _contractStart, double _contractTime,
				  double _lenMin, double _lenMax) {
		
		nodes[0] = node1;
		nodes[1] = node2;
		strength = _strength;
		cycleTime = _cycleTime;
		contractStart = _contractStart;
		contractTime = _contractTime;
		lenMin = _lenMin;
		lenMax = _lenMax;
	}
	
	public void getForces(XYPoint onNode0, XYPoint onNode1, double time) {
	// Calculates the forces on the nodes at the given time
		
		XYPoint delta = nodes[1].pos.subtracted(nodes[0].pos);
		double angle = Math.atan2(delta.getY(), delta.getX());

	    boolean contraction = isContracting(time);
	    double len = Math.sqrt(delta.getX()*delta.getX() + delta.getY()*delta.getY());

	    // Determine 'a' for force like -a*(x-c)^2+d :
	    double b = lenMin - 0.5*(lenMax+lenMin);
	    double a = strength / (b*b);
	    
	    // Determine contraction strength:
	    double tmp = len - 0.5*(lenMax+lenMin);
	    double modifiedStrength = Math.max(-strength, -a * tmp*tmp + strength);

	    // Determine force direction:
	    int dir = 1;
	    if(len > lenMax) {
	        dir = 1;
	    } else if(len < lenMin) {
	        dir = -1;
	    } else if(contraction) {
	        dir = -1;
	    } else {
	        dir = 1;
	    }
	    
	    onNode0.setX(-dir*modifiedStrength*Math.cos(angle));
	    onNode0.setY(-dir*modifiedStrength*Math.sin(angle));
	    onNode1.setX( dir*modifiedStrength*Math.cos(angle));
	    onNode1.setY( dir*modifiedStrength*Math.sin(angle));
	}
	
	public Node getNode(int i) {
		return nodes[i];
	}
	
	public boolean isContracting(double time) {
		
		int nCycles = (int)(time/cycleTime);
	    double cycleStart = nCycles*cycleTime;

	    return (time >= cycleStart+contractStart && time < cycleStart+contractStart+contractTime) ||
	           (time >= cycleStart-cycleTime+contractStart && time < cycleStart-cycleTime+contractStart+contractTime);
	}
}
