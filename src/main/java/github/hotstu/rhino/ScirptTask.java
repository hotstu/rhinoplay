package github.hotstu.rhino;

import java.util.concurrent.CountDownLatch;

public class ScirptTask {
    public int what = 0;
    public final String input;
    private final CountDownLatch lock;
    public String output = null;
    public String err = null;


    public ScirptTask(String input) {
        this.input = input;
        this.lock = new CountDownLatch(1);
    }

    public void await(long timeout) {
        try {
            lock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendback() {
        lock.countDown();
    }
}
