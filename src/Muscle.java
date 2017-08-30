import java.util.Random;

public class Muscle {
	
	public double strength;
	public double cycleTime, contractStart, contractTime;
	public double lenMin, lenMax;
	
	public Node[] nodes = new Node[2];
	
	final static double STRENGTH_MAX = 100.0;
	final static double CYCLE_TIME_MAX = 10.0;
	final static double LEN_MIN = 5.0;
	final static double LEN_MAX = 100.0;
	
// Constructors:
	public Muscle(Node node0, Node node1) {
		
		nodes[0] = node0;
		nodes[1] = node1;
	}
	
	public Muscle(Node node0, Node node1,
				  double _strength, double _cycleTime,
				  double _contractStart, double _contractTime,
				  double _lenMin, double _lenMax) {
		
		nodes[0] = node0;
		nodes[1] = node1;
		strength = _strength;
		cycleTime = _cycleTime;
		contractStart = _contractStart;
		contractTime = _contractTime;
		lenMin = _lenMin;
		lenMax = _lenMax;
	}
	
	public Muscle(Muscle other) {
		
		strength = other.strength;
		cycleTime = other.cycleTime;
		contractStart = other.contractStart;
		contractTime = other.contractTime;
		lenMin = other.lenMin;
		lenMax = other.lenMax;
		
		//nodes done in Creature(Creature other)
	}
	
// Manipulation:
	public void randomize() {
		
		strength = Math.random() * STRENGTH_MAX;
        cycleTime = Math.random() * CYCLE_TIME_MAX;
        contractStart = Math.random() * cycleTime;
        contractTime = Math.random() * cycleTime;
        lenMax = Math.random() * (LEN_MAX-LEN_MIN) + LEN_MIN;
        lenMin = Math.random() * (lenMax-LEN_MIN) + LEN_MIN;
	}
	
// Computations:
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
	
// Getters:
	public Node getNode(int i) {
		return nodes[i];
	}
	
	public boolean isContracting(double time) {
		
		int nCycles = (int)(time/cycleTime);
	    double cycleStart = nCycles*cycleTime;

	    return (time >= cycleStart+contractStart && time < cycleStart+contractStart+contractTime) ||
	           (time >= cycleStart-cycleTime+contractStart && time < cycleStart-cycleTime+contractStart+contractTime);
	}
	
// Mutation:
	public void mutate(MutationParameters params) {
		
		Random r = new Random();
		
		int rand = (int)(Math.random() * 6.0);
		switch(rand) {
			case(0): strength += r.nextGaussian() * params.stdDeviation * STRENGTH_MAX;
					 strength = Math.min(STRENGTH_MAX, Math.max(0.0, strength));
					 break;
			case(1): cycleTime += r.nextGaussian() * params.stdDeviation * CYCLE_TIME_MAX;
					 cycleTime = Math.min(CYCLE_TIME_MAX, Math.max(0.0, cycleTime));
					 break;
			case(2): contractStart += r.nextGaussian() * params.stdDeviation * cycleTime;
					 contractStart = Math.min(cycleTime, Math.max(0.0, contractStart));
			 	 	 break;
			case(3): contractTime += r.nextGaussian() * params.stdDeviation * cycleTime;
					 contractTime = Math.min(cycleTime, Math.max(0.0, contractTime));
			 		 break;
			case(4): lenMin += r.nextGaussian() * params.stdDeviation * (lenMax-LEN_MIN);
					 lenMin = Math.min(lenMax, Math.max(LEN_MIN, lenMin));
					 break;
			case(5): lenMax += r.nextGaussian() * params.stdDeviation * (LEN_MAX-lenMin);
					 lenMax = Math.min(LEN_MAX, Math.max(lenMin, lenMax));
			 		 break;
		}
	}
}
