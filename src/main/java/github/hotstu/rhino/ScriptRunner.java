package github.hotstu.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.tools.shell.Global;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ScriptRunner {
    private static ScriptRunner sInstance;
    private final Context context;
    private  Scriptable scope;
    private final ArrayBlockingQueue<ScirptTask> mQuene;
    private boolean mStoped = false;
    private  Global global;

    private ScriptRunner() {
        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
        context = Context.enter();
        global = new Global(context);
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

    public void resetScope() {
        ScirptTask task = new ScirptTask(null);
        task.what = 1;
        addTask(task);
    }

    private void run(ScirptTask task) {
        if (task.what == 1) {
            global = new Global(context);
            scope = context.initStandardObjects(global);
            return;
        }
        // Now evaluate the string we've colected.
        System.out.println("run script in thread: " + Thread.currentThread().getId());
        Object result = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            global.setOut(new PrintStream(out));
            result = context.evaluateString(scope, task.input, "<cmd>", 1, null);
            if (result == null || result == Undefined.instance || result == Undefined.SCRIPTABLE_UNDEFINED) {
                if (out.size() > 0) {
                    final String outResult = new String(out.toByteArray());
                    task.output = Context.toString(outResult);
                } else {
                    task.output = Context.toString(result);
                }
            } else {
                task.output = Context.toString(result);
            }
            global.setOut(null);
            out.close();

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
