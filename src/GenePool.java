
public class GenePool {

	public Creature[] creatures;
	public Double[] fitness;
	
	private int nCreatures, generation;
	private Physics physics;
	private MutationParameters mp;
	
// Constructor:
	public GenePool(int _nCreatures, Physics _physics, MutationParameters _mp) {
		
		assert(_nCreatures%2 == 0);
		
		nCreatures = _nCreatures;
		physics = _physics;
		mp = _mp;
		
		creatures = new Creature[nCreatures];
		fitness = new Double[nCreatures];
	}
	
// Getter:
	public int getGeneration() {
		
		return generation;
	}
	
// Manipulation:
	public void randomInit(int nNodes, double sizeMax) {
		
		generation = 0;
		
		for(int i=0; i<nCreatures; i++) {
			creatures[i] = new Creature(new Physics(physics));
			creatures[i].randomize(nNodes, sizeMax);
		}
		
		computeFitness();
		sort();
	}
	
	public void newGeneration() {
		
		//killAndReplace();
		killAndBreed();
		evolve();
		computeFitness();
		sort();
		
		generation++;
	}
	
// Manipulation helpers:
	private void sort() {
		
		boolean sorted = false;
		while(!sorted) {			
			sorted = true;
			
			for(int i=0; i<nCreatures-1; i++) {
				if(fitness[i] < fitness[i+1]) {					
					swap(fitness,i,i+1);
					swap(creatures,i,i+1);
					sorted = false;
				}
			}
		}
	}
	
	private <E> void swap(E[] a, int i, int j) {
        if (i != j) {
            E tmp = a[i];
            a[i]  = a[j];
            a[j]  = tmp;
        }
    }
	
	private void computeFitness() {
		
		for(int i=0; i<nCreatures; i++) {
			
			Creature c = creatures[i];
			c.setToStart();
			
			c.physics.time = 0.0;
			while(c.physics.time < c.physics.timeMax &&
				  c.alive) {
				c.move();
			}
			
			if(c.alive) {
				fitness[i] = c.getCenter().getX();
			} else {
				fitness[i] = -Double.MAX_VALUE;
			}			
		}
	}
	
	private void killAndReplace() {
		
		for(int i=0; i<nCreatures/2; i++) {
			
			int other = nCreatures-i-1;
			
			if(creatures[i].alive) {
				
				double chanceToWin;
				if(creatures[other].alive) {
					chanceToWin = (nCreatures-i) / nCreatures;
				} else {
					chanceToWin = 1.0;
				}
				
				int winner, loser;
				if(Math.random() < chanceToWin) {
					winner = i;
					loser = other;
				} else {
					winner = other;
					loser = i;
				}
				
				if(Math.random() < mp.random) {
					creatures[loser] = new Creature(new Physics(physics));
				} else {
					creatures[loser] = new Creature(creatures[winner]);
				}
			} else {
				
				creatures[i] = new Creature(new Physics(physics));
				creatures[other] = new Creature(new Physics(physics));
			}		
		}
	}
	
	private void killAndBreed() {
		
		for(int i=0; i<nCreatures/2; i++) {
		
			int parent1 = i;
			int parent2 = (int)(Math.random() * (nCreatures/2));
			int child = nCreatures/2 + i;
			
			if(Math.random() < mp.random) {
				creatures[child] = new Creature(new Physics(physics));
			} else {				
				if(!creatures[parent1].alive && !creatures[parent1].alive) {				
					creatures[child] = new Creature(new Physics(physics));
				} else if(!creatures[parent1].alive) {
					creatures[child] = new Creature(creatures[parent2]);
				} else if(!creatures[parent2].alive) {
					creatures[child] = new Creature(creatures[parent1]);
				} else { // both alive					
					Creature p1 = creatures[parent1];
					Creature p2 = creatures[parent2];
					
					if(Math.random() < 0.5) {
						p1 = creatures[parent2];
						p2 = creatures[parent1];
					}
					
					creatures[child] = breedChild(p1, p2);
				}
			}
		}
	}
	
	private Creature breedChild(Creature parent1, Creature parent2) {
		
		Creature child = new Creature(parent1);
		
		int minNumNodes = Math.min(parent1.nodes.size(), parent2.nodes.size());
		for(int i=0; i<minNumNodes; i++) {
			
			if(Math.random() < 0.5) {
				Node childNode  = child.nodes.get(i);
				Node parentNode = parent2.nodes.get(i);
				
				childNode.copy(parentNode);
			}
		}
		
		int minNumMuscles = Math.min(parent1.muscles.size(), parent2.muscles.size());
		for(int i=0; i<minNumMuscles; i++) {
			
			if(Math.random() < 0.5) {
				Muscle childMuscle  = child.muscles.get(i);
				Muscle parentMuscle = parent2.muscles.get(i);
				
				childMuscle.copy(parentMuscle);
			}
		}
		
		return child;
	}
	
	private void evolve() {
		
		for(int i=0; i<nCreatures; i++) {
			if(creatures[i].alive) {
				creatures[i].mutate(mp);
			}
		}
	}
}
