
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
		
		killAndReplace();
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
			
			//double chanceToWin = (fitness[i]>fitness[nCreatures-i-1]) ? 1.0 : 0.0; // fitness[i] / (fitness[i] + fitness[nCreatures-i-1]);
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
			
			//int loser = nCreatures-i-1;
			if(Math.random() < mp.random) {
				creatures[loser] = new Creature(new Physics(physics));
			} else {
				creatures[loser] = new Creature(creatures[winner]);
			}
		}
	}
	
	private void evolve() {
		
		for(int i=0; i<nCreatures; i++) {
			if(creatures[i].alive) {
				creatures[i].mutate(mp);
			}
		}
	}
}
