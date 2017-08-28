import java.util.*;

public class Creature {
	
	public Physics physics;

	public ArrayList<Muscle> muscles = new ArrayList<Muscle>();
	public ArrayList<Node>   nodes   = new ArrayList<Node>();
	
	boolean alive;
	
	final static XYPoint SIZE_MAX = new XYPoint(300.0, 300.0);
	final static XYPoint INITIAL_SIZE_MAX = new XYPoint(100.0, 100.0);
	
// Constructors:
	public Creature(Physics _physics) {
		
		physics = _physics;
		alive = false;
	}
	
// Get:
	public XYPoint getCenter() {
		
		XYPoint center = new XYPoint(0.0, 0.0);
		
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {
			XYPoint tmp = iterator.next().pos;
			center.add(tmp);
		}
		
		int n = nodes.size();
		return center.dividedByScalar(n);
	}
	
	public XYPoint getSize() {
		
		XYPoint min = getCenter();
		XYPoint max = new XYPoint(min);
		
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			XYPoint pos = iterator.next().pos;
			
			if(pos.getX() < min.getX()) {
				min.setX(pos.getX());
			} else if(pos.getX() > max.getX()) {
				max.setX(pos.getX());
			}
			if(pos.getY() < min.getY()) {
				min.setY(pos.getY());
			} else if(pos.getY() > max.getY()) {
				max.setY(pos.getY());
			}
		}
		
		return max.subtracted(min);
	}
	
// Initializers:
	public void randomize(int nNodes, double sizeMax) {
		
		nodes.clear();
		muscles.clear();
		
		for(int i=0; i<nNodes; i++) {
			
			XYPoint pos = new XYPoint(Math.random() * INITIAL_SIZE_MAX.getX(),
									  Math.random() * INITIAL_SIZE_MAX.getY());
			XYPoint vel = new XYPoint(Math.random(), Math.random());
			double fric = Math.random();
			double mass = Math.random() * Node.MASS_MAX;
			double rad  = Math.random() * Node.RADIUS_MAX;
			
			nodes.add(new Node(pos,vel,fric,mass,rad));
		}
		
		for(int i=0; i<nNodes; i++) {
			
	        double stren = Math.random() * Muscle.STRENGTH_MAX;
	        double cycleT = Math.random() * Muscle.CYCLE_TIME_MAX;
	        double contractS = Math.random() * cycleT;
	        double contractT = Math.random() * cycleT;
	        double lenMax = Math.random() * Muscle.LEN_MAX;
	        double lenMin = Math.random() * (lenMax-Muscle.LEN_MIN) + Muscle.LEN_MIN;

	        Node node1 = nodes.get(i);
	        Node node2 = nodes.get((i+1)%nNodes);
	        
	        muscles.add(new Muscle(node1, node2, stren,
	        					   cycleT, contractS, contractT,
	        					   lenMin, lenMax));
	    }
		
		alive = checkIfAlive();
	}
	
// Controllers:
	public void setToStart() {
		
		double minY = 9999999;
	    double radius = 0.0;
	    double centerX = 0.0;
	    
	    Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			Node node = iterator.next();
			
			if(node.pos.getY() < minY) {
				minY = node.pos.getY();
				radius = node.radius;
			}
			centerX += node.pos.getX();
			
			node.vel.setLocation(0.0, 0.0);
		}

	    centerX /= nodes.size();
	    
	    iterator = nodes.iterator();
	    while (iterator.hasNext()) {			
			XYPoint pos = iterator.next().pos;
			
			XYPoint subtraction = new XYPoint(centerX, minY-radius);
			pos.subtract(subtraction);
	    }
	}
	
	public void move() {
		
		if(!alive) return;
		
		updateVel();
		applyGravity();
		applyAirFriction();
		
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			Node node = iterator.next();
			
			node.moveBy(node.vel.multipliedByScalar(physics.timeStep)); // dx = v * dt
		}
		
		alive = checkIfAlive();
	}
	
// Helper:
	private void updateVel() {
		
		Iterator<Muscle> iteratorM = muscles.iterator();
		while (iteratorM.hasNext()) {			
			Muscle muscle = iteratorM.next();
			
			XYPoint forceOnNode0 = new XYPoint();
			XYPoint forceOnNode1 = new XYPoint();
			muscle.getForces(forceOnNode0, forceOnNode1, physics.time);
			
			Node node = muscle.getNode(0);
			XYPoint deltaVel = forceOnNode0.dividedByScalar(node.mass);
			deltaVel.multiplyByScalar(physics.timeStep);
			node.vel.add(deltaVel); // v += F/m * dt
			
			node = muscle.getNode(1);
			deltaVel = forceOnNode1.dividedByScalar(node.mass);
			deltaVel.multiplyByScalar(physics.timeStep);
			node.vel.add(deltaVel); // v += F/m * dt
		}
		
		// Handle contact with ground:
		Iterator<Node> iteratorN = nodes.iterator();
		while (iteratorN.hasNext()) {			
			Node node = iteratorN.next();
			
			if(node.isOnGround()) {
				node.vel.data.x *= 1 - node.friction;
				node.vel.data.y = 0.0;
			}
		}
	}
	
	private void applyGravity() {
		
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			Node node = iterator.next();
			
			if(!node.isOnGround()) {
				node.vel.add(new XYPoint(0.0, -physics.gravity * physics.timeStep));
			}
		}
	}
	
	private void applyAirFriction() {
		
		Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			Node node = iterator.next();
			
			double vx = node.vel.getX();
			double vy = node.vel.getY();
			
			double acceleration = vx*vx + vy*vy;
			acceleration *= -node.friction / node.mass;
			
			double angle = Math.atan2(vy, vx);
			
			int dir0 = (vx > 0) ? 1 : -1;
	        int dir1 = (vy > 0) ? 1 : -1;
			
			XYPoint deltaVel = new XYPoint(acceleration * Math.cos(angle) * physics.timeStep,
										   acceleration * Math.sin(angle) * physics.timeStep);
			node.vel.add(deltaVel);
			
			// Air friction cannot invert movement:
			vx = node.vel.getX();
			vy = node.vel.getY();
	        if(vx * dir0 < 0) node.vel.setX(0.0);
	        if(vy * dir1 < 0) node.vel.setY(0.0);
		}
	}
	
	private boolean checkIfAlive() {
		
		XYPoint size = getSize();		
		return (size.getX() <= SIZE_MAX.getX() && size.getY() <= SIZE_MAX.getY());
	}
}
