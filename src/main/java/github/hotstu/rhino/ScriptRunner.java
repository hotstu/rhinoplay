package github.hotstu.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    private static ScriptRunner sInstance;
    private final Context context;
    private final Scriptable scope;
    private final ArrayBlockingQueue<ScirptTask> mQuene;
    private boolean mStoped = false;

    private ScriptRunner() {
        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
        context = Context.enter();
        Global global = new Global(context) {

        };
        // Initialize the standard objects (Object, Function, etc.)
        // This must be done before scripts can be executed. Returns
        // a scope object that we use in later calls.
        scope = context.initStandardObjects(global);
        mQuene = new ArrayBlockingQueue<ScirptTask>(10);
    }

    public synchronized void start() {
        this.mStoped = false;
        while (!mStoped) {
            final ScirptTask poll;
            try {
                poll = mQuene.poll(300, TimeUnit.MILLISECONDS);
                if (poll != null) {
                    run(poll);
                    poll.sendback();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ScriptRunner Stopped");
    }

    public void shutDown() {
        this.mStoped = true;
    }

    public void addTask(ScirptTask task) {
        mQuene.add(task);
    }

    private void run(ScirptTask task) {
        // Now evaluate the string we've colected.
        System.out.println("run script in thread: " + Thread.currentThread().getId());
        Object result = null;
        try {
            result = context.evaluateString(scope, task.input, "<cmd>", 1, null);
            task.output = Context.toString(result);

        } catch (Exception e) {
            e.printStackTrace();
            task.err = e.getMessage();
        }
    }

    public static ScriptRunner getInstance() {
        if (sInstance == null) {
            synchronized (ScriptRunner.class) {
                if (sInstance == null) {
                    sInstance = new ScriptRunner();
                }
            }
        }
        return sInstance;
    }
}
