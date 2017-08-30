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
	
	public Creature(Creature other) {
		
		physics = new Physics(other.physics);
		alive = other.alive;
		
		Iterator<Muscle> iteratorM = other.muscles.iterator();
		while (iteratorM.hasNext()) {			
			Muscle otherMuscle = iteratorM.next();
			
			Muscle muscle = new Muscle(otherMuscle);
			this.muscles.add(muscle);
		}
		
		Iterator<Node> iteratorN = other.nodes.iterator();
		while (iteratorN.hasNext()) {			
			Node otherNode = iteratorN.next();
			
			Node node = new Node(otherNode);
			this.nodes.add(node);
		}
		
		// Connect Muscles to Nodes:
		for(int i=0; i<other.muscles.size(); i++) {
			Muscle otherMuscle = other.muscles.get(i);
			Node otherNode0 = otherMuscle.getNode(0);
			Node otherNode1 = otherMuscle.getNode(1);
			
			for(int j=0; j<other.nodes.size(); j++) {
				Node tmpNode = other.nodes.get(j);
				
				if(tmpNode == otherNode0) {
					this.muscles.get(i).nodes[0] = this.nodes.get(j);
				} else if(tmpNode == otherNode1) {
					this.muscles.get(i).nodes[1] = this.nodes.get(j);
				}
			}
		}
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
			
			Node newNode = new Node();
			newNode.randomize();			
			nodes.add(newNode);
		}
		
		for(int i=0; i<nNodes; i++) {
			
	        Node node0 = nodes.get(i);
	        Node node1 = nodes.get((i+1)%nNodes);
	        
	        Muscle newMuscle = new Muscle(node0, node1);
	        newMuscle.randomize();
	        muscles.add(newMuscle);
	    }
		
		alive = checkIfAlive();
	}
	
// Controllers:
	public void setToStart() {
		
		physics.time = 0.0;
		
		double minY = Double.MAX_VALUE;
	    double radius = 0.0;
	    double centerX = 0.0;
	    
	    Iterator<Node> iterator = nodes.iterator();
		while (iterator.hasNext()) {			
			Node node = iterator.next();
			
			node.setToStart();
			
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
		
		physics.propagate();
		
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
	
// Mutate:
	public void mutate(MutationParameters params) {
		
		Iterator<Muscle> iteratorM = muscles.iterator();
		while (iteratorM.hasNext()) {			
			Muscle muscle = iteratorM.next();
			
			if(Math.random() < params.mutate) {			
				muscle.mutate(params);
			}
		}
		
		Iterator<Node> iteratorN = nodes.iterator();
		while (iteratorN.hasNext()) {			
			Node node = iteratorN.next();
			
			if(Math.random() < params.mutate) {				
				node.mutate(params);
			}
		}
		
		if(Math.random() < params.newMuscle) {			
			addMuscle();
		}
		
		if(Math.random() < params.newNode) {			
			addNode();
		}
		
		if(Math.random() < params.removeMuscle) {
			removeMuscle();
		}
		
		if(Math.random() < params.removeNode) {
			removeNode();
		}
		
		checkIfAlive();
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
				node.vel.data.y = Math.max(0.0, node.vel.data.y);
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
			acceleration *= -physics.airFriction / node.mass;
			
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
	
	private void addMuscle() {
		
		// Choose nodes:
		int nNodes = nodes.size();
		int node0 = (int)(Math.random() * nNodes);
		int node1 = (int)(Math.random() * (nNodes-1));
		if(node0 == node1) {
			node1 = nNodes-1;
		}
		
		Node n0 = nodes.get(node0);
		Node n1 = nodes.get(node1);
		
		// Check if muscle already exists:
		Iterator<Muscle> iteratorM = muscles.iterator();
		while (iteratorM.hasNext()) {			
			Muscle muscle = iteratorM.next();
							
			if((muscle.nodes[0] == n0 && muscle.nodes[1] == n1) ||
			   (muscle.nodes[0] == n1 && muscle.nodes[1] == n0)) {
					return;
			}
		}
		
		addMuscleTo(n0, n1);
	}
	
	private void addMuscleTo(Node n0, Node n1) {
		
		Muscle newMuscle = new Muscle(n0, n1);
		newMuscle.randomize();
		muscles.add(newMuscle);
	}
	
	private void addNode() {
		
		Node newNode = new Node();
		newNode.randomize();
				
		int nNodes = nodes.size();
		int node0 = (int)(Math.random() * nNodes);
		int node1 = (int)(Math.random() * (nNodes-1));
		if(node0 == node1) {
			node1 = nNodes-1;
		}
		
		Node n0 = nodes.get(node0);
		Node n1 = nodes.get(node1);
		
		addMuscleTo(newNode, n0);
		addMuscleTo(newNode, n1);
		nodes.add(newNode);		
	}
	
	private void removeMuscle() {
		
		int nMuscles = muscles.size();
		int rand = (int)(Math.random() * nMuscles);
		muscles.remove(rand);
	}
	
	private void removeNode() {
		
		int nNodes = nodes.size();
		int rand = (int)(Math.random() * nNodes);
		Node node = nodes.get(rand);
		
		ArrayList<Muscle> deleteList = new ArrayList<Muscle>();
		
		Iterator<Muscle> iteratorM = muscles.iterator();
		while (iteratorM.hasNext()) {			
			Muscle muscle = iteratorM.next();
			
			if(muscle.nodes[0] == node || muscle.nodes[1] == node) {
				deleteList.add(muscle);
			}
		}
		
		Iterator<Muscle> iterator = deleteList.iterator();
		while(iterator.hasNext()) {
			Muscle m = iterator.next();		
			muscles.remove(m);
		}
		
		nodes.remove(rand);
	}
}
