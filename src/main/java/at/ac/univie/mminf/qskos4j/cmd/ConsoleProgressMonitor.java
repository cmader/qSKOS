package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleProgressMonitor implements IProgressMonitor {

	private final Logger logger = LoggerFactory.getLogger(ConsoleProgressMonitor.class);
	private final int PROG_BAR_WIDTH = 50;
	
	private float prevProgressValue;
	
	@Override
	public void onUpdateProgress(float progress) {
		if (progress > .999) {
			clearConsoleBar();
		}
		else if (progress - prevProgressValue > .01) {
			int percentage = Math.round(progress * 100);			
			System.out.print("|" +createBar(percentage)+ "|" +percentage+ "%\r");
			
			prevProgressValue = progress;
		}
	}
	
	private void clearConsoleBar() {
		System.out.print(produceBlanks(PROG_BAR_WIDTH + 10) + "\r");		
	}
	
	private String createBar(int percentage) {
		int numSegments = (int) Math.round((double) (PROG_BAR_WIDTH * percentage) / (double) 100);
		
		String bar = "";
		for (int i = 0; i < numSegments; i++) {
			bar += "=";
		}
		bar += produceBlanks(PROG_BAR_WIDTH - numSegments);
				
		return bar;
	}
	
	private String produceBlanks(int numBlanks) {
		return new String(new char[numBlanks]).replace('\0', ' ');
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
