
public class Physics {

	public double time, timeStep, timeMax;
	public double gravity;
	public double airFriction;
	
	public Physics(double _time, double _timeStep, double _timeMax, double _gravity, double _airFriction) {
		
		time = _time;
		timeStep = _timeStep;
		timeMax = _timeMax;
		gravity = _gravity;
		airFriction = _airFriction;
	}
	
	public Physics(Physics other) {
		
		time = other.time;
		timeStep = other.timeStep;
		timeMax = other.timeMax;
		gravity = other.gravity;
		airFriction = other.airFriction;
	}
	
	void propagate() {
		
		if(time < timeMax) {
			time += timeStep;	
		}	
	}
}
