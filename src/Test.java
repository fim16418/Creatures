
public class Test {
	
	public static void main(String[] args) {
		
		Physics physics = new Physics(0.0, 0.1, 20.0, 10.0, 0.0005);
		MutationParameters mp = new MutationParameters(0.15, 0.25, 0.1, 0.01, 0.1, 0.01, 0.1);
		
		int nCreatures = 1000;
		int nGenerations = 100;
		
		GenePool pool = new GenePool(nCreatures, physics, mp);
		pool.randomInit(3, 10.0);
		
		for(int i=0; i<nGenerations; i++) {
			pool.newGeneration();
			System.out.println("Generation = "+pool.getGeneration()+
							   ", Fitness best = "+pool.fitness[0]);		
		}
		
		System.out.println("Fitness best = "+pool.fitness[0]);
		System.out.println("Fitness worst = "+pool.fitness[nCreatures-1]);
		
		PreviewFrame frame1 = new PreviewFrame(pool.creatures[0]);
		frame1.setLocation(400, 200);
		frame1.startAnimation();
		
		PreviewFrame frame2 = new PreviewFrame(pool.creatures[nCreatures/2]);
		frame2.setLocation(800, 200);
		frame2.startAnimation();
		
		PreviewFrame frame3 = new PreviewFrame(pool.creatures[nCreatures-1]);
		frame3.setLocation(1200, 200);
		frame3.startAnimation();
	}
}