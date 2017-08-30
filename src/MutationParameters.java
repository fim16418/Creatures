
public class MutationParameters {
	
	// Chances of replacing a killed creature by a new random creature
	// (Otherwise it is replaced by a copy of an existing creature)
	public double random;
	
	// Chances of mutating a node or muscle
	// (Evaluated separately for each node/muscle)
	public double mutate;
	
	// Chances of creating a new muscle
	// (Evaluated once per creature)
	public double newMuscle;
	
	// Chances of creating a new node
	// (Evaluated once per creature)
	public double newNode;
	
	// Chances of removing a muscle
	public double removeMuscle;
	
	// Chances of removing a node
	// (And its connected muscles)
	public double removeNode;
	
	// Standard deviation for gaussian distribution
	public double stdDeviation;
		
// Constructor:
	public MutationParameters(double _random, double _mutate,
							  double _newMuscle, double _newNode,
							  double _removeMuscle, double _removeNode,
							  double _stdDeviation) {
		
		random = _random;
		mutate = _mutate;
		newMuscle = _newMuscle;
		newNode = _newNode;
		removeMuscle = _removeMuscle;
		removeNode = _removeNode;
		stdDeviation = _stdDeviation;
	}
}