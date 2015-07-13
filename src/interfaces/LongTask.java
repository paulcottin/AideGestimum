package interfaces;

public interface LongTask {
	
	public boolean isRunning();
	public void setRunning(boolean b);
	public void onDispose();
}
