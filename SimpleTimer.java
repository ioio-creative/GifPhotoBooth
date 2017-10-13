// https://www.youtube.com/watch?v=kYdA_Svo6uw
public class SimpleTimer {
    private long lastCheckTime;
    private long timeInterval;

    public SimpleTimer(long aTimeInterval) {
        reset(aTimeInterval);
    }

    public SimpleTimer(long aStartTime, long aTimeInterval) {
        reset(aStartTime, aTimeInterval);
    }

    public boolean check() {
        boolean isTimeTicked = false;
        if (System.currentTimeMillis() > lastCheckTime + timeInterval) {
            lastCheckTime = System.currentTimeMillis();
            isTimeTicked = true;
        }
        return isTimeTicked;
    }

    public void reset() {
        restart();
    }

    public void reset(long aTimeInterval) {
        timeInterval = aTimeInterval;
        restart();
    }

    public void reset(long aStartTime, long aTimeInterval) {
        lastCheckTime = aStartTime;
        timeInterval = aTimeInterval; 
    }

    private void restart() {
        lastCheckTime = System.currentTimeMillis();
    }
}