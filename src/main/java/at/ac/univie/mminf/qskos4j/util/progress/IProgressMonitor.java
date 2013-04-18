package at.ac.univie.mminf.qskos4j.util.progress;

public interface IProgressMonitor {

	/**
	 * Called if a task's progress changes
	 * @param progress value between 0 and 1
	 */
	public void onUpdateProgress(float progress);
	
	/**
	 * Textual description for the currently performed task
	 * @param description
	 */
	public void setTaskDescription(String description);
	
	/**
	 * Resets the internal state
	 */
	public void reset();

    public void onFinish();
	
}
