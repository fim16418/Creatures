import java.util.*;

public class Node {
	public XYPoint pos, vel;
	public XYPoint startPos;
	
	public double friction;
	public double mass, radius;
	
	private boolean onGround;
	
	final static double MASS_MAX = 10.0;
	final static double RADIUS_MAX = 10.0;
	
// Constructors:
	public Node() {}
	
	public Node(XYPoint _pos, XYPoint _vel,
			    double _friction, double _mass, double _radius) {
		
		pos = _pos;
		startPos = new XYPoint(_pos);
		vel = _vel;
		friction = _friction;
		mass = _mass;
		radius = _radius;
		
		//assert(pos.getY() >= radius);
		onGround = pos.getY() <= radius;
	}
	
	public Node(Node other) {
		
		pos = new XYPoint(other.pos);
		vel = new XYPoint(other.vel);
		startPos = new XYPoint(other.startPos);
		friction = other.friction;
		mass = other.mass;
		radius = other.radius;
		onGround = other.onGround;
	}
	
// Manipulation:
	public void randomize() {
		
		pos = new XYPoint(Math.random() * Creature.INITIAL_SIZE_MAX.getX(),
				  		  Math.random() * Creature.INITIAL_SIZE_MAX.getY());
		startPos = new XYPoint(pos);
		vel = new XYPoint(Math.random(), Math.random());
		friction = Math.random();
		mass = Math.random() * MASS_MAX;
		radius  = Math.random() * RADIUS_MAX;
		
		onGround = pos.getY() <= radius;
	}
	
	public void copy(Node other) {
		
		pos.setLocation(other.pos);
		vel.setLocation(other.vel);
		startPos.setLocation(other.startPos);
		friction = other.friction;
		mass = other.mass;
		radius = other.radius;
		onGround = other.onGround;
	}
	
// Getter:
	public boolean isOnGround() {
		
		return onGround;
	}
	
// Controllers:
	public void setToStart() {
		
		pos = new XYPoint(startPos);
		vel = new XYPoint(0.0, 0.0);
		onGround = pos.getY() <= radius;
	}
	
	public void moveBy(XYPoint delta) {
		
		pos.add(delta);
		
		onGround = pos.getY() <= radius;
		if(onGround) {
			pos.setY(2*radius - pos.getY());
			vel.setY(0.5 * Math.abs(vel.getY()));
		}
	}
	
// Mutation:
	public void mutate(MutationParameters params) {
		
		Random r = new Random();
		
		int rand = (int)(Math.random() * 4.0);
		switch(rand) {
			case(0): startPos = new XYPoint(startPos.getX() + r.nextGaussian() * params.stdDeviation * Creature.SIZE_MAX.getX(),
										    startPos.getY() + r.nextGaussian() * params.stdDeviation * Creature.SIZE_MAX.getY());
					 break;
			case(1): friction += r.nextGaussian() * params.stdDeviation;
					 friction = Math.min(1.0, Math.max(0.0, friction));
					 break;
			case(2): mass += r.nextGaussian() * params.stdDeviation * MASS_MAX;
					 mass = Math.min(MASS_MAX, Math.max(0.0, mass));
			 	 	 break;
			case(3): radius += r.nextGaussian() * params.stdDeviation * RADIUS_MAX;
					 radius = Math.min(RADIUS_MAX, Math.max(0.0, radius));
			 		 break;
		}
	}
}
