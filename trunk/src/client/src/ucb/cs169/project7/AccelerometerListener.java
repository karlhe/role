package ucb.cs169.project7;

public interface AccelerometerListener {

	public void onAccelerationChanged(float x, float y, float z);
	
	public void onShake(float force);
	
}
