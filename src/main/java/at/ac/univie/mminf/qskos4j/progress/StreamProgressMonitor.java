package at.ac.univie.mminf.qskos4j.progress;

public class StreamProgressMonitor implements IProgressMonitor {

    private int prevPercentage = 0, prevTenPercentage = 0;

    @Override
    public void onUpdateProgress(float progress) {
        int percentage = Math.round(progress * 100);
        int tenPercentage = (int) Math.floor(progress * 10);
        if (percentage > prevPercentage) {
            if (tenPercentage > prevTenPercentage) {
                System.out.print(tenPercentage * 10 + "%");
                prevTenPercentage = tenPercentage;
            }
            else {
                System.out.print(".");
            }
            prevPercentage = percentage;
        }
    }

    @Override
    public void setTaskDescription(String description) {
        System.out.println(description);
    }

    @Override
    public void reset() {
        prevPercentage = 0;
    }

    @Override
    public void onFinish() {
        System.out.println("done");
    }

}
