
public class Test {
	
	public static void main(String[] args) {
		
		Physics physics = new Physics(0.0, 0.1, 50.0, 10.0, 0.0005);
		MutationParameters mp = new MutationParameters(0.15, 0.25, 0.1, 0.01, 0.05, 0.0025, 0.1);
		
		int nCreatures = 1000;
		int nGenerations = 100;
		
		GenePool pool = new GenePool(nCreatures, physics, mp);
		pool.randomInit(3, 10.0);
		
		for(int i=0; i<nGenerations; i++) {
			pool.newGeneration();
			System.out.println("Generation = "+pool.getGeneration()+
							   ", Fitness best = "+pool.fitness[0].intValue());		
		}
		
		System.out.println("Fitness best = "+pool.fitness[0].intValue());
		System.out.println("Fitness worst = "+pool.fitness[nCreatures-1].intValue());
		
		PreviewFrame frame1 = new PreviewFrame(pool.creatures[0]);
		frame1.setLocation(400, 50);
		frame1.startAnimation();
		
		PreviewFrame frame2 = new PreviewFrame(pool.creatures[1]);
		frame2.setLocation(800, 50);
		frame2.startAnimation();
		
		PreviewFrame frame3 = new PreviewFrame(pool.creatures[2]);
		frame3.setLocation(1200, 50);
		frame3.startAnimation();
		
		PreviewFrame frame4 = new PreviewFrame(pool.creatures[nCreatures/2-1]);
		frame4.setLocation(400, 400);
		frame4.startAnimation();
		
		PreviewFrame frame5 = new PreviewFrame(pool.creatures[nCreatures/2]);
		frame5.setLocation(800, 400);
		frame5.startAnimation();
		
		PreviewFrame frame6 = new PreviewFrame(pool.creatures[nCreatures/2+1]);
		frame6.setLocation(1200, 400);
		frame6.startAnimation();
		
		PreviewFrame frame7 = new PreviewFrame(pool.creatures[nCreatures-3]);
		frame7.setLocation(400, 750);
		frame7.startAnimation();
		
		PreviewFrame frame8 = new PreviewFrame(pool.creatures[nCreatures-2]);
		frame8.setLocation(800, 750);
		frame8.startAnimation();
		
		PreviewFrame frame9 = new PreviewFrame(pool.creatures[nCreatures-1]);
		frame9.setLocation(1200, 750);
		frame9.startAnimation();
	}
}