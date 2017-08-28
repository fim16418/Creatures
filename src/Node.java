
public class Node {
	public XYPoint pos, vel;
	
	public double friction;
	public double mass, radius;
	
	private boolean onGround;
	
	final static double MASS_MAX = 10.0;
	final static double RADIUS_MAX = 10.0;
	
	public Node(XYPoint _pos, XYPoint _vel,
			    double _friction, double _mass, double _radius) {
		
		pos = _pos;
		vel = _vel;
		friction = _friction;
		mass = _mass;
		radius = _radius;
		
		assert(pos.getY() >= radius);
		onGround = pos.getY() <= radius;
	}
	
	public boolean isOnGround() {
		
		return onGround;
	}
	
	public void moveBy(XYPoint delta) {
		
		pos.add(delta);
		
		onGround = pos.getY() <= radius;
		if(onGround) {
			pos.setY(2*radius - pos.getY());
			vel.setY(0.5 * Math.abs(vel.getY()));
		}
	}
}
