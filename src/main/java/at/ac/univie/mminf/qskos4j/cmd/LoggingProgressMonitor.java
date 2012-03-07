package at.ac.univie.mminf.qskos4j.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;

public class LoggingProgressMonitor implements IProgressMonitor {

	private final Logger logger = LoggerFactory.getLogger(LoggingProgressMonitor.class);
	
	float prevProgressValue;
	
	@Override
	public void onUpdateProgress(float progress) {
		if (progress - prevProgressValue > .01) {
			logger.info("new value: " +Math.round(progress * 100)+ "%");
			prevProgressValue = progress;
		}
	}

	@Override
	public void setTaskDescription(String description) {
		logger.info(description);
	}
	
	@Override
	public void reset() {
		prevProgressValue = 0;
	}

}
